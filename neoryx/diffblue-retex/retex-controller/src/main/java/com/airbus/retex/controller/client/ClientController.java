package com.airbus.retex.controller.client;

import com.airbus.retex.business.dto.client.ClientFilteringDto;
import com.airbus.retex.business.dto.client.ClientLightDto;
import com.airbus.retex.service.client.IClientService;
import com.airbus.retex.utils.ConstantUrl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@Api(value = "Client", tags = {"Client"})
@RestController
public class ClientController {

    @Autowired
    private IClientService iClientService;

    @ApiOperation("Get list of Client")
    @GetMapping(ConstantUrl.API_CLIENTS)
    @Secured({"ROLE_REQUEST:READ", "ROLE_FILTERING:READ"})
    public ResponseEntity<List<ClientLightDto>> getAllClientName(@Valid ClientFilteringDto filtering) {
        return ResponseEntity.ok().body(iClientService.getAllClientName(filtering));
    }
}