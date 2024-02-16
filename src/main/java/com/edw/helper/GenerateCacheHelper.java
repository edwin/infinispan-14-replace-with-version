package com.edw.helper;

import jakarta.annotation.PostConstruct;
import org.infinispan.client.hotrod.Flag;
import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
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
    public void prepareData () {
        for (int i = 0; i < 5000; i ++) {
            listOfUuid.add(UUID.randomUUID().toString());
        }
    }

    public void generate() {
        final RemoteCache cache = cacheManager.getCache("user-cache");
        for(int j = 0 ; j < 10; j ++) {
            executor.execute(() -> {
                for (int i = 0; i < 5000; i ++) {
                    while (true) {
                        Long timestamp = System.currentTimeMillis();
                        MetadataValue metadataValue = cache.getWithMetadata(listOfUuid.get(i));
                        Boolean success = false;
                        if(metadataValue==null) {
                            Object o = cache.withFlags(Flag.FORCE_RETURN_VALUE).putIfAbsent(listOfUuid.get(i), new BigDecimal(1000));
                            success = o == null;
                        } else {
                            BigDecimal newValue = (new BigDecimal((String)metadataValue.getValue())).add(new BigDecimal(1000));
                            success = cache.replaceWithVersion(listOfUuid.get(i), newValue, metadataValue.getVersion());
                        }
                        logger.info("{} printing {} version is {} for {}", success, listOfUuid.get(i), metadataValue==null?"0":metadataValue.getVersion(), System.currentTimeMillis()-timestamp);

                        if(success)
                            break;
                    }
                }
            });
        }
    }
}
