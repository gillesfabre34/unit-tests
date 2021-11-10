package com.airbus.retex.service.translate;

import com.airbus.retex.business.dto.TranslationDto;
import com.airbus.retex.model.AbstractTranslation;
import com.airbus.retex.model.TranslatableModel;
import com.airbus.retex.model.common.Language;

public interface ITranslationService {

    String manageTranslationDefaultValue(TranslationDto translationDto, Language language);

    <T extends AbstractTranslation<E>, E extends Enum> String getFieldValue(TranslatableModel<T, E> model, E field, Language language);

}
