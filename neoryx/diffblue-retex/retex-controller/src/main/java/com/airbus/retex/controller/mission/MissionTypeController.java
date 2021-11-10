package com.airbus.retex.controller.mission;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.airbus.retex.business.dto.mission.MissionTypeLightDto;
import com.airbus.retex.service.mission.IMissionTypeService;
import com.airbus.retex.utils.ConstantUrl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "Mission Type", tags = {"Mission Type"})
@RestController
public class MissionTypeController {

    @Autowired
    private IMissionTypeService missionTypeService;


    @ApiOperation("Get list of Mission Type")
    @GetMapping(ConstantUrl.API_MISSIONS)
    @Secured({"ROLE_REQUEST:READ", "ROLE_FILTERING:READ"})
    public ResponseEntity<List<MissionTypeLightDto>> getAllMissionType() {
        return ResponseEntity.ok().body(missionTypeService.getAllMissionsType());
    }
}
