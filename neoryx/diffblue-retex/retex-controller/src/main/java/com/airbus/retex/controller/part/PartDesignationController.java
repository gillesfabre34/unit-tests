package com.airbus.retex.controller.part;

import com.airbus.retex.business.dto.part.PartDesignationLightDto;
import com.airbus.retex.configuration.CustomLocalResolver;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.service.part.IPartDesignationService;
import com.airbus.retex.utils.ConstantUrl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@Api(value = "Part Designation", tags = {"Parts Designation"})
@RestController
public class PartDesignationController {

    @Autowired
    private IPartDesignationService partDesignationService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private CustomLocalResolver customLocalResolver;

    @ApiOperation("Get all parts designation")
    @GetMapping(ConstantUrl.API_DESIGNATIONS)
//    @Secured("ROLE_ADMIN:READ")
    public ResponseEntity<List<PartDesignationLightDto>> getAllPartsDesignation(Authentication authentication) {
        Locale locale = customLocalResolver.resolveLocale(httpServletRequest);
        return ResponseEntity.ok().body(partDesignationService.getAllPartsDesignation(Language.languageFor(locale)));
    }
}
