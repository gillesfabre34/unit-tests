package com.airbus.retex.controller.thingworx;

import com.airbus.retex.business.authentication.CustomUserDetails;
import com.airbus.retex.business.dto.step.StepThingworxDto;
import com.airbus.retex.business.dto.thingWorx.RedirectURLDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.configuration.RetexSecurityConfiguration;
import com.airbus.retex.service.media.IMediaService;
import com.airbus.retex.service.thingworx.IThingWorxService;
import com.airbus.retex.utils.ConstantUrl;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping
@Api(value = "ThingWorx Api", tags = {"ThingWorx"})
public class ThingWorxController {

    @Autowired
    private IThingWorxService iThingWorxService;
    @Autowired
    private IMediaService mediaService;

    @ApiOperation("Get url of Thingworx")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Get url of Thingworx"),
            @ApiResponse(code = 400, message = "Impossible to get Thingworx url")})
    @GetMapping(ConstantUrl.API_THINGWORX_URL)
    public ResponseEntity<RedirectURLDto> getThingworxUrl(Authentication authentication,
                                                          @RequestParam("routingComponentId") Long routingComponentId,
                                                          @RequestParam("routingComponentVersionNumber") Long routingComponentVersionNumber,
                                                          @RequestParam("drtId") Long drtId,
                                                          @RequestParam("operationId") Long operationId,
                                                          @RequestParam("taskId") Long taskId) throws FunctionalException, IOException {
        CustomUserDetails customUserDetails = ((CustomUserDetails) authentication.getPrincipal());
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(iThingWorxService.getThingworxMainUrl(
                customUserDetails.getUser(), routingComponentId, routingComponentVersionNumber, drtId, operationId, taskId, customUserDetails.getLanguage().locale));
    }

    @ApiOperation("Pre-upload file to get temporary UUID, save files in temporary media table")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "The temporary uuid of the uploaded image"),
                    @ApiResponse(code = 400, message = "Impossible to upload image")
            })
    @PostMapping(ConstantUrl.API_THINGWORX_MEDIA_PREUPLOAD)
    @Secured("ROLE_API_KEY:READ")
    @ApiImplicitParam(name = RetexSecurityConfiguration.THING_WORX_API_KEY,
            value = RetexSecurityConfiguration.THING_WORX_API_KEY, required = true,
            allowEmptyValue = false, paramType = "header", example = "Api-key of thingworx")
    public ResponseEntity preUploadTemporaryFile(@RequestParam("file") MultipartFile file) throws FunctionalException {
        return ResponseEntity.ok().body(mediaService.saveThingworxTemporaryFile(file));
    }

    /**
     * This endpoint will be used from thingworx to post data (measures) to Retex server
     * When data is received from thingworx it will be pushed to FrondEnd across websocket push
     *
     * @return
     * @throws FunctionalException
     */
    @ApiOperation("Post measures from ThingWorx to Retex")
    @PostMapping(ConstantUrl.API_THINGWORX_MEASURES)
    @Secured("ROLE_API_KEY:READ")
    @ApiImplicitParam(name = RetexSecurityConfiguration.THING_WORX_API_KEY,
            value = RetexSecurityConfiguration.THING_WORX_API_KEY, required = true,
            allowEmptyValue = false, paramType = "header", example = "Api-key of thingworx")
    public ResponseEntity postThingworxMeasures(@RequestBody StepThingworxDto thingworxData) throws FunctionalException, IOException {
        return ResponseEntity.ok().body(iThingWorxService.postMeasures(thingworxData));
    }
}
