package com.edw.config;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <pre>
 *     com.edw.config.InfinispanConfiguration
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 29 Jan 2024 15:57
 */
@Configuration
public class InfinispanConfiguration {
    @Bean
    public RemoteCacheManager remoteCacheManager() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServers("127.0.0.1:11222");
        builder.protocolVersion("1.0");
        return new RemoteCacheManager(builder.build());
    }
}
