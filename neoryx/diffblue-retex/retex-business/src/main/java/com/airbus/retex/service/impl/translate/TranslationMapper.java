package com.airbus.retex.service.impl.translate;

import static com.airbus.retex.business.mapper.MapperUtils.updateList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.airbus.retex.FullyTranslatedDto;
import com.airbus.retex.business.mapper.AbstractMapper;
import com.airbus.retex.model.AbstractTranslation;
import com.airbus.retex.model.TranslatableModel;
import com.airbus.retex.model.TranslationModel;
import com.airbus.retex.model.common.Language;

@Mapper(componentModel = "spring")
public abstract class TranslationMapper extends AbstractMapper {

    @AfterMapping
    public <T extends AbstractTranslation<E>, E extends Enum>
    void mapTranslationsToTranslatedFields(TranslatableModel<T, E> model, @MappingTarget FullyTranslatedDto dto) {
        if(dto.getTranslatedFields() == null) {
            dto.setTranslatedFields(new HashMap<>());
        }
        model.getTranslations()
				.forEach(translation -> {
                    Map<E, String> fieldsMap = (Map<E, String>) dto.getTranslatedFields().get(translation.getLanguage());
                    if(fieldsMap == null) {
                        fieldsMap = new HashMap<>();
                        dto.getTranslatedFields().put(translation.getLanguage(), fieldsMap);
                    }
                    fieldsMap.put(translation.getField(), translation.getValue());
                });
    }

    @AfterMapping
    public <T extends AbstractTranslation<E>, E extends Enum>
    void copyTranslationsToTranslatedFields(TranslatableModel<T, E> model, FullyTranslatedDto dto) {
        if(dto.getTranslatedFields() == null) {
            dto.setTranslatedFields(new HashMap<>());
        }
        model.getTranslations()
				.forEach(translation -> {
                    Map<E, String> fieldsMap = (Map<E, String>) dto.getTranslatedFields().get(translation.getLanguage());
                    if(fieldsMap == null) {
                        fieldsMap = new HashMap<>();
                        dto.getTranslatedFields().put(translation.getLanguage(), fieldsMap);
                    }
                    fieldsMap.put(translation.getField(), translation.getValue());
                });
    }

    /**
     * @param entity
     * @param translationConstructor
     * @param listFields
     * @param <T>
     * @param <E>
     */
    public <T extends AbstractTranslation<E>, E extends Enum> void updateEntityTranslations(TranslatableModel<T, E> entity, Supplier<T> translationConstructor, Map<Language, Map<E, String>> listFields) {
        List<TranslationModel<E>> sourceTranslations = mapToTranslationList(listFields);
        Set<T> currentTranslations = entity.getTranslations();

        updateList(
            sourceTranslations,
            currentTranslations,
				(TranslationModel<E> source, T destination) -> source.getLanguage().equals(destination.getLanguage())
						&& source.getField().equals(destination.getField()),
            () -> {
                T translation = translationConstructor.get();
                entity.addTranslation(translation);
                return translation;
            },
            (TranslationModel<E> source, T destination) -> {
                destination.setLanguage(source.getLanguage());
                destination.setValue(source.getValue());
                destination.setField(source.getField());
            }
        );
    }

    /**
     * Transform map to list of translation entity.
     *
     * @param listFields
     * @param <T> Translation class for entity
     * @param <E> Enum field for translation fields
     * @return
     */
    private <E extends Enum> List mapToTranslationList(Map<Language, Map<E, String>> listFields) {
        List<TranslationModel<E>> translations = new ArrayList<>();

        for (Map.Entry<Language, Map<E, String>> language : listFields.entrySet()) {
            for (Map.Entry<E, String> field : language.getValue().entrySet()) {
				TranslationModel<E> translation = new TranslationModel<>();
                translation.setLanguage(language.getKey());
                translation.setField(field.getKey());
                translation.setValue(field.getValue());
                translations.add(translation);
            }
        }

        return translations;
    }

    /**
     * Transform List to Map translatedFields.
     *
     * @param translations
     * @return
     */
    public  <T extends AbstractTranslation<E>, E extends Enum> Map<Language, Map<E, String>> mapToTranslationFields(Collection<T> translations) {
		EnumMap<Language, Map<E, String>> listFields = new EnumMap<>(Language.class);

        translations.forEach(t -> {
            if(!listFields.containsKey(t.getLanguage())){
                listFields.put(t.getLanguage(), new HashMap<E, String>());
            }
            listFields.get(t.getLanguage()).put(t.getField(), t.getValue());
        });

        return listFields;
    }
}
