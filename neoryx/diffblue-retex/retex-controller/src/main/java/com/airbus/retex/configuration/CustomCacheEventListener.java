package com.airbus.retex.configuration;

import lombok.extern.slf4j.Slf4j;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

import java.util.Arrays;


@Slf4j
public class CustomCacheEventListener implements CacheEventListener<Object, Object> {

    @Override
    public void onEvent(CacheEvent<?, ?> cacheEvent) {
        if (log.isDebugEnabled()) {
            log.debug("Cache event = {}, Key = {}, Old value = {}, New value = {}",
                    cacheEvent.getType(), cacheEvent.getKey(),
                    trimByteArrayLength(cacheEvent.getOldValue()),
                    trimByteArrayLength(cacheEvent.getNewValue()));
        }
    }

    private Object trimByteArrayLength(Object o) {
        if (o instanceof byte[]) {
            byte[] a = (byte[]) o;
            return String.format("byte[](size=%d, start= %s)", a.length,
                    Arrays.toString(Arrays.copyOfRange(a,0,Math.min(a.length, 80))));
        }
        return o;
    }
}
