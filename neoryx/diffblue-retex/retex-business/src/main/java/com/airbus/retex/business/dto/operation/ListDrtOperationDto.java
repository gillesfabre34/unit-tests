package com.airbus.retex.business.dto.operation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListDrtOperationDto extends ListOperationDto {

    public ListDrtOperationDto(ListOperationDto listOperationDto){
        super(listOperationDto);
    }

    private boolean isClosable;
}
