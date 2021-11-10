package com.airbus.retex.damage;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumActiveState;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.damage.Damage;
import com.airbus.retex.model.functionality.Functionality;
import com.airbus.retex.model.functionality.damage.FunctionalityDamage;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.persistence.damage.DamageRepository;
import com.airbus.retex.service.damage.IDamageService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.HashSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class DamageControllerIT extends BaseControllerTest {

    private static final String API_DAMAGE_LIBRARY = "/api/damages";
    private static final String API_DELETE_DAMAGED_FUNCTIONALITY = API_DAMAGE_LIBRARY + "/{id}/functionality/{functionalityID}";
    private static final String API_GET_FUNCTIONALITY_DAMAGES = API_DAMAGE_LIBRARY + "/{id}/functionality/{functionalityID}";
    private static final String API_REVOKE_FUNCTIONALITYDAMGE_OF_DAMAGE = API_DAMAGE_LIBRARY + "/{id}/functionalityDamage/{functionalityDamageID}";
    private static final String API_ADD_DAMAGE_IMAGE = API_DAMAGE_LIBRARY + "/{id}/image";
    private static final String API_UPDATE_DAMAGE = API_DAMAGE_LIBRARY + "/{id}";
    private static final String API_UPDATE_DAMAGE_PROCESSING_STATUS = API_DAMAGE_LIBRARY + "/{id}/status";
    private static final String API_FUNCTIONALITY_DAMAGE_TO_DAMAGE = API_DAMAGE_LIBRARY +"/{id}/functionality/{functionalityID}";
    private static final String API_UPDATE_FUNCTIONALITY_DAMAGE = API_DAMAGE_LIBRARY + "/{id}/functionalityDamage/{functionalityDamageID}/update";
    private static final String API_ADD_FUNCTIONALITY_DAMAGE_IMAGE = API_DAMAGE_LIBRARY +"/{id}/functionalityDamage/{functionalityDamageID}/image";
    private static final String API_DELETE_DAMAGE_MEDIA = API_DAMAGE_LIBRARY +"/{id}/media";

    @Autowired
    private IDamageService damageService;

    @Autowired
    private DamageRepository damageRepository;

    private Damage damageToTest;
    private FunctionalityDamage functionalityDamageToTest;
    private Functionality functionalityToAdd;
    private Media mediaToTest;

    @BeforeEach
    public void before(){
        dataSetInitializer.createUserFeature(FeatureCode.DAMAGE, dataset.user_superAdmin, EnumRightLevel.WRITE);

        runInTransaction(() -> {
            damageToTest = dataSetInitializer.createDamage(EnumActiveState.ACTIVE, "test");
            functionalityDamageToTest = dataSetInitializer.createFunctionalityDamage(dataset.functionality_teeth, damageToTest);
            mediaToTest = dataSetInitializer.createMedia();

            damageToTest.setImages(new HashSet<>(Arrays.asList(mediaToTest)));

            functionalityToAdd = dataSetInitializer.createFunctionality();
        });
    }

    // ------------------------------------------------------------------------------------------
    // --------------------------------------- DAMAGE -------------------------------------------
    // ------------------------------------------------------------------------------------------

    @Test
    public void createDamage_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        ObjectNode createdObjectNode = objectMapper.createObjectNode();
        createdObjectNode.putObject("translatedFields")
                .putObject("FR")
                .put("coloration", "coloration")
                .put("density", "densité")
                .put("morphology", "morphologie")
                .put("name", "nom")
                .put("position", "position")
                .put("potentialRootCause", "cause principale")
                .put("size", "taille");
        createdObjectNode.putObject("translatedFields")
                .putObject("EN")
                .put("coloration", "coloration")
                .put("name", "name")
                .put("position", "position")
                .put("potentialRootCause", "potentialRootCause")
                .put("size", "size");

        withRequest = multipart(API_DAMAGE_LIBRARY)
                .file(new MockMultipartFile("file1", "file_1.txt", "text/plain", "test file 1 content".getBytes()))
                .file(new MockMultipartFile("file2", "file_2.txt", "text/plain", "test file 2 content".getBytes()))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .content(createdObjectNode.toString());
        expectedStatus = HttpStatus.OK;
        abstractCheck()
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.translatedFields", notNullValue()));
        //FIXME verifier qu'on post_a recu exactement les memes donnees .andExpect(jsonPath("$.translatedFields",notNullValue()));
    }

    @Test
    public void addDamageImage_OK() throws Exception {
        Long id  = dataset.damage_corrosion.getTechnicalId();
        int initialCount = damageService.getDamage(id, null).getImages().size();
        asUser = dataset.user_superAdmin;
        withRequest = multipart(API_ADD_DAMAGE_IMAGE, id)
                .file(testMultipartImage());
        expectedStatus = HttpStatus.OK;
        checkMedia();

        int finalCount = damageService.getDamage(id, null).getImages().size();
        assertThat(finalCount, equalTo(initialCount + 1));
    }

    @Test
    public void updateDamage_OK() throws Exception {
        objectMapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        asUser = dataset.user_superAdmin;
        ObjectNode createdObjectNode = objectMapper.createObjectNode();
        String colorationUpdate = "coloration mise à jour";
        String densityUpdate = "densité mise à jour";
        String morphologyUpdate = "morphologie mise à jour";
        String name = "nom mise à jour";
        String position = "position mise à jour";
        String potentialRootCause ="cause principale mise à jour";

        final ObjectNode translatedFields = createdObjectNode.putObject("translatedFields");

        translatedFields.putObject("FR")
                .put("coloration", colorationUpdate)
                .put("density", densityUpdate)
                .put("morphology", morphologyUpdate)
                .put("name", name)
                .put("position", position)
                .put("potentialRootCause", potentialRootCause)
                .put("size", "taille mise à jour");
        translatedFields.putObject("EN")
                .put("coloration", colorationUpdate)
                .put("density", densityUpdate)
                .put("morphology", morphologyUpdate)
                .put("name", name)
                .put("position", position)
                .put("potentialRootCause", potentialRootCause)
                .put("size", "taille mise à jour");

        withRequest = put(API_UPDATE_DAMAGE, dataset.damage_corrosion.getNaturalId())
                .content(createdObjectNode.toString());
        expectedStatus = HttpStatus.OK;
        abstractCheck()
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.translatedFields", notNullValue()))
                .andExpect(jsonPath("$.translatedFields.FR.coloration", equalTo(colorationUpdate)))
                .andExpect(jsonPath("$.translatedFields.FR.density", equalTo(densityUpdate)))
                .andExpect(jsonPath("$.translatedFields.FR.morphology", equalTo(morphologyUpdate)))
                .andExpect(jsonPath("$.translatedFields.FR.name", equalTo(name)))
                .andExpect(jsonPath("$.translatedFields.FR.position", equalTo(position)))
                .andExpect(jsonPath("$.translatedFields.FR.potentialRootCause", equalTo(potentialRootCause)));
    }

    @Test
    public void getDamage_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        var url = API_DAMAGE_LIBRARY + "/" + dataset.damage_corrosion.getNaturalId();
        withRequest = get(url);
        expectedStatus = HttpStatus.OK;
        abstractCheck()
                .andExpect(jsonPath("$.id", equalTo(dataset.damage_corrosion.getNaturalId().intValue())))
                .andExpect(jsonPath("$.translatedFields", notNullValue()));
        //FIXME verifier egalement que la functionality damage n'est pas null .andExpect(jsonPath("$.translatedFields",notNullValue()));
    }

    @Test
    public void getAll_ACTIVE_Damages_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_DAMAGE_LIBRARY).param("statusFilter", EnumActiveState.ACTIVE.name());
        expectedStatus = HttpStatus.OK;

        Specification<Damage> specification = Specification.where(null);
        specification = specification.and(isState(EnumActiveState.ACTIVE));

        abstractCheck()
                .andExpect(jsonPath("$", hasSize(damageRepository.findAllLastVersions(specification).size())))
                .andExpect(jsonPath("$[0].id", equalTo(dataset.damage_corrosion.getNaturalId().intValue())))
                .andExpect(jsonPath("$[0].name", notNullValue()));
    }

    @Test
    public void getAll_REVOKE_Damages_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_DAMAGE_LIBRARY).param("statusFilter", EnumActiveState.REVOKED.name());
        expectedStatus = HttpStatus.OK;

        Specification<Damage> specification = Specification.where(null);
        specification = specification.and(isState(EnumActiveState.REVOKED));

        abstractCheck()
                .andExpect(jsonPath("$", hasSize(damageRepository.findAllLastVersions(specification).size())))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].name", notNullValue()));
    }

    @Test
    public void revokeDamage() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = delete(API_UPDATE_DAMAGE, damageToTest.getNaturalId());
        expectedStatus = HttpStatus.NO_CONTENT;
    }

    @Test
    public void updateStatusOfDamage_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = put(API_UPDATE_DAMAGE_PROCESSING_STATUS, dataset.damage_corrosion.getNaturalId()).content(EnumStatus.CREATED.name());
        expectedStatus = HttpStatus.OK;
        abstractCheck().andExpect(jsonPath("$.status").value(EnumStatus.CREATED.name()));
    }


    // ------------------------------------------------------------------------------------------
    // --------------------------------- DAMAGE FUNCTIONALITIES ---------------------------------
    // ------------------------------------------------------------------------------------------
    @Test
    public void deleteDamagedFunctionalityOfDamage_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = delete(API_DELETE_DAMAGED_FUNCTIONALITY, damageToTest.getNaturalId(), dataset.functionality_teeth.getId());
        expectedStatus = HttpStatus.OK;
        // expect 2
        assertEquals("true", abstractCheck().andReturn().getResponse().getContentAsString());
    }

    @Test
    public void getFunctionalityDamages_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = get(API_GET_FUNCTIONALITY_DAMAGES, 1, 1);
        expectedStatus = HttpStatus.OK;
        abstractCheck();
    }

    @Test
    public void revokeFunctionalityDamageOfDamage_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = delete(API_REVOKE_FUNCTIONALITYDAMGE_OF_DAMAGE, damageToTest.getNaturalId(), functionalityDamageToTest.getNaturalId());
        expectedStatus = HttpStatus.NO_CONTENT;
    }

    @Test
    public void addFunctionalityDamageToDamage_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        ObjectNode createdObjectNode = objectMapper.createObjectNode();
        createdObjectNode.putObject("translatedFields")
                .putObject("FR")
                .put("reference", "Exemple de référence")
                .put("description", "Exemple de description");

        withRequest = post(API_FUNCTIONALITY_DAMAGE_TO_DAMAGE, dataset.damage_corrosion.getNaturalId(), dataset.functionality_bearingRaces.getId())
                .content(createdObjectNode.toString());
        expectedStatus = HttpStatus.NO_CONTENT;
        abstractCheck();
    }

    @Test
    public void addImageToFunctionalityDamage_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        withRequest = multipart(API_ADD_FUNCTIONALITY_DAMAGE_IMAGE, damageToTest.getNaturalId(), functionalityDamageToTest.getNaturalId())
                .file(testMultipartImage());
        expectedStatus = HttpStatus.OK;
        checkMedia();
    }

    @Test
    public void updateFunctionalityDamage_OK() throws Exception {
        asUser = dataset.user_superAdmin;
        ObjectNode createdObjectNode = objectMapper.createObjectNode();
        createdObjectNode.putObject("translatedFields")
                .putObject("EN")
                .put("reference", "X Reference example ")
                .put("description", "X Description example");

        withRequest = put(API_UPDATE_FUNCTIONALITY_DAMAGE, damageToTest.getNaturalId(), functionalityDamageToTest.getNaturalId())
                .content(createdObjectNode.toString());

        expectedStatus = HttpStatus.OK;
        abstractCheck()
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.translatedFields", notNullValue()));
        //FIXME VERIFIER QUE LES DONNEES SONT BIEN MISE A JOURS
    }

    @Test
    public void deleteDamageMedia_OK() throws Exception {
        asUser = dataset.user_simpleUser;
        this.dataSetInitializer.createUserFeature(FeatureCode.DAMAGE, asUser, EnumRightLevel.WRITE);

        withRequest = delete(API_DELETE_DAMAGE_MEDIA, damageToTest.getNaturalId()).param("uuid", mediaToTest.getUuid().toString());
        expectedStatus = HttpStatus.OK;
        assertEquals("true", abstractCheck().andReturn().getResponse().getContentAsString());
    }

    @Test
    public void deleteDamageMedia_Exception() throws Exception {
        asUser = dataset.user_simpleUser;
        this.dataSetInitializer.createUserFeature(FeatureCode.DAMAGE, asUser, EnumRightLevel.WRITE);
        // set a unknown uuid
        withRequest = delete(API_DELETE_DAMAGE_MEDIA, damageToTest.getNaturalId()).param("uuid", "309a7558-d92a-11e9-8a34-2a2ae2dbcce4");
        expectedStatus = HttpStatus.BAD_REQUEST;
        checkFunctionalException("retex.error.media.not.found");
    }

    private Specification<Damage> isState(EnumActiveState enumState){
        return (root, query, cb) -> cb.equal(root.get("state"), enumState);
    }
}
