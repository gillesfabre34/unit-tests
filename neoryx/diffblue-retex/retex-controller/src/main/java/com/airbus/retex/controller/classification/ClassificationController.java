package com.airbus.retex.controller.classification;

import com.airbus.retex.model.classification.EnumClassification;
import com.airbus.retex.utils.ConstantUrl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping
@Api(value = "Classification Endpoints", tags = {"Classification api"})
public class ClassificationController {

    @ApiOperation("Get list of classification")
    @GetMapping(ConstantUrl.API_CLASSIFICATIONS)
    @Secured("ROLE_PART_MAPPING:READ")
    public ResponseEntity<List<EnumClassification>> getClassifications(Authentication authentication) {
        List<EnumClassification> result;
        result = Arrays.asList(EnumClassification.values());
        return ResponseEntity.ok().body(result);
    }
}
