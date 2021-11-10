package com.airbus.retex.service.importer.exception;

public class DataImporterException extends Exception {

    private long lineNumber = -1;

    public DataImporterException(String message) {
        super(message);
    }

    public DataImporterException(long lineNumber, String message) {
        super(message);
        this.lineNumber = lineNumber;
    }

    public DataImporterException(long lineNumber, String message, Throwable cause) {
        super(message, cause);
        this.lineNumber = lineNumber;
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(long lineNumber) {
        this.lineNumber = lineNumber;
    }
}
