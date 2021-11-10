package com.airbus.retex.business.dto;

/**
 * Interface used for outbound Dto to map technical id to id
 */
public interface IVersionableChildOutDto extends Dto {

    void setTechnicalId(Long technicalId);

    Long getId();
}
