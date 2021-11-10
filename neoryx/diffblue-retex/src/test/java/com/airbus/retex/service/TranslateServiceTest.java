package com.airbus.retex.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.hamcrest.Matcher;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import com.airbus.retex.BaseRepositoryTest;
import com.airbus.retex.model.admin.role.Role;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.model.basic.IIdentifiedModel;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.damage.Damage;
import com.airbus.retex.service.translate.ITranslateService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
public class TranslateServiceTest extends BaseRepositoryTest {

    private static final String TRANSLATE_CACHE_REPO_NAME = "translateCacheable";

    @Autowired
    private ITranslateService translateService;


    @Autowired
    private CacheManager cacheManager;

    @Autowired
    protected ObjectMapper objectMapper;


    private String fieldName = "FIELD";
    private String fieldValue = "VALEUR";

    private static final String TEST_FIELD = "test";
    private static final String FR_VALUE = "En Français";
    private static final String EN_VALUE = "In English";
    private static final String ROLE_CLASSNAME = Role.class.getSimpleName();
    private Role role;

    @BeforeEach
    public void before() {
        role = dataSetInitializer.createRole(dataset.airbusEntity_france, RoleCode.ADMIN);
        translateService.saveFieldValue(role, TEST_FIELD, Language.FR, FR_VALUE);
        translateService.saveFieldValue(role, TEST_FIELD, Language.EN, EN_VALUE);
    }

    @Test
    public void getFieldValue_english() {
        String value = translateService.getFieldValue(ROLE_CLASSNAME, role.getId(), TEST_FIELD, Language.EN);
        assertThat(value, equalTo(EN_VALUE));
    }

    @Test
    public void getFieldValue_french() {
        String value = translateService.getFieldValue(ROLE_CLASSNAME, role.getId(), TEST_FIELD, Language.FR);
        assertThat(value, equalTo(FR_VALUE));
    }

    @Test
    public void getFieldValue_mapChangeOnSave() {
        Map<Language, String> mapBefore = translateService.getFieldValues(role, TEST_FIELD);
        assertThat(mapBefore.get(Language.EN), equalTo(EN_VALUE));

        final String newValue = "New value";
        translateService.saveFieldValue(role, TEST_FIELD, Language.EN, newValue);

        Map<Language, String> mapAfter = translateService.getFieldValues(role, TEST_FIELD);
        assertThat(mapAfter.get(Language.EN), equalTo(newValue));
    }

    @Test
    public void getFieldValue1_cacheUsed() {
        evictCache(role, TEST_FIELD, Language.FR, null);
        // FIXME : for GUILLAUME, CETTE METHODE N'EST PAS CACHÉE
        // translateService.getFieldValue(role, TEST_FIELD, Language.FR);
        // assertCache(role, TEST_FIELD, Language.FR, null, not(nullValue()));
    }

    @Test
    @Disabled
    public void getFieldValue2_cacheUsed() {
        evictCache(role, TEST_FIELD, Language.FR, null);

        translateService.getFieldValue(ROLE_CLASSNAME, role.getId(), TEST_FIELD, Language.FR);
        assertCache(role, TEST_FIELD, Language.FR, null, not(nullValue()));
    }

    private void evictCache(IIdentifiedModel<?> entity, String field, Language language, Long revision) {
        doOnCache(entity, field, language, revision, (cache, cacheKey) -> {
            cache.evict(cacheKey);
        });
    }

    private void assertCache(IIdentifiedModel<?> entity, String field, Language language, Long revision, Matcher<? super Cache.ValueWrapper> matcher) {
        doOnCache(entity, field, language, revision, (cache, cacheKey) -> {
            Cache.ValueWrapper valueWrapper = cache.get(cacheKey);
            assertThat(valueWrapper, matcher);
        });
    }

    private void doOnCache(IIdentifiedModel<?> entity, String field, Language language, Long revision, BiConsumer<Cache, String> cacheAndKey) {
        Cache cache = cacheManager.getCache(TRANSLATE_CACHE_REPO_NAME);
        String cacheKey = constructCacheKey(entity.getClass().getSimpleName(), role.getId(), field, language, revision);
        cacheAndKey.accept(cache, cacheKey);
    }

    @Test
    public void getTranslatedHash_CACHEABLE_ROLE_OK() {

        // ------- TEST CACHE FOR ROLE ENTITY---------
        Role roleAdmin = dataset.role_admin;

        Map<Language, String> translatedLabels1 = translateService.getFieldValues(roleAdmin, Role.FIELD_LABEL);
        Map<Language, String> translatedLabels2 = translateService.getFieldValues(roleAdmin, Role.FIELD_LABEL);
        assertEquals(translatedLabels1, translatedLabels2);

        // save by service
        saveTranslateByService(roleAdmin, Role.FIELD_LABEL, Language.EN, "KEY");
        saveTranslateByService(roleAdmin, Role.FIELD_LABEL, Language.FR, "CLE");

        Map<Language, String> translatedLabels4 = translateService.getFieldValues(roleAdmin, Role.FIELD_LABEL);
        Assertions.assertNotEquals(translatedLabels1, translatedLabels4);
    }

    @Test
    public void getTranslatedHash_GET_CACHE_ENTITY_NAME_DIFFERENT() {
        // test to get role 1, by passing another Entity Type, User for example
        Map<Language, String> translatedLabels1 = translateService.getFieldValues(dataset.user_simpleUser, Role.FIELD_LABEL);
        assertEquals(0, translatedLabels1.size());

        // now save post_a User and ensure we have something equal
        saveTranslateByService(dataset.user_simpleUser, Role.FIELD_LABEL, Language.EN, "KEY");
        Map<Language, String> translatedLabels2 = translateService.getFieldValues(dataset.user_simpleUser, Role.FIELD_LABEL);
        assertEquals("KEY", translatedLabels2.get(Language.EN));
    }

    /*@Test
    public void getTranslatedHash_GET_CACHE_ALL_PARAMS_NULL() {
        // test to get role 1, by passing another Entity Type, Damage for example
        Map<Language, String> translatedLabels = translateService.getFieldValues(null, null);
        assertEquals(translatedLabels.size(), 0);
    }*/

    /*@Test
    public void checkCacheData() {
        Role roleAdmin = dataset.role_adminRole;
        translateService.getFieldValues(roleAdmin, Role.FIELD_LABEL);

        String wantedKeyCachedStorage = constructCacheKey(Role.class.getSimpleName(), roleAdmin.getId(), Role.FIELD_LABEL, Language.FR);

        // check if key cache is respected like defined in method (getFieldValues)
        Cache cache = cacheManager.getCache(TRANSLATE_CACHE_REPO_NAME);
        assertCache(roleAdmin, Role.FIELD_LABEL, Language.FR, not(nullValue()));
        Cache.ValueWrapper valueWrapper1 = cache.get(wantedKeyCachedStorage);
        Assertions.assertNotNull(valueWrapper1.get());

        // check if after post_a second call to method getFieldValues, the data are the same
        translateService.getFieldValues(roleAdmin, Role.FIELD_LABEL);
        Cache.ValueWrapper valueWrapper2 = cache.get(wantedKeyCachedStorage);
        Assertions.assertNotNull(valueWrapper2.get());
        Assertions.assertEquals(valueWrapper1.get(), valueWrapper2.get());

        // evict cache and check we have nothing stored
        Map<Language, String> labels = new HashMap<>();
        labels.put(Language.EN, "KEY");
        labels.put(Language.FR, "clé");
        translateService.saveFieldValues(roleAdmin, Role.FIELD_LABEL, labels);
        Cache.ValueWrapper valueWrapper3 = cache.get(wantedKeyCachedStorage);
        Assertions.assertNull(valueWrapper3);

        // check with not existing entity id, result should be null
        roleAdmin.setId(notExistingEntityId);
        translateService.getFieldValues(roleAdmin, Role.FIELD_LABEL);
        cache = cacheManager.getCache(TRANSLATE_CACHE_REPO_NAME);
        Cache.ValueWrapper valueWrapper4 = cache.get(wantedKeyCachedStorage);
        Assertions.assertNull(valueWrapper4);
    }*/

    @Test
    public void saveGetFieldValue_CACHE_OK() {
        // save field
        translateService.saveFieldValue(Damage.class.getSimpleName(), dataset.damage_corrosion.getTechnicalId(), fieldName, Language.EN, fieldValue);

        Cache cache = cacheManager.getCache(TRANSLATE_CACHE_REPO_NAME);

        String wantedKeyCachedStorage = constructCacheKey(Damage.class.getSimpleName(), dataset.damage_corrosion.getTechnicalId(), fieldName);
        // ensure is existing in the cache
        Cache.ValueWrapper valueWrapper = cache.get(wantedKeyCachedStorage);
        Assertions.assertNull(valueWrapper);

        // should be null
        String value1 = translateService.getFieldValue(Damage.class.getSimpleName(), dataset.damage_corrosion.getTechnicalId(), fieldName, Language.EN);
        String value2 = translateService.getFieldValue(Damage.class.getSimpleName(), dataset.damage_corrosion.getTechnicalId(), fieldName, Language.EN);
        assertEquals(value1, fieldValue);
        assertEquals(value2, fieldValue);
    }

    /**
     * construct use key cache
     * example : key = "#className.concat('-').concat(#entityId).concat('-').concat(#field)")
     *
     * @param className
     * @param entityId
     * @param label
     * @param restArgs
     * @return
     */
    public String constructCacheKey(String className, Long entityId, String label, Object... restArgs) {
        StringBuilder res = new StringBuilder();
        // this is common key for each cache
        res.append(className + "-" + entityId + "-" + label);
        Arrays.stream(restArgs).forEach(e -> {
            res.append("-").append(e);
        });
        return res.toString();
    }

    // save translate by service
    public void saveTranslateByService(IIdentifiedModel entity, String label, Language language, String value) {
        Map<Language, String> labels = new HashMap<>();
        labels.put(language, value);
        translateService.saveFieldValues(entity, Role.FIELD_LABEL, labels);
    }
}
