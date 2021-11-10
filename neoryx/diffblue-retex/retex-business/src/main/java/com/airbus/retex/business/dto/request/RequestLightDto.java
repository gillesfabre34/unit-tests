package com.airbus.retex.business.dto.request;

import com.airbus.retex.business.dto.TranslateDto;
import com.airbus.retex.business.dto.TranslatedDto;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.request.RequestFieldsEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestLightDto extends TranslatedDto {

    private Long id;

    private TranslateDto name = new TranslateDto(this, RequestFieldsEnum.name);

    private TranslateDto description = new TranslateDto(this, RequestFieldsEnum.description, null);

    private String reference;

    private LocalDateTime creationDate;

    private LocalDate dueDate;

    private EnumStatus status;

    private Boolean isDeletable;

    @Override
    public String getClassName() {
        return (Request.class.getSimpleName());
    }

    @Override
    public Long getEntityId() {
        return id;
    }
}
