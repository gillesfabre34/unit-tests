package com.airbus.retex.service.importer.exception;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class EntryAlreadyExistsException extends DataImporterException {

    public EntryAlreadyExistsException(Map<String, String> kvs) {
        this(kvs.entrySet().stream()
                .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                .collect(Collectors.toList()));
    }
    public EntryAlreadyExistsException(Collection<String> values) {
        this(String.join(",",values));
    }
    public EntryAlreadyExistsException(String values) {
        super("The entry already exists: " + values);
    }
}
