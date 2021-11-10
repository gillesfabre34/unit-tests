package com.airbus.retex.controller.media;

import com.airbus.retex.business.dto.media.ThumbnailSizeDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.TechnicalError;
import com.airbus.retex.config.ThumbnailSize;
import com.airbus.retex.model.media.AbstractMedia;
import com.airbus.retex.model.media.IBaseMedia;
import com.airbus.retex.service.impl.util.MediaUtil;
import com.airbus.retex.service.media.IMediaService;
import com.airbus.retex.utils.ConstantUrl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping
@Api(value = "Media api", tags = {"Media api"})
public class MediaController {

    @Autowired
    private IMediaService mediaService;

    /**
     * Get Media (image or files)
     *
     * @param uuid
     * @param sizeDto
     * @return
     */
    @ApiOperation("Get content of a media")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "The media content"),
                    @ApiResponse(code = 400, message = "The request body does not match the expected one")
            })
    @GetMapping(ConstantUrl.API_GET_MEDIA)
    public ResponseEntity getContentMediaByUUID(@PathVariable("uuid") UUID uuid, @Valid ThumbnailSizeDto sizeDto
    ) throws FunctionalException {
        IBaseMedia media = mediaService.retrieveMedia(uuid);
        if (null == media) {
            return ResponseEntity.notFound().build();
        }

        InputStreamSource inputStreamSource;

        if(sizeDto.isValid()){
            // case Thumbnail
            inputStreamSource = mediaService.readThumbnailStream(media, new ThumbnailSize(sizeDto.getWidth(), sizeDto.getHeight()));
        }else{
            // case raw file
            inputStreamSource = mediaService.readStream(media);
        }

        try {
            return new ResponseEntity(new InputStreamResource(inputStreamSource.getInputStream()), getMediaHeaderInfos(media), HttpStatus.OK);
        } catch (IOException e) {
            throw new TechnicalError("Unable to open stream", e);
        }
    }

    /**
     * Just retreive a temporary uuid, to be used later
     *
     * @param file
     * @return
     */
    @ApiOperation("Pre-upload file to get temporary UUID to use later on save request")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "The temporary uuid of the uploaded file"),
                    @ApiResponse(code = 400, message = "The request body does not match the expected one")
            })
    @PostMapping(ConstantUrl.API_POST_MEDIA_PRE_UPLOAD_FILES)
    public ResponseEntity preUploadTemporaryFile(
            @RequestParam("file") MultipartFile file
    ) throws FunctionalException {
            return ResponseEntity.ok().body(mediaService.saveTemporaryFile(file));
    }

    /**
     * @param media
     * @return
     */
    private HttpHeaders getMediaHeaderInfos(IBaseMedia media) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("content-disposition", "attachment; filename=" + MediaUtil.getFileNameWithoutPath(media.getFilename()));
        try {
            //FIXME content type should be calculated when the media is saved
            responseHeaders.add("Content-Type", Files.probeContentType(new File(MediaUtil.getFilenameForFormat(null, media.getFilename())).toPath()));
        } catch (IOException e) {
            throw new TechnicalError("Unable to read file", e);
        }
        return responseHeaders;
    }

}
