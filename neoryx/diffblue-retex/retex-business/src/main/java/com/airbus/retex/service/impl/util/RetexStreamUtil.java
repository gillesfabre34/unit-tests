package com.airbus.retex.service.impl.util;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class RetexStreamUtil {

    public static <T> Collector<T, ?, Optional<T>> findOneOrEmpty() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() > 1) {
                        throw new IllegalStateException();
                    }
                    if (list.isEmpty()) {
                        return Optional.empty();
                    }
                    return Optional.ofNullable(list.get(0));
                }
        );
    }

    public static <T> Predicate<T> distinctBy(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
