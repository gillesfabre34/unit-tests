package com.airbus.retex.business.dto.operation;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.functionalArea.FunctionalAreaDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListOperationDto implements Dto {

    public ListOperationDto(ListOperationDto listDrtOperationDto){
        this.setFunctionalAreas(listDrtOperationDto.getFunctionalAreas());
        this.setOperations(listDrtOperationDto.getOperations());
    }

    @Nullable
    private List<OperationFullDto> operations;

    @Nullable
    private List<FunctionalAreaDto> functionalAreas;

}
