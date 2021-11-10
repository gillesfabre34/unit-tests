package com.airbus.retex.service.importer.exception;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class InvalidEntryException extends DataImporterException {

    public InvalidEntryException(Map<String, String> kvs) {
        this(kvs.entrySet().stream()
                .map(e -> String.format("%s=%s", e.getKey(), e.getValue()))
                .collect(Collectors.toList()));
    }
    public InvalidEntryException(Collection<String> values) {
        this(String.join(",",values));
    }
    public InvalidEntryException(String values) {
        super("The entry is invalid: " + values);
    }
}
