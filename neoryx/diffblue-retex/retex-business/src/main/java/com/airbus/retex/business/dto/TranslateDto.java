package com.airbus.retex.business.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TranslateDto implements Dto {

    private static final ThreadLocal<Long> REVISION_THREAD_LOCAL = new ThreadLocal<>();
    private static final String MISSING_TRANSLATION = "[ Missing translation ]";

    public static void setCurrentRevision(Long revision) {
        REVISION_THREAD_LOCAL.set(revision);
    }

    private TranslatedDto translatedDto;
    private String className;
    private Long entityId;
    private String field;
    private Long revision;
    private String defaultValue = MISSING_TRANSLATION;



    public TranslateDto(TranslatedDto translatedDto, String field) {
        this.translatedDto = translatedDto;
        this.field = field;
        this.revision = REVISION_THREAD_LOCAL.get();
    }

    public TranslateDto(TranslatedDto translatedDto, Enum fieldEnumValue) {
        this.translatedDto = translatedDto;
        this.field = fieldEnumValue.name();
        this.revision = REVISION_THREAD_LOCAL.get();
    }

    public TranslateDto(TranslatedDto translatedDto, Enum fieldEnumValue, String defaultValue) {
        this.translatedDto = translatedDto;
        this.field = fieldEnumValue.name();
        this.revision = REVISION_THREAD_LOCAL.get();
        this.defaultValue = defaultValue;
    }

    public String getClassName() {
        return (translatedDto == null ? className : translatedDto.getClassName());
    }

    public Long getEntityId() {
        return (translatedDto == null ? entityId : translatedDto.getEntityId());
    }

    public Long getRevision() {
        return (translatedDto == null ? revision : translatedDto.getRevision());
    }
}
