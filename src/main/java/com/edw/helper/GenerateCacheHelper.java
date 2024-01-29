package com.edw.helper;

import jakarta.annotation.PostConstruct;
import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
                    Long timestamp = System.currentTimeMillis();
                    MetadataValue metadataValue = cache.getWithMetadata(listOfUuid.get(i));
                    if(metadataValue==null) {
                        cache.put(listOfUuid.get(i), UUID.randomUUID().toString());
                    } else {
                        cache.replaceWithVersion(listOfUuid.get(i), UUID.randomUUID()+"-"+metadataValue.getVersion(), metadataValue.getVersion());
                    }
                    logger.info("printing {} for {}", listOfUuid.get(i), System.currentTimeMillis()-timestamp);
                }
            });
        }
    }
}
