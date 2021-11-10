package com.airbus.retex.business.dto;

import com.airbus.retex.model.AbstractTranslation;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class TranslationDto<T extends AbstractTranslation<E>, E extends Enum> implements Dto {

    private static final String MISSING_TRANSLATION = "[ Missing translation ]";
    private ITranslationDto translationDtoAttribut;
    private Class<T> className;
    private Long entityId;
    private E field;

    private String defaultValue = MISSING_TRANSLATION;

    public TranslationDto(ITranslationDto entity, E field) {
        this.translationDtoAttribut = entity;
        this.field = field;
    }

    public TranslationDto(ITranslationDto entity, E field, String defaultValue) {
        this.translationDtoAttribut = entity;
        this.field = field;
        this.defaultValue = defaultValue;
    }

    public Long getEntityId() {
        return (translationDtoAttribut == null ? entityId : translationDtoAttribut.getEntityId());
    }

    public Class<T> getClassName() {
        return (translationDtoAttribut == null ? className : translationDtoAttribut.getClassTranslation());
    }
}
