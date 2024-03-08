package com.edw.helper;

import org.infinispan.Cache;
import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.api.CacheContainerAdmin;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.transaction.TransactionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.TransactionManager;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <pre>
 *     com.edw.helper.GenerateCacheHelper
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 29 Jan 2024 15:56
 */
@Service
public class GenerateCacheHelper {

    @Autowired
    private RemoteCacheManager cacheManager;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    private List<String> listOfUuid = new ArrayList<>();

    private static final int NUM_EXECUTORS = 100;
    private static final int NUM_ENTRIES = 5000;

    final private Map<String, BigDecimal> concurrentHashMap = new ConcurrentHashMap<>(NUM_ENTRIES, 0.9f, 1);

    private CountDownLatch latch;

    @PostConstruct
    public void prepareData() {
        for (int i = 0; i < NUM_ENTRIES; i++) {
            listOfUuid.add(UUID.randomUUID().toString());
        }
    }

    public void generate() {
        logger.info("starting ====================");
        final RemoteCache cache = cacheManager.getCache("balance");

        for(int j = 0 ; j < NUM_EXECUTORS; j ++) {
            executor.execute(() -> {
                for (int i = 0; i < NUM_ENTRIES; i ++) {
                    while (true) {
                        Long timestamp = System.currentTimeMillis();
                        MetadataValue metadataValue = cache.getWithMetadata(listOfUuid.get(i));
                        BigDecimal newValue =
                                ((BigDecimal) metadataValue.getValue()).add(new BigDecimal(1000));
                        Boolean success = cache.replaceWithVersion(listOfUuid.get(i), newValue, metadataValue.getVersion());

                        if (success)
                            break;
                        else {
                            logger.info("retrying {} for {}",
                                                listOfUuid.get(i),
                                                System.currentTimeMillis() - timestamp);
                        }
                    }
                }
            });
        }

        logger.info("done ====================");
    }

    public List<String> query() {
        List<String> list = new ArrayList<>();
        final RemoteCache cache = cacheManager.getCache("balance");
        for (String uuid : listOfUuid) {
            BigDecimal bigDecimal = (BigDecimal) cache.get(uuid);
            if(!bigDecimal.equals(new BigDecimal(100_000)))
                list.add(uuid);
        }
        return list;
    }

    public void initiate() {
        final RemoteCache cache = cacheManager.getCache("balance");

        for (String uuid : listOfUuid) {
            cache.putIfAbsent(uuid, new BigDecimal(0));
            concurrentHashMap.put(uuid,  new BigDecimal(0));
        }
    }

    public void generateConcurrentButSequential() {
        List<List<Bean>> listOfUuidBeans = new ArrayList<>();
        for (String uuid : listOfUuid) {
            List<Bean> listOfBeans = new ArrayList<>();
            for (int i = 0 ; i < NUM_EXECUTORS; i ++) {
                listOfBeans.add(new Bean(uuid, new BigDecimal(1000)));
            }
            listOfUuidBeans.add(listOfBeans);
        }


        logger.info("begin ====================");

        final RemoteCache cache = cacheManager.getCache("balance");
        latch = new CountDownLatch(NUM_ENTRIES);

        int CHUNK_SIZE = 200;

        // split into CHUNK_SIZE threads
        AtomicInteger counter = new AtomicInteger();
        Collection<List<List<Bean>>> partitionedList = listOfUuidBeans.stream()
                                                                    .collect(Collectors.groupingBy(i -> counter.getAndIncrement() / CHUNK_SIZE))
                                                                    .values();

        for (List<List<Bean>> partitioned : partitionedList) {
            CountDownLatch privateLatch = new CountDownLatch(CHUNK_SIZE);;
            for(List<Bean> beans : partitioned) {
                executor.execute(() -> {
                    logger.info("processing {} ", beans.get(0).getKey());
                    for (Bean bean : beans) {
                        while (true) {
                            Long timestamp = System.currentTimeMillis();
                            MetadataValue metadataValue = cache.getWithMetadata(bean.getKey());
                            BigDecimal newValue = (bean.getValue().add(new BigDecimal("" + metadataValue.getValue())));
                            Boolean success = cache.replaceWithVersion(bean.getKey(), newValue, metadataValue.getVersion());

                            if (success) {
                                logger.info("successfully processing {} - {}", bean.getKey(), bean.getValue());
                                break;
                            }
                            else {
                                logger.info("{} retry {} version is {} for {}", success, bean.getKey(),
                                        metadataValue.getVersion(),
                                        System.currentTimeMillis() - timestamp);
                            }
                        }
                    }

                    privateLatch.countDown();
                    latch.countDown();
                });
            }

            try {
                privateLatch.await();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        try {
            latch.await();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        logger.info("done ====================");
    }

    class Bean {
        private String key;
        private BigDecimal value;

        public Bean() {
        }

        public Bean(String key, BigDecimal value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Bean{" +
                    "key='" + key + '\'' +
                    ", value=" + value +
                    '}';
        }
    }
}