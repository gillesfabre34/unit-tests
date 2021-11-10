package com.airbus.retex.controller.childRequest;

import com.airbus.retex.business.authentication.CustomUserDetails;
import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.childRequest.ChildRequestDetailDto;
import com.airbus.retex.business.dto.childRequest.ChildRequestFilteringDto;
import com.airbus.retex.business.dto.childRequest.ChildRequestFullDto;
import com.airbus.retex.business.dto.childRequest.ChildRequestLightDto;
import com.airbus.retex.business.dto.versioning.VersionDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.childrequest.ChildRequest;

import com.airbus.retex.service.impl.childRequest.ChildRequestServiceImpl;
import com.airbus.retex.utils.ConstantUrl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(value = "ChildRequest", tags = {"Child Requests"})
@RestController
public class ChildRequestController {

    @Autowired
    private ChildRequestServiceImpl childRequestService;


    @ApiOperation("Gets list of child requests of request")
    @GetMapping(ConstantUrl.API_CHILD_REQUESTS)
    @Secured("ROLE_REQUEST:READ")
    public ResponseEntity<PageDto<ChildRequestLightDto>> getAllChildRequestsOfRequest(Authentication authentication,
                                                                                      @PathVariable("id") Long id,
                                                                                      @Valid ChildRequestFilteringDto childRequestFilteringDto){
        CustomUserDetails customUserDetails = ((CustomUserDetails) authentication.getPrincipal());
        return ResponseEntity.ok().body(childRequestService.findFilteredChildRequestOfRequest(childRequestFilteringDto, id, customUserDetails.getLanguage()));
    }
    @ApiOperation("Gets child request by id")
    @GetMapping(ConstantUrl.API_CHILD_REQUEST)
    @Secured("ROLE_REQUEST:READ")
    public ResponseEntity<ChildRequestFullDto> getOneChildRequest(Authentication authentication,
                                                                  @PathVariable("id") Long id, //FIXME : Synchronise with front TEAM for removing this param from their CALL
                                                                  @PathVariable("childRequestId") Long childRequestId,
                                                                  @RequestParam(value = "version", required = false) Long version) throws FunctionalException {
        CustomUserDetails customUserDetails = ((CustomUserDetails) authentication.getPrincipal());
        return ResponseEntity.ok().body(childRequestService.getChildRequest(childRequestId, version, customUserDetails.getLanguage()));
    }
    @ApiOperation("Deletes child request")
    @DeleteMapping(ConstantUrl.API_CHILD_REQUEST_DELETE)
    @Secured("ROLE_REQUEST:WRITE")
    public ResponseEntity<Void> deleteChildRequest(@PathVariable("id") Long id, //FIXME : Synchronise with front TEAM for removing this param from their CALL
                                                   @PathVariable("childRequestId") Long childRequestId) throws  FunctionalException {
        childRequestService.deleteChildRequest(childRequestId);
        return ResponseEntity.noContent().build();
    }
    @ApiOperation("Changes child request status")
    @PutMapping(value = ConstantUrl.API_CHILD_REQUEST_UPDATE_STATUS, params = "status")
    @Secured("ROLE_REQUEST:WRITE")
    public ResponseEntity<Void> updateChildRequestStatus(@PathVariable("id") Long id, //FIXME : Synchronise with front TEAM for removing this param from their CALL
                                                         @PathVariable("childRequestId") Long childRequestId,
                                                         @RequestParam(value = "status", required = true) EnumStatus status) throws FunctionalException {
        if (status == null) {
            throw new FunctionalException("retex.error.childrequest.invalid.status");
        }
        if (status.equals(EnumStatus.CREATED) || status.equals(EnumStatus.DELETED)){
            throw new FunctionalException("retex.error.childrequest.invalid.status.update.value");
        }
        childRequestService.updateChildRequestStatus(childRequestId, status);
        return ResponseEntity.noContent().build();
    }
    @ApiOperation("Creates a child Request ")
    @PostMapping(ConstantUrl.API_CHILD_REQUESTS)
    @Secured("ROLE_REQUEST:WRITE")
    public ResponseEntity<ChildRequestDetailDto> createChildRequest(@PathVariable("id") Long id,
                                             @RequestBody @Valid ChildRequestDetailDto childRequestCreateDto) throws FunctionalException {
        return childRequestService.createChildRequest(id, childRequestCreateDto);
    }
    @ApiOperation("Updates a child request")
    @PutMapping(value = ConstantUrl.API_CHILD_REQUEST_UPDATE ,params = "!status")
    @Secured("ROLE_REQUEST:WRITE")
    public ResponseEntity<ChildRequestDetailDto> updateChildRequest(
            @PathVariable("id") Long id, //FIXME : Synchronise with front TEAM for removing this param from their CALL
            @PathVariable("childRequestId") Long childRequestId,
            @RequestBody ChildRequestDetailDto childRequestUpdateDto
    ) throws FunctionalException {
        return ResponseEntity.ok(childRequestService.updateChildRequest(childRequestId, childRequestUpdateDto) );
    }
    @ApiOperation("Gets Child request versions")
    @GetMapping(ConstantUrl.API_CHILD_REQUEST_GET_VERSIONS)
    @Secured("ROLE_REQUEST:READ")
    public ResponseEntity<List<VersionDto>> getChildRequestVersions(Authentication authentication,
                                                               @PathVariable("childRequestId") Long childRequestId){
        return ResponseEntity.ok().body(childRequestService.getVersions(ChildRequest.class, childRequestId));
    }
}
