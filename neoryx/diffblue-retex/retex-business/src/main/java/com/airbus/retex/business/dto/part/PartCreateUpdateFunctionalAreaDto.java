package com.airbus.retex.business.dto.part;

import com.airbus.retex.business.dto.functionalArea.FunctionalAreaCreateOrUpdateDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PartCreateUpdateFunctionalAreaDto extends PartUpdateHeaderDto {

    private List<FunctionalAreaCreateOrUpdateDto> functionalAreas;
}
