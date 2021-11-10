package com.airbus.retex.service.impl.translate;

import com.airbus.retex.business.dto.TranslationDto;
import com.airbus.retex.model.AbstractTranslation;
import com.airbus.retex.model.TranslatableModel;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.service.translate.ITranslationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
@Service(value = "translationService")
@Transactional
public class TranslationServiceImpl implements ITranslationService {

    @Autowired
    EntityManager entityManager;

    @Override
    public String manageTranslationDefaultValue(TranslationDto translationDto, Language language) {
        String translate = getTranslationFieldValue(translationDto.getEntityId(), translationDto.getField(), translationDto.getClassName(), language);

        if (null == translate) {
            translate = translationDto.getDefaultValue();
        }
        return translate;
    }

    @Override
    public <T extends AbstractTranslation<E>, E extends Enum> String getFieldValue(TranslatableModel<T, E> model, E field, Language language) {
        AtomicReference<String> value = new AtomicReference<>();
        model.getTranslations().forEach(translation -> {
            if (translation.getField().equals(field) && translation.getLanguage().equals(language)) {
                value.set(translation.getValue());
            }
        });

        return value.get();
    }

    private <T extends AbstractTranslation<E>, E extends Enum>  String getTranslationFieldValue(Long id, E field, Class<T> className,  Language language) {
        T translation = internalFindTranslation(className, id, field, language);
        if (null == translation && !language.isDefault()) {
            translation = internalFindTranslation(className, id, field, Language.getDefault());
        }
        if (null != translation) {
            return translation.getValue();
        } else {
            log.info("Cannot find translation for parameters : "+className+", "+ id +", "+language+", revision");
            return null;
        }
    }


    private <T extends AbstractTranslation<E>, E extends Enum>  T internalFindTranslation(Class<T> className, Long entityId, E field, Language language) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> queryBuilder = builder.createQuery(className);
        Root<T> root = queryBuilder.from(className);

        Predicate predicateField = builder.equal(root.get("field"), field);
        Predicate predicateLang = builder.equal(root.get("language"), language);
        Predicate predicateEntityId = builder.equal(root.get("entity"), entityId);
        Predicate predicate = builder.and(predicateEntityId, predicateField, predicateLang);
        queryBuilder.where(predicate);

        try {
            return entityManager.createQuery(queryBuilder).getSingleResult();
        } catch (NoResultException e){
            return null;
        }

    }

}
