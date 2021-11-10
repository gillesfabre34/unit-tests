package com.airbus.retex.business.dto.operationType;

import com.airbus.retex.business.dto.TranslateDto;
import com.airbus.retex.business.dto.TranslatedDto;
import com.airbus.retex.model.operation.OperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OperationTypeLightDto extends TranslatedDto {

    private Long id;

    private TranslateDto name = new TranslateDto(this, OperationTypeFieldsEnum.name);

    @Override
    public String getClassName() {return OperationType.class.getSimpleName();}

    @Override
    public Long getEntityId() {return id;}
}
