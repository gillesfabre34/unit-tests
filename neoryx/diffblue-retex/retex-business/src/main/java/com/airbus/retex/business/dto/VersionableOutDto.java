package com.airbus.retex.business.dto;

/**
 * Base implementation of IVersionableOutDto
 */
public class VersionableOutDto implements IVersionableOutDto {

    private Long naturalId;

    public void setNaturalId(Long naturalId) {
        this.naturalId = naturalId;
    }

    public Long getId() {
        return this.naturalId;
    }
}
