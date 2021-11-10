package com.airbus.retex.service.impl.translate;

import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.request.RequestFieldsEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TranslationValidatorUT {

    @Test
    public void validateTranslations_OK(){
        List<String> requiredFields = List.of(RequestFieldsEnum.name.name());
        Map<Language, Map<RequestFieldsEnum, String>> translations = getValidTranslations();
        boolean result = TranslationValidator.validateTranslations(translations, requiredFields);
        Assertions.assertTrue(result);
    }

    @Test
    public void validateTranslations_allEmptyTranslations_Nok(){
        List<String> requiredFields = List.of();
        Map<Language, Map<RequestFieldsEnum, String>> translations = new HashMap<>();
        boolean result = TranslationValidator.validateTranslations(translations, requiredFields);
        Assertions.assertFalse(result);
    }

    @Test
    public void validateTranslations_missingLanguageVersionOfField_Nok(){
        List<String> requiredFields = List.of(RequestFieldsEnum.name.name());
        Map<Language, Map<RequestFieldsEnum, String>> translations = getValidTranslations();
        translations.get(Language.FR).remove(RequestFieldsEnum.description);
        boolean result = TranslationValidator.validateTranslations(translations, requiredFields);
        Assertions.assertFalse(result);
    }

    @Test
    public void validateTranslations_missingLanguageVersion_Nok(){
        List<String> requiredFields = List.of(RequestFieldsEnum.name.name());
        Map<Language, Map<RequestFieldsEnum, String>> translations = getValidTranslations();
        translations.remove(Language.FR);
        boolean result = TranslationValidator.validateTranslations(translations, requiredFields);
        Assertions.assertFalse(result);
    }

    @Test
    public void validateTranslations_EmptyValue_Nok(){
        List<String> requiredFields = List.of(RequestFieldsEnum.name.name());
        Map<Language, Map<RequestFieldsEnum, String>> translations = getValidTranslations();
        translations.get(Language.FR).put(RequestFieldsEnum.description, "");
        boolean result = TranslationValidator.validateTranslations(translations, requiredFields);
        Assertions.assertFalse(result);
    }

    @Test
    public void validateTranslations_RequiredFieldMissing_Nok(){
        List<String> requiredFields = List.of(RequestFieldsEnum.name.name());
        Map<Language, Map<RequestFieldsEnum, String>> translations = getValidTranslations();
        translations.get(Language.FR).remove(RequestFieldsEnum.name);
        translations.get(Language.EN).remove(RequestFieldsEnum.name);
        boolean result = TranslationValidator.validateTranslations(translations, requiredFields);
        Assertions.assertFalse(result);
    }

    private Map<Language, Map<RequestFieldsEnum, String>> getValidTranslations(){
        Map<Language, Map<RequestFieldsEnum, String>> translations = new HashMap<>();
        Map<RequestFieldsEnum, String> english = new HashMap<>();
        english.put(RequestFieldsEnum.name, "hello");
        english.put(RequestFieldsEnum.description, "description");
        translations.put(Language.EN, english);
        Map<RequestFieldsEnum, String> french = new HashMap<>();
        french.put(RequestFieldsEnum.name, "bonjour");
        french.put(RequestFieldsEnum.description, "description");
        translations.put(Language.FR, french);
        return translations;
    }
}
