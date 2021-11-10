package com.airbus.retex.business.dto.functionality;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@ApiModel(value = "FunctionalityFieldsDto list parameters", description = "different Functionality attributes")
public class FunctionalityFields {
    public static final String NAME = "name";

	/**
	 * private contructor
	 */
	private FunctionalityFields() {

	}
}