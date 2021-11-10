package com.airbus.retex.service.ata;

import java.util.List;

public interface IATAService {

    /**
     * Get all ATA
     *
     * @return all ATA Dto
     */
    List<String> getAllATACode();
}
