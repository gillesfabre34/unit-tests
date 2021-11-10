package com.airbus.retex.controller.routingComponent;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.airbus.retex.business.dto.routingComponent.RoutingComponentCreateUpdateDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentFilterDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentFilterRequestDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentFilteringDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentFullDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentIndexDto;
import com.airbus.retex.business.dto.step.StepFullDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.controller.VersionsController;
import com.airbus.retex.service.impl.routingComponent.RoutingComponentIndexServiceImpl;
import com.airbus.retex.service.routingComponent.IRoutingComponentFiltersService;
import com.airbus.retex.service.routingComponent.IRoutingComponentIndexService;
import com.airbus.retex.service.routingComponent.IRoutingComponentService;
import com.airbus.retex.utils.ConstantUrl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Routing Component", tags = {"Routing Components"})
@RequestMapping(ConstantUrl.API_ROUTING_COMPONENTS)
@RestController
public class RoutingComponentController extends VersionsController<RoutingComponentIndexServiceImpl> {

    @Autowired
    private IRoutingComponentIndexService routingComponentIndexService;
    @Autowired
    private IRoutingComponentService routingComponentService;
    @Autowired
    private IRoutingComponentFiltersService routingComponentFiltersService;

    /**
     * Returns a Routing Component or TodoList by RoutingComponentIndexId
     *
     * @param id
     * @return
     * @throws FunctionalException
     */
    @ApiOperation(value = "Get a RoutingComponent by id")
    @GetMapping("/{id}")
    @Secured("ROLE_ROUTING_COMPONENT:READ")
    public ResponseEntity<RoutingComponentFullDto> getRoutingComponent(Authentication authentication, @PathVariable("id") Long id,
                                              @Nullable  @RequestParam(value = "version", required = false) Long version) throws FunctionalException {
        RoutingComponentFullDto optRoutingComponentDto = routingComponentService.getRoutingComponent(id, version)
                .orElseThrow(() ->  new FunctionalException("retex.routing.component.not.found"));
        return ResponseEntity.ok().body(optRoutingComponentDto);
    }
    @Deprecated(since = "4.2") // USE getRoutingComponent instead
    @ApiOperation(value = "Get a RoutingComponent steps", responseContainer = "List")
    @GetMapping("/{id}/steps")
    @Secured("ROLE_ROUTING_COMPONENT:READ")
    public ResponseEntity< List<StepFullDto>> getRoutingComponentSteps(Authentication authentication, @PathVariable("id") Long id,
                                                   @RequestParam(value = "version", required = false) String version) throws FunctionalException {
        List<StepFullDto> result = routingComponentService.getRoutingComponentSteps(id);
        if (result.isEmpty()) {
            return ResponseEntity.ok().body(Collections.emptyList());
        }
        return ResponseEntity.ok().body(result);
    }
    @ApiOperation("Get list of routing components")
    @GetMapping("")
    @Secured("ROLE_ROUTING_COMPONENT:READ")
    public ResponseEntity<PageDto<RoutingComponentIndexDto>> getAllRoutingComponents(Authentication authentication,
                                                                                     @Valid RoutingComponentFilteringDto filteringDto) {
        CustomUserDetails customUserDetails = ((CustomUserDetails) authentication.getPrincipal());
        return ResponseEntity.ok().body(routingComponentIndexService.findRoutingComponentsIndex(filteringDto, customUserDetails.getLanguage().locale)); //FIXME Language
    }
    @ApiOperation("Get list of filters to display list of routingComponents")
    @GetMapping("/available-filters")
    @Secured("ROLE_ROUTING_COMPONENT:READ")
    public ResponseEntity<RoutingComponentFilterDto> getAllFiltersForRoutingComponents(Authentication authentication,
                                                                                       RoutingComponentFilterRequestDto filters) {
        return ResponseEntity.ok().body(routingComponentFiltersService.getRoutingComponentFilters(filters));
    }
    @ApiOperation("Create a RoutingComponent")
    @PostMapping("")
    @Secured("ROLE_ROUTING_COMPONENT:WRITE")
    public ResponseEntity<RoutingComponentFullDto> createRoutingComponent(@RequestParam(value = "validate", defaultValue = "false") Boolean validated,
                                                                          @RequestBody @Valid RoutingComponentCreateUpdateDto routingComponentCreationDto) throws FunctionalException {
        return ResponseEntity.ok().body(routingComponentService.createRoutingComponent(routingComponentCreationDto, validated));
    }
    @ApiOperation("Update a RoutingComponent")
    @PutMapping("/{id}")
    @Secured("ROLE_ROUTING_COMPONENT:WRITE")
    public ResponseEntity<RoutingComponentFullDto> updateRoutingComponent(@PathVariable Long id,
                                                                          @RequestParam(value = "validate", defaultValue = "false") Boolean validated,
                                                                          @RequestBody @Valid RoutingComponentCreateUpdateDto routingComponentUpdateDto) throws FunctionalException {
        return ResponseEntity.ok().body(routingComponentService.updateRoutingComponent(routingComponentUpdateDto, id, validated));
    }
    @ApiOperation("Delete a routing component")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Routing Component deleted successfully"),
    })
    @DeleteMapping("/{id}")
    @Secured("ROLE_ROUTING_COMPONENT:WRITE")
    public ResponseEntity<Void> deleteRoutingComponent(@PathVariable Long id) throws FunctionalException {
        routingComponentIndexService.deleteRoutingComponent(id);
        return ResponseEntity.noContent().build();
    }

}
