package com.airbus.retex.service.impl.translate;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.dto.TranslateDto;
import com.airbus.retex.business.dto.TranslatedFieldsDto;
import com.airbus.retex.business.exception.TechnicalError;
import com.airbus.retex.model.basic.AbstractVersionableChildModel;
import com.airbus.retex.model.basic.IIdentifiedModel;
import com.airbus.retex.model.basic.IIdentifiedVersionModel;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.translation.Translate;
import com.airbus.retex.persistence.translate.TranslateRepository;
import com.airbus.retex.service.impl.util.HibernateUtil;
import com.airbus.retex.service.translate.ITranslateService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional(rollbackFor = Exception.class)
@Service(value = "translateService")
public class TranslateServiceImpl implements ITranslateService {

    private static final String CACHE_MAIN_KEY_IDENTIFIER = "#className.concat('-').concat(#entityId).concat('-').concat(#field).concat('-').concat(#language)";
    private static final String CACHE_MAIN_KEY_IDENTIFIER_WITH_REVISION = CACHE_MAIN_KEY_IDENTIFIER + ".concat('-').concat(#revision)";

    @Autowired
    private TranslateRepository translationRepository;

    @Autowired
    private EntityManager entityManager;


    @Autowired
    private ITranslateService self;

    @Override
    @CacheEvict(value = Translate.TRANSLATE_CACHE, key = "#entity.getClass().getSimpleName() + '-' + #entity.getId() + '-'+ #field + '-' + #language + '-null'")
    public void saveFieldValue(IIdentifiedModel<Long> entity, String field, Language language, String value) {
        saveFieldValue(entity.getClass().getSimpleName(), entity.getId(), field, language, value);
    }

    @Override
    @CacheEvict(value = Translate.TRANSLATE_CACHE, key = "#entity.getClass().getSimpleName() + '-' + #entity.getId() + '-'+ #field + '-' + #language + '-null'")
    public void saveFieldValueVersion(IIdentifiedVersionModel<Long> entity, String field, Language language, String value) {
        saveFieldValue(entity.getClass().getSimpleName(), entity.getTechnicalId(), field, language, value);
    }


    /**
     * save new translated field
     *
     * @param className
     * @param entityId
     * @param field
     * @param language
     * @param value
     * @return
     */
    @Override
    @CacheEvict(value = Translate.TRANSLATE_CACHE, key = CACHE_MAIN_KEY_IDENTIFIER+".concat('-null')")
    public void saveFieldValue(String className, Long entityId, String field, Language language, String value) {
        Translate translate = new Translate();
        translate.setClassName(className);
        translate.setEntityId(entityId);
        translate.setField(field);
        translate.setLanguage(language);
        translate.setValue(value);
        translationRepository.save(translate);
    }

    @Override
    public String getFieldValue(IIdentifiedModel<Long> entity, String field, Language language) {
        return this.getFieldValue(HibernateUtil.getPersistentClass(entity).getSimpleName(), entity.getId(), field, language);
    }
    /**
     * get field value
     *
     * @param className
     * @param entityId
     * @param field
     * @param language
     * @return
     */
    @Override
    @Cacheable(value = Translate.TRANSLATE_CACHE, key = CACHE_MAIN_KEY_IDENTIFIER + "+'-null'")
    public String getFieldValue(String className, Long entityId, String field, Language language) {
        return this.getFieldValue(className, entityId, field, language, null);
    }

    @Override
    @Cacheable(value = Translate.TRANSLATE_CACHE, key = CACHE_MAIN_KEY_IDENTIFIER_WITH_REVISION)
    public String getFieldValue(String className, Long entityId, String field, Language language, Long revision) {
        Optional<Translate> translateOpt = internalFindTranslate(className, entityId, field, language, revision);
        if (translateOpt.isEmpty() && !language.isDefault()) {
            translateOpt = internalFindTranslate(className, entityId, field, Language.getDefault(), revision);
        }
        if (translateOpt.isPresent()) {
            return translateOpt.get().getValue();
        } else {
            log.info("Cannot find translation for parameters : "+className+", "+entityId+", "+language+", revision");
            return null;
        }
    }

    private Optional<Translate> internalFindTranslate(String className, Long entityId, String field, Language language, Long revision) {
        if (revision == null) {
            return translationRepository.findById(new Translate.TranslateId(className, entityId, field, language));
        } else {
            AuditReader auditReader = AuditReaderFactory.get(entityManager);
            AuditQuery auditQuery = auditReader.createQuery()
                    .forEntitiesAtRevision(Translate.class, revision)
                    .add(AuditEntity.id().eq(new Translate.TranslateId(className, entityId, field, language)));
            List<Translate> results = auditQuery.getResultList();
            if (results.size() == 1) {
                return Optional.of(results.get(0));
            } else if (results.isEmpty()) {
                return Optional.empty();
            } else {
                throw new TechnicalError("Multiple results");
            }
        }
    }


    /**
     * get translated field, using cache
     *
     * @param entity
     * @param field
     * @return
     */
    @Override
    public Map<Language, String> getFieldValues(IIdentifiedModel<Long> entity, String field) {
        return getFieldValues(HibernateUtil.getPersistentClass(entity).getSimpleName(), entity.getId(), field);
    }



    @Override
    public Map<Language, String> getFieldValues(String className, Long entityId, String field) {
		EnumMap<Language, String> result = new EnumMap<>(Language.class);
        for (var lang : Language.values()) {
            String value = getFieldValue(className, entityId, field, lang);
            if (value != null) {
                result.put(lang, value);
            }
        }
        return result;
    }

    @Override
    public String manageDefaultValue(TranslateDto translateDto, Language language) {
        String translate = getFieldValue(translateDto.getClassName(),
                translateDto.getEntityId(), translateDto.getField(), language);

        if (null == translate) {
            translate = translateDto.getDefaultValue();
        }
        return translate;
    }

    @Override
    public void saveFieldValues(IIdentifiedModel<Long> entity, String field, Map<Language, String> translatedFieldsHash) {
        saveFieldValues(HibernateUtil.getPersistentClass(entity).getSimpleName(), entity.getId(), field, translatedFieldsHash);
    }

    @Override
    public void saveFieldValues(String className, Long entityId, String field, Map<Language, String> translatedFieldsHash) {
		translatedFieldsHash.forEach((lang, value) -> saveFieldValue(className, entityId, field, lang, value));
    }

    @Override
    public <T extends Enum> Map<Language, Map<T, String>> getTranslatedFields(final IIdentifiedModel<Long> entity, Set<T> entityFields) {
       return getTranslatedFields(HibernateUtil.getPersistentClass(entity).getSimpleName(), entity.getId(), entityFields);
    }

    @Override
    public <T extends Enum> Map<Language, Map<T, String>> getTranslatedFields(String className, Long entityId, Set<T> entityFields) {
        TranslatedFieldsDto<T> result = new TranslatedFieldsDto<>();
        for (Language lang : Language.values()) {
            Map<T, String> fields = new HashMap<>();
            entityFields.forEach(field ->
                    fields.put(field, self.getFieldValue(className, entityId, field.toString(), lang))
            );
            result.put(lang, fields);
        }
        return result;
    }

    @Override
    public <T extends Enum> void saveTranslatedFields(IIdentifiedModel<Long> entity, Map<Language, Map<T, String>> listFields) {
        if (null == listFields) {
            return;
        }

        for (Map.Entry<Language, Map<T, String>> entry : listFields.entrySet()) {
            for (Map.Entry<T, String> field : entry.getValue().entrySet()) {
                self.saveFieldValue(entity, field.getKey().toString(), entry.getKey(), field.getValue());
            }
        }
    }

    @Override
    public <T extends Enum> Set<Translate> convertToTranslateList(IIdentifiedVersionModel<Long> entity, Map<Language, Map<T, String>> listFields) {
        Set<Translate> translateList = new HashSet<>();
        if (null == listFields) {
            return translateList;
        }

        for (Map.Entry<Language, Map<T, String>> entry : listFields.entrySet()) {
            for (Map.Entry<T, String> field : entry.getValue().entrySet()) {
                Translate translate = new Translate();
                translate.setClassName(entity.getClass().getSimpleName());
                translate.setEntityId(entity.getTechnicalId());
                translate.setField(field.getKey().toString());
                translate.setLanguage(entry.getKey());
                translate.setValue(field.getValue());
                translateList.add(translate);
            }
        }
        return translateList;
    }

    @Override
    public void saveTranslate(Map<Object, Set<Translate>> mapToTranslate) {
        mapToTranslate.forEach((o, translates) -> {
            if(o instanceof AbstractVersionableChildModel) {
				translates.forEach(translate -> saveFieldValueVersion(((AbstractVersionableChildModel) o),
						translate.getField(), translate.getLanguage(), translate.getValue())


                );
            }
        });
    }
}
