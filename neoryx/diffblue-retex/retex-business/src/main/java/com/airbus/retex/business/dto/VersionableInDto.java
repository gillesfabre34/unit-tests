package com.airbus.retex.business.dto;

/**
 * Base implementation of IVersionableInDto
 */
public class VersionableInDto implements IVersionableInDto {

    private Long naturalId;

    public void setId(Long naturalId) {
        this.naturalId = naturalId;
    }

    public Long getNaturalId() {
        return this.naturalId;
    }
}
