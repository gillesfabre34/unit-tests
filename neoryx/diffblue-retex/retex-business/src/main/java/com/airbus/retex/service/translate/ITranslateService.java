package com.airbus.retex.service.translate;

import com.airbus.retex.business.dto.TranslateDto;
import com.airbus.retex.model.basic.IIdentifiedModel;
import com.airbus.retex.model.basic.IIdentifiedVersionModel;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.translation.Translate;

import java.util.Map;
import java.util.Set;

public interface ITranslateService {

    void saveFieldValue(IIdentifiedModel<Long> entity, String field, Language language, String value);

    void saveFieldValueVersion(IIdentifiedVersionModel<Long> entity, String field, Language language, String value);

    void saveFieldValue(String className, Long entityId, String field, Language language, String value);

    String getFieldValue(IIdentifiedModel<Long> entity, String field, Language language);

    String getFieldValue(String className, Long entityId, String field, Language language);

    String getFieldValue(String className, Long entityId, String field, Language language, Long revision);

    Map<Language, String> getFieldValues(IIdentifiedModel<Long> entity, String field);

    Map<Language, String> getFieldValues(String className, Long entityId, String field);

    String manageDefaultValue(TranslateDto translateDto, Language language);

    void saveFieldValues(IIdentifiedModel<Long> entity, String field, Map<Language, String> translatedFieldsHash);

    void saveFieldValues(String className, Long entityId, String field, Map<Language, String> translatedFieldsHash);

    /**
     * Get translated fields of entity
     * @param <T>
     * @param entity
     * @param entityFields
     * @return
     */
    <T extends Enum> Map<Language, Map<T, String>> getTranslatedFields(final IIdentifiedModel<Long> entity, Set<T> entityFields);

    <T extends Enum> Map<Language, Map<T, String>> getTranslatedFields(String className, Long entityId, Set<T> entityFields);

    /**
     * Save translated fields of entity
     * @param entity
     * @param listFields
     * @param <T>
     */
    <T extends Enum> void saveTranslatedFields(IIdentifiedModel<Long> entity, Map<Language, Map<T, String>> listFields);

    <T extends Enum> Set<Translate> convertToTranslateList(IIdentifiedVersionModel<Long> entity, Map<Language, Map<T, String>> listFields);


    /**
     * Save Map<Object, Set<Tranlate>>
     * @param mapToTranslate
     */
    void saveTranslate(Map<Object, Set<Translate>> mapToTranslate);

}
