package com.airbus.retex.business.dto;

/**
 * Interface used for outbound Dto to map natural id to id
 */
public interface IVersionableOutDto extends Dto {

    void setNaturalId(Long naturalId);

    Long getId();
}
