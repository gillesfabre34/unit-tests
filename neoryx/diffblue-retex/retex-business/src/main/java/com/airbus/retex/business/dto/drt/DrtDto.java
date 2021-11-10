package com.airbus.retex.business.dto.drt;



import com.airbus.retex.business.dto.origin.OriginLightDto;
import com.airbus.retex.business.dto.part.PartDesignationDto;
import com.airbus.retex.business.dto.user.UserLightDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@NoArgsConstructor
public class DrtDto extends DrtLightDto {

    private String partNumber;

    private String serialNumber;

    private PartDesignationDto designation;

    private OriginLightDto origin;

    private UserLightDto assignedOperator;

    // this is for the US 686
    //private Boolean isClosable;
}
