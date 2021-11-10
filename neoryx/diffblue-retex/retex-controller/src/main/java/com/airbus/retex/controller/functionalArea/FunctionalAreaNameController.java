package com.airbus.retex.controller.functionalArea;

import com.airbus.retex.business.dto.functionalAreaName.FunctionalAreaNameDto;
import com.airbus.retex.configuration.CustomLocalResolver;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.service.functionalArea.IFunctionalAreaNameService;
import com.airbus.retex.utils.ConstantUrl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping
@Api(value = "Functional Area Name Endpoints", tags = {"Functionals Area Names"})
public class FunctionalAreaNameController {
    @Autowired
    private IFunctionalAreaNameService functionalAreaNameService;

    @Autowired
    private CustomLocalResolver customLocalResolver;

    @Autowired
    private HttpServletRequest httpServletRequest;


    @ApiOperation("Get list of functionals area names")
    @GetMapping(ConstantUrl.API_FUNCTIONALS_AREA_NAMES)
    @Secured("ROLE_PART_MAPPING:READ")
    public ResponseEntity<List<FunctionalAreaNameDto>> getFunctionalAreaNames() {
        Locale locale = customLocalResolver.resolveLocale(httpServletRequest);
        return ResponseEntity.ok().body(functionalAreaNameService.getAllFunctionalAreasNames(Language.languageFor(locale)));

    }
}
