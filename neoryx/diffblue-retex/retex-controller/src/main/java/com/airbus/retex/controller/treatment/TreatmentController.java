package com.airbus.retex.controller.treatment;

import com.airbus.retex.business.dto.treatment.TreatmentDto;
import com.airbus.retex.configuration.CustomLocalResolver;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.service.treatment.ITreatmentService;
import com.airbus.retex.utils.ConstantUrl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping
@Api(value = "Treatments Endpoint", tags = {"Treatment api"})
public class TreatmentController {
    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private CustomLocalResolver customLocalResolver;

    @Autowired
    private ITreatmentService treatmentService;

    @ApiOperation("Get list of treatments")
    @GetMapping(ConstantUrl.API_TREATMENTS)
    @Secured("ROLE_PART_MAPPING:READ")
    public ResponseEntity<List<TreatmentDto>> getAllTreatments(Authentication authentication) {
        Locale locale = customLocalResolver.resolveLocale(httpServletRequest);
        return ResponseEntity.ok().body(treatmentService.getAllTreatments(Language.languageFor(locale)));
    }
}
