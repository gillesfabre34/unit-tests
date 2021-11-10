package com.airbus.retex.controller.routing;

import java.util.List;

import javax.validation.Valid;

import com.airbus.retex.utils.ValidList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.airbus.retex.business.authentication.CustomUserDetails;
import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.functionalArea.FunctionalAreaFullDto;
import com.airbus.retex.business.dto.inspection.RoutingInspectionDetailHighlightDto;
import com.airbus.retex.business.dto.operation.ListOperationDto;
import com.airbus.retex.business.dto.operation.ManageOperationDto;
import com.airbus.retex.business.dto.operation.OperationFullDto;
import com.airbus.retex.business.dto.routing.RoutingCreatedDto;
import com.airbus.retex.business.dto.routing.RoutingCreationDto;
import com.airbus.retex.business.dto.routing.RoutingDto;
import com.airbus.retex.business.dto.routing.RoutingFilteringDto;
import com.airbus.retex.business.dto.routing.RoutingFullDto;
import com.airbus.retex.business.dto.step.StepUpdateDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.controller.VersionsController;
import com.airbus.retex.model.user.User;
import com.airbus.retex.service.impl.routing.RoutingServiceImpl;
import com.airbus.retex.service.routing.IRoutingService;
import com.airbus.retex.utils.ConstantUrl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Routing", tags = {"Routing"})
@RequestMapping(ConstantUrl.API_ROUTINGS)
@RestController
public class RoutingController extends VersionsController<RoutingServiceImpl> {

    @Autowired
    private IRoutingService routingService;

    @ApiOperation("Get list of Routing")
    @GetMapping("")
    @Secured("ROLE_ROUTING:READ")
    public ResponseEntity<PageDto<RoutingDto>> getAllRoutings(
            @Valid RoutingFilteringDto routingFiltering) {
        return ResponseEntity.ok().body(routingService.findRoutings(routingFiltering));
    }
    @ApiOperation("Get Routing By Id")
    @GetMapping("/{id}")
    @Secured("ROLE_ROUTING:READ")
    public ResponseEntity<RoutingFullDto> getRouting(
            @PathVariable(value = "id") Long naturalId,
            @Nullable @RequestParam(value = "version", required = false) Long version
    ) throws NotFoundException {
        RoutingFullDto routing = routingService.findRoutingById(naturalId, version);
        return ResponseEntity.ok(routing);
    }
    @ApiOperation("Delete a routing")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Routing deleted successfully"),
    })
    @DeleteMapping("/{id}")
    @Secured("ROLE_ROUTING:WRITE")
    public ResponseEntity<Void> deleteRouting(@PathVariable(value = "id") Long naturalId) throws FunctionalException {
        routingService.deleteRouting(naturalId);
        return ResponseEntity.noContent().build();
    }
    @ApiOperation("Create a Routing")
    @PostMapping("")
    @Secured("ROLE_ROUTING:WRITE")
    public ResponseEntity<RoutingCreatedDto> createRouting(Authentication authentication,
                                                           @RequestBody @Valid RoutingCreationDto routingCreationDto) throws FunctionalException {
        User creator = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        return ResponseEntity.status(HttpStatus.CREATED).body(routingService.createRouting(routingCreationDto, creator));
    }
    @ApiOperation("Manage Operation of a Routing")
    @PutMapping("/{routingId}/operations")
    @Secured("ROLE_ROUTING:WRITE")
    public ResponseEntity<List<OperationFullDto>> manageRoutingOperation(@PathVariable(value = "routingId") Long naturalId,
                                                                         @RequestBody @Valid ManageOperationDto manageOperationDto
    ) throws FunctionalException {
        return ResponseEntity.ok(routingService.manageOperation(naturalId, manageOperationDto));
    }
    @ApiOperation("Duplicate a routing")
    @PostMapping("/{id}/duplicate")
    @Secured("ROLE_ROUTING:WRITE")
    public ResponseEntity<RoutingCreatedDto> createDuplicateRouting(Authentication authentication,
                                                                      @PathVariable(value = "id") Long oldRoutingNaturalId,
                                                                      @RequestBody @Valid RoutingCreationDto routingCreationDto) throws Exception {
        User creator = ((CustomUserDetails) authentication.getPrincipal()).getUser();
        try {
            RoutingCreatedDto routing = routingService.duplicateRouting(routingCreationDto, creator, oldRoutingNaturalId);
            return ResponseEntity.ok(routing);
        } catch (FunctionalException exception) {
            return ResponseEntity.badRequest().build();
        }
    }
    @ApiOperation("Get Routing Inspection header")
    @GetMapping("/{routingId}/operations/{operationId}/tasks/{taskId}/header")
    @Secured("ROLE_ROUTING:READ")
    public ResponseEntity<FunctionalAreaFullDto> getRoutingInspectionHeader(
            @PathVariable("routingId") Long routingNaturalId,
            @PathVariable("operationId") Long operationNaturalId,
            @PathVariable("taskId") Long functionalityNaturalId,
            @Nullable @RequestParam(value = "version", required = false) Long version
    ) throws FunctionalException {
        FunctionalAreaFullDto routingInspectionHeader = routingService.getFunctionalAreaDtoByRoutingAndTaskId(routingNaturalId, operationNaturalId, functionalityNaturalId, version);
        return ResponseEntity.ok(routingInspectionHeader);
    }
    @ApiOperation("Get all Operation of a Routing")
    @GetMapping("/{routingId}/operations")
    @Secured("ROLE_ROUTING:READ")
    public ResponseEntity<ListOperationDto> getRoutingOperationById(
            @PathVariable("routingId") Long routingNaturalId,
            @Nullable @RequestParam(value = "version", required = false) Long version
    ) throws NotFoundException {
        return ResponseEntity.ok(routingService.getOperationByRoutingId(routingNaturalId, version));
    }
    @ApiOperation("Get Routing Inspection details")
    @GetMapping("/{routingId}/operations/{operationId}/tasks/{taskId}")
    @Secured("ROLE_ROUTING:READ")
    public ResponseEntity<RoutingInspectionDetailHighlightDto> getRoutingInspectionDetails(
            @PathVariable("routingId") Long routingNaturalId,
            @PathVariable("operationId") Long operationNaturalId,
            @PathVariable("taskId") Long taskNaturalId,
            @Nullable @RequestParam(value = "version", required = false) Long version
    ) throws FunctionalException {
        return ResponseEntity.ok(routingService.getListInspectionByOperationAndTask(routingNaturalId, operationNaturalId, taskNaturalId, version));
    }
    @ApiOperation("Put Routing Inspection details")
    @PutMapping("/{routingId}/operations/{operationId}/tasks/{taskId}")
    @Secured("ROLE_ROUTING:WRITE")
    public ResponseEntity putRoutingInspectionDetails(@PathVariable("routingId") Long routingNaturalId,
                                                      @PathVariable("operationId") Long operationNaturalId,
                                                      @PathVariable("taskId") Long taskNaturalId,
                                                      @RequestBody @Valid ValidList<StepUpdateDto> stepUpdateDtoList) throws FunctionalException {
        routingService.putListInspectionDetailByPostId(routingNaturalId, operationNaturalId, taskNaturalId, stepUpdateDtoList);
        return ResponseEntity.noContent().build();
    }
    @ApiOperation("Put Routing Status Validated")
    @PutMapping("/{id}/publish")
    @Secured("ROLE_ROUTING:WRITE")
    public ResponseEntity putRoutingStatusValidated(@PathVariable("id") Long naturalId) throws FunctionalException {
        routingService.setRoutingStatusToValidated(naturalId);
        return ResponseEntity.noContent().build();
    }
    @ApiOperation("Put Routing update to latest validated part version")
    @PutMapping("/{id}/update-part")
    @Secured("ROLE_ROUTING:WRITE")
    public ResponseEntity putRoutingUpdateValidatedPartVersion(@PathVariable("id") Long naturalId) throws FunctionalException {
        routingService.updateToLatestPart(naturalId);

        return ResponseEntity.noContent().build();
    }
    @ApiOperation("Put Routing update to latest validated routing component version")
    @PutMapping("/{id}/update-routing-component/{routingComponentId}")
    @Secured("ROLE_ROUTING:WRITE")
    public ResponseEntity putRoutingUpdateValidatedROutingComponentVersion(
            @PathVariable("id") Long naturalId,
            @PathVariable("routingComponentId") Long routingComponentNaturalId
    ) throws FunctionalException {
        routingService.updateToLatestRoutingComponent(naturalId, routingComponentNaturalId);

        return ResponseEntity.noContent().build();
    }
}
