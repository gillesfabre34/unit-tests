package com.airbus.retex.request;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.dto.request.RequestCreationDto;
import com.airbus.retex.business.dto.request.RequestDto;
import com.airbus.retex.helper.DtoHelper;
import com.airbus.retex.helper.IdentifiableDto;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.request.RequestFieldsEnum;
import com.airbus.retex.utils.ConstantUrl;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class RequestControllerCreateRequestIT extends BaseControllerTest {

    private RequestCreationDto dto;

    @BeforeEach
    public void setup(){
        dto = DtoHelper.generateValidCreationDto();
    }

    @Test
    public void createRequest_Ok() throws Exception {
        dto.setAirbusEntityId(dataset.airbusEntity_france.getId());
        asUser = dataset.user_simpleUser;
        this.dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.WRITE);

        withRequest = post(ConstantUrl.API_REQUESTS).content(objectMapper.writeValueAsBytes(dto));

        expectedStatus = HttpStatus.OK;

        ResultActions result = abstractCheck();
        result.andExpect(jsonPath("$.id", notNullValue()));
        result.andExpect(jsonPath("$.status", equalTo(EnumStatus.CREATED.name())));
        result.andExpect(jsonPath("$.name", equalTo("Request XY")));
        result.andExpect(jsonPath("$.description", equalTo("Test description")));
        result.andExpect(jsonPath("$.reference", nullValue()));
        result.andExpect(jsonPath("$.origin", nullValue()));
        result.andExpect(jsonPath("$.airbusEntity.id", equalTo(dataset.airbusEntity_france.getId().intValue())));
        result.andExpect(jsonPath("$.requester.id", equalTo(asUser.getId().intValue())));
        result.andExpect(jsonPath("$.creationDate", notNullValue()));

        // Check database state
        String response = result.andReturn().getResponse().getContentAsString();
        IdentifiableDto identifiableDto = objectMapper.readValue(response, IdentifiableDto.class); // cannot unmarshall to RequestDto because of the translated fields
        ModelMapper mapper = new ModelMapper();
        RequestDto responseDto = mapper.map(identifiableDto, RequestDto.class);

        // define reletionships to be fetched from DB
        JSONObject relationships = new JSONObject();
        JSONObject requester = new JSONObject();
        requester.put("roles", new JSONObject());
        relationships.put("airbusEntity", new JSONObject());
        relationships.put("requester", requester);
        Request r = databaseVerificationService.retrieveOne(responseDto, relationships);

        assertThat(r.getRequester().getLastName(), notNullValue());
        assertThat(r.getRequester().getRoles().size(), equalTo(1));
    }

    @Test
    public void createRequestWithNonExistingAirbusEntityId_Error() throws Exception {
        dto.setAirbusEntityId(0L);
        asUser = dataset.user_simpleUser;
        this.dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.WRITE);

        withRequest = post(ConstantUrl.API_REQUESTS).content(objectMapper.writeValueAsBytes(dto));

        expectedStatus = HttpStatus.NOT_FOUND;

        abstractCheck().andExpect(jsonPath("$.messages").value("The airbusEntity does not exists."));
    }

    @Test
    public void createRequestWithPastDueDate_Error() throws Exception {
        LocalDate previousDay =  LocalDate.now().minusDays(15);

        dto.setAirbusEntityId(dataset.airbusEntity_france.getId());
        dto.setDueDate(previousDay);
        asUser = dataset.user_simpleUser;
        this.dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.READ);

        withRequest = post(ConstantUrl.API_REQUESTS).content(objectMapper.writeValueAsBytes(dto));

        expectedStatus = HttpStatus.BAD_REQUEST;

        abstractCheck()
                .andExpect(jsonPath("$.messages").value("The field dueDate is invalid"));
    }

    @Test
    public void dueDateExpectedFormat() throws IOException {
        String tpl = "{\"translatedFields\":{\"EN\":{\"description\":\"Test description\",\"name\":\"Request XY\"},\"FR\":{\"description\":\"Description test\",\"name\":\"Requete XY\"}},\"airbusEntityId\":2,\"dueDate\":\"%s\"}";
        String dueDate = "2019-08-28";
        objectMapper.readValue(String.format(tpl, dueDate), RequestCreationDto.class);

    }

    @Test
    public void createRequestWithoutDescription() throws Exception {
        dto.setAirbusEntityId(dataset.airbusEntity_france.getId());
        dto.getTranslatedFields().get(Language.FR).remove(RequestFieldsEnum.description);
        dto.getTranslatedFields().get(Language.EN).remove(RequestFieldsEnum.description);
        asUser = dataset.user_superAdmin;
        this.dataSetInitializer.createUserFeature(FeatureCode.REQUEST, asUser, EnumRightLevel.WRITE);

        withRequest = post(ConstantUrl.API_REQUESTS).content(objectMapper.writeValueAsBytes(dto));

        expectedStatus = HttpStatus.OK;

        abstractCheck()
                .andExpect(jsonPath("$.status").value(EnumStatus.CREATED.name()))
                .andExpect(jsonPath("$.versionNumber").value(1));
    }

}
