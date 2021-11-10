package com.airbus.retex.controller.material;

import com.airbus.retex.service.material.IMaterialService;
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

import java.util.List;

@RestController
@RequestMapping
@Api(value = "Materials Endpoint", tags = {"Material api"})
public class MaterialController {

    @Autowired
    private IMaterialService materialService;

    @ApiOperation("Get list of materials")
    @GetMapping(ConstantUrl.API_MATERIALS)
    @Secured("ROLE_PART_MAPPING:READ")
    public ResponseEntity<List<String>> getMaterials(Authentication authentication) {
        return ResponseEntity.ok().body(materialService.getAllMaterials());
    }
}
