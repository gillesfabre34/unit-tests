package com.airbus.retex.service.impl.translate;

import com.airbus.retex.model.common.Language;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

public class TranslationValidator {

	/**
	 * private contructor
	 */
	private TranslationValidator() {

	}

    public static <T extends Enum<?>> boolean validateTranslations(Map<Language, Map<T, String>> translations) {
        return validateTranslations(translations, null);
    }

    public static <T extends Enum<?>> boolean validateTranslations(Map<Language, Map<T, String>> translations, List<String> requiredFields) {
        if(translations == null){
            return false;
        }
        Language[] languages = Language.values();
        if(translations.size() != languages.length){
            // Each field must be set in all languages
            return false;
        }
        int count = translations.get(languages[0]).size();
        for (Language lang : languages){
            if(count != translations.get(lang).size()){
                // Each languages must hold all entries
                return false;
            }
        }
        for (Language lang : languages){
            Map<? extends Enum, String> items = translations.get(lang);
            for (String value : items.values()){
                if(StringUtils.isEmpty(value)){
                    // No empty value allowed
                    return false;
                }
            }
        }

        //Get enum class from first entry of first map
        Map.Entry<Language, Map<T, String>> languageEntry = translations.entrySet().iterator().next();
        if(languageEntry.getValue().size() == 0) {
            //If nothing in the first map
            return (requiredFields !=  null) && requiredFields.isEmpty();
        }
        Map.Entry<T, String> fieldEntry = languageEntry.getValue().entrySet().iterator().next();
        Class<Enum> enumClass = (Class<Enum>) fieldEntry.getKey().getClass();

        if(requiredFields == null){
            //Then look for all enum values
            for (Enum value : enumClass.getEnumConstants()) {
                if (!translations.get(languages[0]).containsKey(value)) {
                    return false;
                }
            }
        } else {
            //Then look for value in requiredFields
            for (String field : requiredFields) {
                Enum value;
                try {
                    value = Enum.valueOf(enumClass, field);
                } catch (IllegalArgumentException ex) {
                    return false;
                }
                if (!translations.get(languages[0]).containsKey(value)) {
                    return false;
                }
            }
        }
        return true;
    }
}
