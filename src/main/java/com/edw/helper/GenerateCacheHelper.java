package com.edw.helper;

import jakarta.annotation.PostConstruct;
import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
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

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    private List<String> listOfUuid = new ArrayList<>();

    @PostConstruct
    public void prepareData() {
        for (int i = 0; i < 5000; i++) {
            listOfUuid.add(UUID.randomUUID().toString());
        }
    }

    public void generate() {
        logger.info("starting ====================");
        final RemoteCache cache = cacheManager.getCache("balance");
        for (int j = 0; j < 9; j++) {
            executor.execute(() -> {
                for (int i = 0; i < 5000; i++) {
                    while (true) {
                        Long timestamp = System.currentTimeMillis();
                        MetadataValue metadataValue = cache.getWithMetadata(listOfUuid.get(i));
                        BigDecimal newValue =
                                (new BigDecimal((String) metadataValue.getValue())).add(new BigDecimal(1000));
                        Boolean success = cache.replaceWithVersion(listOfUuid.get(i), newValue, metadataValue.getVersion());

                        if (success)
                            break;
                        else {
                            logger.info("{} printing {} version is {} for {}", success, listOfUuid.get(i),
                                    metadataValue.getVersion(),
                                    System.currentTimeMillis() - timestamp);
//                            try {
//                                Thread.sleep(new Random().nextInt(100));
//                            } catch (Exception ex) {
//                                ex.printStackTrace();
//                            }
                        }
                    }
                }
            });
        }
    }

    public void initiate() {
        final RemoteCache cache = cacheManager.getCache("balance");
        for (int i = 0; i < 5000; i++) {
            cache.putIfAbsent(listOfUuid.get(i), new BigDecimal(1000));
        }
    }
}
