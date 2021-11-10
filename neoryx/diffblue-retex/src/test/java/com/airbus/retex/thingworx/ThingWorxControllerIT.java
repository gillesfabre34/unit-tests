package com.airbus.retex.thingworx;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.dto.post.PostCustomThingworxDto;
import com.airbus.retex.business.dto.step.StepCustomThingworxDto;
import com.airbus.retex.business.dto.step.StepThingworxDto;
import com.airbus.retex.business.dto.thingWorx.RedirectURLDto;
import com.airbus.retex.config.RetexConfig;
import com.airbus.retex.dataset.DatasetFactory;
import com.airbus.retex.dataset.DrtDataset;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.messaging.WebsocketIdentifier;
import com.airbus.retex.model.post.Post;
import com.airbus.retex.persistence.messaging.WebsocketIdentifierRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ThingWorxControllerIT extends BaseControllerTest {
    private static final String API_THING_WORX_MEASURES = "/api/thingworx/measures";
    private static final String EXTERNAL_API_KEY_LABEL = "api-key";
    private static final String EXTERNAL_API_KEY_VALUE = "ziz88199.8iTfsc8dOOWssSs8jYq9Rn-A500DDAtl3";
    private static final String API_THING_WORX_URL = "/api/thingworx/url";
    private static final String API_THING_WORX_PRE_UPLOAD_FILE = "/api/thingworx/media/pre-upload";
    private static final UUID MEDIA_UUID = UUID.fromString("c60b41e6-0503-4b74-9c81-18903f3df961");

    @Autowired
    private WebsocketIdentifierRepository websocketIdentifierRepository;
    @Autowired
    private RetexConfig retexConfig;
    @Autowired
    private DatasetFactory datasetFactory;
    private ObjectMapper mapper = new ObjectMapper();

    private DrtDataset drtDataset;

    @BeforeEach
    private void before() {
        drtDataset = datasetFactory.createDrtDataset();
    }

    @Test
    public void getThingWorxUrl_Ok() throws Exception {
        asUser = dataset.user_superAdmin;
        expectedStatus = HttpStatus.OK;
        withParams.add("routingComponentId", drtDataset.requestDataset.routingDataset.partDataset.routingComponentDataset.routingComponentIndexDimensional.getNaturalId().toString());
        withParams.add("routingComponentVersionNumber","1");
        withParams.add("operationId",drtDataset.requestDataset.routingDataset.operationDimensional.getNaturalId().toString());
        withParams.add("drtId",drtDataset.drt.getId().toString());
        withParams.add("taskId",drtDataset.requestDataset.routingDataset.partDataset.functionalArea.getNaturalId().toString());
        withRequest = get(API_THING_WORX_URL).contentType(MediaType.APPLICATION_JSON);

        RedirectURLDto parsedResponse = mapper.readValue(abstractCheck().andReturn().getResponse().getContentAsByteArray(), RedirectURLDto.class);

        URL domain = new URL(parsedResponse.getUrl());
        Map<String, String> map = splitQuery(domain);

        assertFalse(map.get("steps").isEmpty());
        assertEquals(map.get("user"), asUser.getStaffNumber());
        assertEquals(map.get("context"), asUser.getId().toString());
        assertThat(map.get("steps"),notNullValue());
        assertThat(map.get("steps"),stringContainsInOrder(Arrays.asList("id","name","posts")));
    }

    public Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String query = url.toString().replace(retexConfig.getThingworxUrl() ,"");
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            if(!pair.isEmpty()) {
                int idx = pair.indexOf("=");
                query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
            }
        }
        return query_pairs;
    }

    @Test
    public void preUploadTemporaryFile_Ok() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "filename.png", "text/plain", "some xml".getBytes());
        withRequest = multipart(API_THING_WORX_PRE_UPLOAD_FILE).file(file);
        expectedStatus = HttpStatus.OK;
        asUser = null;
        HttpHeaders headers = new HttpHeaders();
        headers.add(EXTERNAL_API_KEY_LABEL, EXTERNAL_API_KEY_VALUE);
        withRequest.headers(headers);
        checkMedia();
    }

    /**
     * verify that we have received posted data from thingworx and pushed to front
     *
     * @throws Exception
     */
    @Test
    void thingworxPostMeasures_received_and_pushed_to_front_ok() throws Exception {
        buildPostRequest(HttpStatus.OK, dataset.websocketIdentifier_one.getId().toString(), EXTERNAL_API_KEY_VALUE);
        asUser = null;
        abstractCheck().andExpect(jsonPath("$.context").isNotEmpty())
                .andExpect(jsonPath("$.steps").isArray())
                .andExpect(jsonPath("$.steps").isNotEmpty())
                .andExpect(jsonPath("$.steps[0].mediaUuid").isNotEmpty())
                .andExpect(jsonPath("$.steps[0].posts[0].controlValue").value(1.2F))
                .andExpect(jsonPath("$.steps[0].posts[0].postId").isNotEmpty());
    }

    @Test
    void thingworxPostMeasures_user_not_connected_websocket() throws Exception {
        Optional<WebsocketIdentifier> wsidOptional = websocketIdentifierRepository.findByUserId(dataset.user_superAdmin.getId());
        WebsocketIdentifier wsid = wsidOptional.get();
        wsid.setConnected(false);
        websocketIdentifierRepository.save(wsid);
        buildPostRequest(HttpStatus.BAD_REQUEST, dataset.websocketIdentifier_one.getId().toString(), EXTERNAL_API_KEY_VALUE);
        asUser = null;
        checkFunctionalException("retex.error.thingworx.user.not.connected.push.service");
    }

    @Test
    void thingworxPostMeasures_post_context_param_not_found() throws Exception {
        buildPostRequest(HttpStatus.BAD_REQUEST, "", EXTERNAL_API_KEY_VALUE);
        asUser = null;
        checkFunctionalException("retex.error.thingworx.context.not.found");
    }

    @Test
    void thingworxPostMeasures_post_api_key_error() throws Exception {
        buildPostRequest(HttpStatus.UNAUTHORIZED, "1", "wrong_api_key");
        asUser = null;
        abstractCheck();
    }

    @Test
    void thingworxPostMeasures_post_api_key_ok() throws Exception {
        buildPostRequest(HttpStatus.OK, dataset.user_superAdmin.getId().toString(), EXTERNAL_API_KEY_VALUE);
        asUser = null;
        abstractCheck();
    }

    void buildPostRequest(HttpStatus status, String contextParam, String apiKey) throws JsonProcessingException {
        StepThingworxDto thingworxDto = new StepThingworxDto();
        List<Long> stepIds = List.of(dataset.step_one.getNaturalId(), dataset.step_two.getNaturalId());
        thingworxDto.setSteps(buildStepsDto(stepIds, dataset.post_a, dataset.post_b));
        // context is user ldap id !!!!
        thingworxDto.setContext(contextParam);
        asUser = dataset.user_superAdmin;
        withRequest = post(API_THING_WORX_MEASURES).content(objectMapper.writeValueAsString(thingworxDto));
        expectedStatus = status;
        HttpHeaders headers = new HttpHeaders();
        headers.add(EXTERNAL_API_KEY_LABEL, apiKey);
        withRequest.headers(headers);
    }


    public List<StepCustomThingworxDto> buildStepsDto(List<Long> stepsIds, Post... p) {
        List<StepCustomThingworxDto> steps = new ArrayList<>();
        for (Long id: stepsIds) {
            StepCustomThingworxDto e = new StepCustomThingworxDto();
            e.setMediaUuid(MEDIA_UUID);
            e.setPosts(createPostsDto(p));
            steps.add(e);
        }
        return steps;
    }

    public List<PostCustomThingworxDto> createPostsDto(Post... posts) {
        List<PostCustomThingworxDto> res = new ArrayList<>();
        for (Post p : posts) {
            PostCustomThingworxDto pt1 = new PostCustomThingworxDto();
            pt1.setControlValue(1.2F);
            pt1.setPostId((long) dataSetInitializer.getNext());
            pt1.setUnitName("mm");
            res.add(pt1);
        }
        return res;
    }

}
