package com.airbus.retex.configuration;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import javax.cache.CacheManager;
import javax.cache.Caching;

import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheEventListenerConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.event.EventType;
import org.ehcache.impl.config.persistence.DefaultPersistenceConfiguration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.airbus.retex.config.RetexConfig;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.translation.Translate;

@Configuration
public class RetexCacheConfiguration {

    @Autowired
    private RetexConfig retexConfig;

    @Bean
    public JCacheCacheManager springCacheManager(CacheManager cm) {
        return new JCacheCacheManager(cm);
    }

    @Bean
    public CacheManager CacheManager() {

        CacheEventListenerConfigurationBuilder cacheEventListenerConfiguration =
                CacheEventListenerConfigurationBuilder.newEventListenerConfiguration(
                        new CustomCacheEventListener(),
                        EventType.CREATED, EventType.UPDATED, EventType.EVICTED,
                        EventType.EXPIRED, EventType.REMOVED)
                        .ordered().asynchronous();

        Map<String, CacheConfiguration<?, ?>> caches = new HashMap<>();
            caches.put(Translate.TRANSLATE_CACHE, CacheConfigurationBuilder.newCacheConfigurationBuilder(
                    String.class, Object.class, ResourcePoolsBuilder.newResourcePoolsBuilder()
                            .heap(1024, EntryUnit.ENTRIES)
                            .build()
                    )
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(30)))
                    .withService(cacheEventListenerConfiguration)
                    .build()

            );
        caches.put(Media.MEDIA_CACHE, CacheConfigurationBuilder.newCacheConfigurationBuilder(
                String.class, byte[].class, ResourcePoolsBuilder.newResourcePoolsBuilder()
                        .disk(1, MemoryUnit.GB, false)
                        .build()
                )
                .withExpiry(ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofHours(24)))
                .withService(cacheEventListenerConfiguration)
                .build()
        );
        DefaultPersistenceConfiguration persistenceServiceConfiguration =
                new DefaultPersistenceConfiguration(new File(retexConfig.getEhcachePath()));

        EhcacheCachingProvider provider =
                (EhcacheCachingProvider) Caching.getCachingProvider("org.ehcache.jsr107.EhcacheCachingProvider");
        DefaultConfiguration configuration = new DefaultConfiguration(caches,
                provider.getDefaultClassLoader(), persistenceServiceConfiguration);
        return provider.getCacheManager(provider.getDefaultURI(), configuration);
    }
}