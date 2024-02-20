package com.edw.helper;

import jakarta.annotation.PostConstruct;
import org.infinispan.Cache;
import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.transaction.TransactionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.TransactionManager;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

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

    @Autowired
    private EmbeddedCacheManager embeddedCacheManager;

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
        latch = new CountDownLatch(NUM_EXECUTORS);

        for (int j = 0; j < NUM_EXECUTORS; j++) {
            executor.execute(() -> {

                ArrayList<String> tempArrayList = new ArrayList(listOfUuid);
                Collections.shuffle(tempArrayList);

                for (String uuid : tempArrayList) {
                    BigDecimal oldValue;
                    BigDecimal newValue;
                    do {
                        oldValue = concurrentHashMap.get(uuid);
                        newValue = concurrentHashMap.get(uuid).add(new BigDecimal(1000));
                    } while(!concurrentHashMap.replace(uuid, oldValue, newValue));
                }

                latch.countDown();
            });
        }

        try {
            latch.await();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        final RemoteCache cache = cacheManager.getCache("balance");
        for(Map.Entry<String, BigDecimal> entry : concurrentHashMap.entrySet()) {
            while (true) {
                Long timestamp = System.currentTimeMillis();
                MetadataValue metadataValue = cache.getWithMetadata(entry.getKey());
                BigDecimal newValue = entry.getValue();
                Boolean success = cache.replaceWithVersion(entry.getKey(), newValue, metadataValue.getVersion());

                if (success) {
                    logger.info("successfully processing {} - {}", entry.getKey(), entry.getValue());
                    break;
                }
                else {
                    logger.info("{} retry {} version is {} for {}", success, entry.getKey(),
                            metadataValue.getVersion(),
                            System.currentTimeMillis() - timestamp);
                    try {
                        Thread.sleep(new Random().nextInt(100, 1000));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        logger.info("done ====================");
    }

    public void generate2() {
        logger.info("starting ====================");

        final Cache embedCache = embeddedCacheManager.getCache("temp-cache");
        latch = new CountDownLatch(NUM_EXECUTORS);

        for (int j = 0; j < NUM_EXECUTORS; j++) {
            executor.execute(() -> {

                ArrayList<String> tempArrayList = new ArrayList(listOfUuid);
                Collections.shuffle(tempArrayList);

                for (String uuid : tempArrayList) {
                    while (true) {
                        TransactionManager transactionManager = embedCache.getAdvancedCache().getTransactionManager();
                        Boolean success = false;
                        Long timestamp = System.currentTimeMillis();

                        try {
                            transactionManager.begin();

                            BigDecimal oldValue = (BigDecimal) embedCache.get(uuid);
                            BigDecimal newValue = oldValue.add(new BigDecimal(1000));
                            success = embedCache.replace(uuid, oldValue, newValue);
                            transactionManager.commit();
                        } catch (Exception ex) {
                            try {
                                success = false;
                                transactionManager.rollback();
                            } catch (Exception ex1) {
                            }
                        }

                        if (success)
                            break;
                        else {
                            logger.info("{} retrying {} for {}", success, uuid,
                                    System.currentTimeMillis() - timestamp);
//                            try {
//                                Thread.sleep(new Random().nextInt(100));
//                            } catch (Exception ex) {
//                                ex.printStackTrace();
//                            }
                        }
                    }
                }

                latch.countDown();
            });
        }

        try {
            latch.await();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        final RemoteCache cache = cacheManager.getCache("balance");
        for (String uuid : listOfUuid) {
            while (true) {
                Long timestamp = System.currentTimeMillis();
                MetadataValue metadataValue = cache.getWithMetadata(uuid);
                BigDecimal newValue = (new BigDecimal("" + embedCache.get(uuid))).add(new BigDecimal("" + metadataValue.getValue()));
                Boolean success = cache.replaceWithVersion(uuid, newValue, metadataValue.getVersion());

                if (success) {
                    logger.info("successfully processing {} - {}", uuid, newValue);
                    break;
                }
                else {
                    logger.info("{} retry {} version is {} for {}", success, uuid,
                            metadataValue.getVersion(),
                            System.currentTimeMillis() - timestamp);
                    try {
                        Thread.sleep(new Random().nextInt(100, 1000));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }

        // clear the local cache
        embedCache.clear();

        logger.info("done ====================");
    }

    public List<String> query() {
        List<String> list = new ArrayList<>();
        final RemoteCache cache = cacheManager.getCache("balance");
        for (String uuid : listOfUuid) {
            BigDecimal bigDecimal = new BigDecimal((String) cache.get(uuid));
            if(!bigDecimal.equals(new BigDecimal(100_000)))
                list.add(uuid);
        }
        return list;
    }

    public void initiate() {

        org.infinispan.configuration.cache.Configuration tempCache = new org.infinispan.configuration.cache.ConfigurationBuilder()
                .transaction().transactionMode(TransactionMode.TRANSACTIONAL)
                .build();
        embeddedCacheManager.createCache("temp-cache", tempCache);
        final Cache embedCache = embeddedCacheManager.getCache("temp-cache");
        final RemoteCache cache = cacheManager.getCache("balance");

        for (String uuid : listOfUuid) {
            cache.putIfAbsent(uuid, new BigDecimal(0));
            embedCache.putIfAbsent(uuid, new BigDecimal(0));
            concurrentHashMap.put(uuid,  new BigDecimal(0));
        }
    }
}