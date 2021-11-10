package com.airbus.retex.controller.request;

import com.airbus.retex.business.authentication.CustomUserDetails;
import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.request.*;
import com.airbus.retex.business.dto.user.UserLightDto;
import com.airbus.retex.business.dto.versioning.VersionDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.service.impl.request.RequestServiceImpl;

import com.airbus.retex.utils.ConstantUrl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(value = "Request", tags = {"Requests"})  // To check
@RestController
public class RequestController {

    @Autowired
    private RequestServiceImpl requestService;

    @ApiOperation("Gets list of requests with filters")
    @GetMapping(ConstantUrl.API_REQUESTS)
    @Secured("ROLE_REQUEST:READ") // Comment
    public ResponseEntity<PageDto<RequestDto>> getAllRequestsWithFilter(
            Authentication authentication,
            @Valid RequestFilteringDto requestFilteringDto
    ) {

        CustomUserDetails customUserDetails = ((CustomUserDetails) authentication.getPrincipal());
        return ResponseEntity.ok().body(requestService.findRequestsWithFilters(requestFilteringDto, customUserDetails.getLanguage()));
    }
    @ApiOperation("Gets list all requesters")
    @GetMapping(ConstantUrl.API_REQUEST_REQUESTERS)
    public ResponseEntity<List<UserLightDto>> getAllRequesters() {
        return ResponseEntity.ok().body(requestService.getAllRequesters());
    }
    @ApiOperation("Gets Request By Id")
    @GetMapping(ConstantUrl.API_REQUEST)
    @Secured("ROLE_REQUEST:READ")
    public ResponseEntity<RequestFullDto> getRequestById(
            @PathVariable Long id,
            @RequestParam(value = "version", required = false) Long version) throws FunctionalException {
        RequestFullDto request = requestService.findRequestById(id, version);
        return ResponseEntity.ok().body(request);
    }
    @ApiOperation("Creates a request")
    @PostMapping(ConstantUrl.API_REQUESTS)
    @Secured("ROLE_REQUEST:WRITE")
    public ResponseEntity<RequestDto> createRequest(
            Authentication authentication,
            @RequestBody @Valid RequestCreationDto requestCreationDto
    ) throws FunctionalException {
        CustomUserDetails customUserDetails = ((CustomUserDetails) authentication.getPrincipal());
        return ResponseEntity.ok().headers(new HttpHeaders())
                .body(requestService.createRequest(requestCreationDto, customUserDetails.getUser()));
    }
    @ApiOperation("Updates Parent Request details")
    @PutMapping(ConstantUrl.API_REQUEST_UPDATE_FALSE)
    @Secured("ROLE_REQUEST:WRITE")
    public ResponseEntity updateParentRequestDetailsSave(@PathVariable Long id,
                                                         @RequestBody RequestUpdateDto requestDetailsDto) throws FunctionalException {
        requestService.updateRequestDetail(id, requestDetailsDto, false);
        return ResponseEntity.noContent().build();
    }
    @ApiOperation("Updates Parent Request details")
    @PutMapping(ConstantUrl.API_REQUEST_UPDATE_TRUE)
    @Secured("ROLE_REQUEST:WRITE")
    public ResponseEntity<RequestDetailsDto> updateParentRequestDetailsValidate(@PathVariable Long id,
                                                                                @RequestBody @Valid RequestUpdateDto requestDetailsDto) throws FunctionalException {
        return ResponseEntity.ok().body(requestService.updateRequestDetail(id, requestDetailsDto, true));
    }
    @ApiOperation("Updates a request")
    @PutMapping(ConstantUrl.API_REQUEST)
    @Secured("ROLE_REQUEST:WRITE")
    public ResponseEntity<RequestDto> updateRequest(
            Authentication authentication,
            @PathVariable("id") Long id,
            @RequestBody @Valid RequestCreationDto requestCreationDto
    ) throws FunctionalException {
        RequestDto createdRequest = requestService.updateRequest(id, requestCreationDto);
        return ResponseEntity.ok().headers(new HttpHeaders()).body(createdRequest);
    }
    @ApiOperation("Gets list of technical managers of request")
    @GetMapping(ConstantUrl.API_REQUEST_TECHNICAL_MANAGERS)
    @Secured("ROLE_REQUEST:READ")
    public ResponseEntity<List<String>> getTechnicalManagersOfRequest(Authentication authentication,
                                                                      @PathVariable("id") Long id) throws FunctionalException {
        return ResponseEntity.ok().body(requestService.getTechnicalManagersOfRequest(id));
    }
    @ApiOperation("Deletes a request")
    @DeleteMapping(ConstantUrl.API_REQUEST_DELETE)
    @Secured("ROLE_REQUEST:WRITE")
    public ResponseEntity<Void> deleteRequest(@PathVariable("id") Long id) throws FunctionalException {
        requestService.deleteRequest(id);
        return ResponseEntity.noContent().build();
    }
    @ApiOperation("Updates request status with CLOSE, DONE, COMPLETED, .....")
    @PutMapping(value = ConstantUrl.API_REQUEST_UPDATE_STATUS, params = "status")
    @Secured("ROLE_REQUEST:WRITE")
    public ResponseEntity<Void> updateRequestStatus(@PathVariable Long id,
                                                    @RequestParam(value = "status", required = true) EnumStatus status) throws FunctionalException {
        if (status == null) {
            throw new FunctionalException("retex.error.request.invalid.status");
        }
        if (status.equals(EnumStatus.CREATED) || status.equals(EnumStatus.DELETED)){
            throw new FunctionalException("retex.error.request.invalid.status.update.value");
        }
        requestService.updateRequestStatus(id, status);
        return ResponseEntity.noContent().build();
    }
    @ApiOperation("Gets request versions")
    @GetMapping(ConstantUrl.API_REQUEST_GET_VERSIONS)
    @Secured("ROLE_REQUEST:READ")
    public ResponseEntity<List<VersionDto>> getRequestVersions(Authentication authentication,
                                                               @PathVariable("id") Long id) {
        return ResponseEntity.ok().body(requestService.getVersions(Request.class, id));
    }
}
