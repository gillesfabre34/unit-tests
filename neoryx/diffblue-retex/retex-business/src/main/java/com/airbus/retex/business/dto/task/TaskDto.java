package com.airbus.retex.business.dto.task;

import com.airbus.retex.business.dto.TranslateDto;
import com.airbus.retex.business.dto.TranslatedDto;
import com.airbus.retex.business.dto.functionality.FunctionalityFieldsEnum;
import com.airbus.retex.model.basic.IIdentifiedModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskDto extends TranslatedDto {
    private Long id;

    private final TranslateDto name = new TranslateDto(this, FunctionalityFieldsEnum.name);

    private final String className;

    public TaskDto(Class<? extends IIdentifiedModel> clazz) {
        this.className = clazz.getSimpleName();
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public Long getEntityId() {
        return id;
    }
}
