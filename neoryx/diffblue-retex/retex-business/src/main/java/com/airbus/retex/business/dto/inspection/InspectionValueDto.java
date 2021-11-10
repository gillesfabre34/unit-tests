package com.airbus.retex.business.dto.inspection;

import com.airbus.retex.model.control.EnumTodoListValue;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.Valid;
import java.util.List;

@Getter
@Setter
public class InspectionValueDto {

    @Valid
    @Nullable
    private List<InspectionStepActivationDto> steps;

    @Nullable
    private EnumTodoListValue todoListValue;
}
