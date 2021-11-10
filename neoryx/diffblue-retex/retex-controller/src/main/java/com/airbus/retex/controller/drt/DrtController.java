package com.airbus.retex.controller.drt;

import com.airbus.retex.business.authentication.CustomUserDetails;
import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.drt.DrtDto;
import com.airbus.retex.business.dto.drt.DrtFilteringDto;
import com.airbus.retex.business.dto.drt.DrtHeaderDto;
import com.airbus.retex.business.dto.functionalArea.FunctionalAreaHeaderDto;
import com.airbus.retex.business.dto.inspection.InspectionDetailsDto;
import com.airbus.retex.business.dto.inspection.InspectionQCheckValueDto;
import com.airbus.retex.business.dto.inspection.InspectionValueDto;
import com.airbus.retex.business.dto.operation.ListDrtOperationDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.configuration.CustomLocalResolver;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.service.drt.IDrtQCheckService;
import com.airbus.retex.service.drt.IDrtService;
import com.airbus.retex.utils.ConstantUrl;
import com.airbus.retex.utils.ValidList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Locale;

@Api(value = "Drt", tags = {"Drt"})
@RestController
public class DrtController {

    @Autowired
    private IDrtService drtService;

    @Autowired
    private IDrtQCheckService drtQCheckService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private CustomLocalResolver customLocalResolver;


    @ApiOperation("Get all Operation of a Drt")
    @GetMapping(ConstantUrl.API_DRT_OPERATIONS)
    @Secured("ROLE_DRT_INTERNAL_INSPECTION:READ")
    public ResponseEntity<ListDrtOperationDto> getOperationsByDrtId(
            @PathVariable("id") @NotNull Long drtId) throws FunctionalException {
        return ResponseEntity.ok(drtService.getOperationsByDrtId(drtId));
    }
    @ApiOperation("Get list of drts with filters")
    @GetMapping(ConstantUrl.API_DRTS)
    @Secured("ROLE_DRT_INTERNAL_INSPECTION:READ")
    public ResponseEntity<PageDto<DrtDto>> getAllDrtsWithFilter(
            Authentication authentication,
            @Valid DrtFilteringDto drtFilteringDto) {
        return ResponseEntity.ok().body(drtService.findDrtsWithFilteringAndUserRoles(((CustomUserDetails) authentication.getPrincipal()).getUserId(), drtFilteringDto ));
    }
    @ApiOperation("get the header of a DRT")
    @GetMapping(ConstantUrl.API_DRT_HEADER)
    @Secured("ROLE_DRT_INTERNAL_INSPECTION:READ")
    public ResponseEntity<DrtHeaderDto> getDrtHeader(@PathVariable @NotNull Long id) throws FunctionalException {
        Locale locale = customLocalResolver.resolveLocale(httpServletRequest);
        return ResponseEntity.ok().body(drtService.getHeader(id, Language.languageFor(locale)));
    }
    @ApiOperation("Get DRT operation task header")
    @GetMapping(ConstantUrl.API_DRT_INSPECTION + "/header")
    @Secured("ROLE_DRT_INTERNAL_INSPECTION:READ")
    public ResponseEntity<FunctionalAreaHeaderDto> getOperationTaskHeader(
            @PathVariable("id") @NotNull Long drtId,
            @PathVariable("operationId") @NotNull Long operationNaturalId,
            @PathVariable("taskId") @NotNull Long taskNaturalId
    ) throws FunctionalException {
        return ResponseEntity.ok().body(drtService.getOperationTaskHeader(drtId, operationNaturalId, taskNaturalId));
    }
    @ApiOperation("Get DRT operation task")
    @GetMapping(ConstantUrl.API_DRT_INSPECTION)
    @Secured("ROLE_DRT_INTERNAL_INSPECTION:READ")
    public ResponseEntity<List<InspectionDetailsDto>> getInspectionDetails(
            @PathVariable("id") @NotNull Long id,
            @PathVariable("operationId") @NotNull Long operationNaturalId,
            @PathVariable("taskId") @NotNull Long taskNaturalId
    ) throws FunctionalException {
        return ResponseEntity.ok().body(drtService.getInspectionDetails(id, operationNaturalId, taskNaturalId));
    }
    @ApiOperation("put value DRT inspection detail")
    @PutMapping(ConstantUrl.API_DRT_INSPECTION)
    @Secured("ROLE_DRT_INTERNAL_INSPECTION:WRITE")
    public ResponseEntity<Void> putInspectionDetails(
            @PathVariable("id") @NotNull Long drtId,
            @PathVariable("operationId") @NotNull Long operationNaturalId,
            @PathVariable("taskId") @NotNull Long taskNaturalId,
            @RequestBody @Valid InspectionValueDto inspectionValueDto,
            @RequestParam(name = "validate", defaultValue = "false") boolean validate,
            Authentication authentication
    ) throws FunctionalException {
        drtService.putInspectionDetails(drtId, operationNaturalId, taskNaturalId, inspectionValueDto, ((CustomUserDetails) authentication.getPrincipal()).getUserId(), validate);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation("Close a DRT")
    @PutMapping(ConstantUrl.API_DRT)
    public ResponseEntity<Void> closeDrt(@PathVariable("id") @NotNull Long drtId,
                                         Authentication authentication) throws FunctionalException {
        if(((CustomUserDetails) authentication.getPrincipal()).isRole(List.of(RoleCode.TECHNICAL_RESPONSIBLE, RoleCode.ADMIN))) {
            drtService.closeDrt(drtId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    @ApiOperation("put Qcheck value DRT inspection detail")
    @PutMapping(ConstantUrl.API_DRT_INSPECTION_QCHECK)
    @Secured("ROLE_DRT_QUALITY_CHECK:WRITE")
    public ResponseEntity<Void> putInspectionQCheckDetails(
            @PathVariable("id") @NotNull Long drtId,
            @PathVariable("operationId") @NotNull Long operationNaturalId,
            @PathVariable("taskId") @NotNull Long taskNaturalId,
            @RequestBody @Valid ValidList<InspectionQCheckValueDto> inspectionQCheckValueDto,
            @RequestParam(name = "validate", defaultValue = "false") boolean validate,
            Authentication authentication
    ) throws FunctionalException {
        if(((CustomUserDetails) authentication.getPrincipal()).isRole(List.of(RoleCode.QUALITY_CONTROLLER, RoleCode.ADMIN))) {
            drtQCheckService.putInspectionQCheck(drtId, operationNaturalId, taskNaturalId, inspectionQCheckValueDto, ((CustomUserDetails) authentication.getPrincipal()).getUserId(), validate);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
