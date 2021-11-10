package com.airbus.retex.media;

import com.airbus.retex.BaseControllerTest;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.helper.MediaHelper;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.service.media.IMediaService;
import com.airbus.retex.utils.ConstantUrl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class MediaControllerIT extends BaseControllerTest {
    @Autowired
    private IMediaService service;

    @Test
    public void postMediaWithUnauthenticatedUser_isForbidden() throws Exception {
        asUser = null;
        MockMultipartFile file = new MockMultipartFile("file", "filename.png", "text/plain", "some xml".getBytes());
        withRequest = multipart(ConstantUrl.API_POST_MEDIA_PRE_UPLOAD_FILES).file(file);
        expectedStatus = HttpStatus.UNAUTHORIZED;
        abstractCheck();
    }

    @Test
    public void preUploadTemporaryFile_Ok() throws Exception {
        asUser = dataset.user_simpleUser2;
        MockMultipartFile file = new MockMultipartFile("file", "filename.png", "text/plain", "some xml".getBytes());
        withRequest = multipart(ConstantUrl.API_POST_MEDIA_PRE_UPLOAD_FILES).file(file);
        expectedStatus = HttpStatus.OK;
        checkMedia()
                .andExpect(jsonPath("$.isFromThingworx", equalTo(false)));
    }

    @Test
    public void preUploadTemporaryFile_Format_Not_Accepted() throws Exception {
        asUser = dataset.user_simpleUser2;
        MockMultipartFile file = new MockMultipartFile("file", "filename.doc", "text/plain", "some xml".getBytes());
        withRequest = multipart(ConstantUrl.API_POST_MEDIA_PRE_UPLOAD_FILES).file(file);
        expectedStatus = HttpStatus.BAD_REQUEST;
        abstractCheck();
    }

    @Test
    public void getRawMediaByUUID() throws Exception {
        Media media = prepareGet();
        withRequest = get(ConstantUrl.API_GET_MEDIA, media.getUuid());
        asUser=dataset.user_simpleUser;
        expectedStatus = HttpStatus.OK;
        abstractCheck();
    }

    @Test
    public void getMediaThumbnailByUUID() throws Exception {
        Media media = prepareGet();
        withParams.add("width", "150");
        withParams.add("height", "150");
        withRequest = get(ConstantUrl.API_GET_MEDIA, media.getUuid());
        expectedStatus = HttpStatus.OK;
        asUser=dataset.user_simpleUser;
        abstractCheck();
    }

    @Test
    public void getMediaThumbnailByUUID_withMissingSize() throws Exception {
        Media media = prepareGet();
        withParams.add("width", "150");
        withRequest = get(ConstantUrl.API_GET_MEDIA, media.getUuid());
        expectedStatus = HttpStatus.BAD_REQUEST;
        asUser=dataset.user_simpleUser;
        abstractCheck();
    }

    private Media prepareGet() throws IOException, FunctionalException {
        InputStreamSource mediaStream = MediaHelper.loadStream();
        asUser = null;
        mediatype = MediaType.IMAGE_JPEG;
        return service.writeStream("h160.jpg", mediaStream);
    }
}
