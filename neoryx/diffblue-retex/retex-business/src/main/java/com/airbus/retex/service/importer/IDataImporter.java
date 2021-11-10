package com.airbus.retex.service.importer;

public interface IDataImporter {
    enum Status {
        Unknown,
        Skipped,
        Success,
        Error,
    }
    /**
     *
     * @throws Exception
     */
    public Status processImport() throws Exception;
}
