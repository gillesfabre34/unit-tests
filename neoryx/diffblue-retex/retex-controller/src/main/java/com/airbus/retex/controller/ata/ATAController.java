package com.airbus.retex.controller.ata;

import com.airbus.retex.service.ata.IATAService;
import com.airbus.retex.utils.ConstantUrl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "ATA", tags = {"ATA"})
@RestController
public class ATAController {

    @Autowired
    private IATAService ataService;

    @ApiOperation("Get list of ATA")
    @GetMapping(ConstantUrl.API_ATAS)
//    @Secured("ROLE_ADMIN:READ")
    public ResponseEntity<List<String>> getAllATACode() {
        return ResponseEntity.ok().body(ataService.getAllATACode());
    }
}
