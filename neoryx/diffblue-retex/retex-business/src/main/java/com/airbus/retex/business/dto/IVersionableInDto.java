package com.airbus.retex.business.dto;

/**
 * Interface used for inbound Dto to map id to natural id
 */
public interface IVersionableInDto extends Dto {

    void setId(Long naturalId);

    Long getNaturalId();
}
