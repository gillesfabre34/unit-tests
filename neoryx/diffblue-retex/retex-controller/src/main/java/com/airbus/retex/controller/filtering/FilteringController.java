package com.airbus.retex.controller.filtering;

import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.filtering.FilteringCreationDto;
import com.airbus.retex.business.dto.filtering.FilteringDto;
import com.airbus.retex.business.dto.filtering.FilteringFiltersDto;
import com.airbus.retex.business.dto.filtering.FilteringFullDto;
import com.airbus.retex.business.dto.filtering.FilteringUpdateDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.configuration.CustomLocalResolver;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.service.filtering.IFilteringService;
import com.airbus.retex.utils.ConstantUrl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;

@Api(value = "Filtering", tags = {"Filtering"})
@RestController
public class FilteringController {

    @Autowired
    private IFilteringService filteringService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private CustomLocalResolver customLocalResolver;

    @ApiOperation("Create a filtering")
    @PostMapping(ConstantUrl.API_FILTERINGS)
    @Secured("ROLE_FILTERING:WRITE")
    public ResponseEntity<FilteringDto> create(
            @RequestBody @Valid FilteringCreationDto filteringCreationDto
    ) throws FunctionalException {
        return ResponseEntity.ok().body(filteringService.createFiltering(filteringCreationDto));
    }
    @ApiOperation("Create a Drt")
    @PostMapping(ConstantUrl.API_FILTERING_POST_DRT)
    @Secured("ROLE_FILTERING:WRITE")
    public ResponseEntity<Long> createDRT(@PathVariable Long id) throws FunctionalException {
        return ResponseEntity.ok().body(filteringService.createDrt(id));
    }
    @ApiOperation("Get list of filterings")
    @GetMapping(ConstantUrl.API_FILTERINGS)
    @Secured("ROLE_FILTERING:READ")
    public ResponseEntity<PageDto<FilteringDto>> getAllFilterings(
            @Valid FilteringFiltersDto filteringDto) throws FunctionalException {
        return ResponseEntity.ok().body(filteringService.findFilteringsWithFilters(filteringDto));
    }
    @ApiOperation("get a filtering")
    @GetMapping(ConstantUrl.API_FILTERING)
    @Secured("ROLE_FILTERING:READ")
    public ResponseEntity<FilteringFullDto> get(@PathVariable @Valid Long id) throws FunctionalException {
        Locale locale = customLocalResolver.resolveLocale(httpServletRequest);
        return ResponseEntity.ok().body(filteringService.findFilteringById(id, Language.languageFor(locale)));
    }
    @ApiOperation("Update a filtering")
    @PutMapping(ConstantUrl.API_FILTERING_UPDATE)
    @Secured("ROLE_FILTERING:WRITE")
    public ResponseEntity<FilteringDto> update(
            @PathVariable Long id,
            @RequestBody @Valid FilteringUpdateDto filteringUpdateDto
    ) throws FunctionalException {
        return ResponseEntity.ok().body(filteringService.updateFiltering(id, filteringUpdateDto));
    }
}
