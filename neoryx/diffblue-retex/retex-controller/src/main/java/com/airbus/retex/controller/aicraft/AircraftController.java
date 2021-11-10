package com.airbus.retex.controller.aicraft;

import com.airbus.retex.business.dto.aircraft.AircraftFamilyLightDto;
import com.airbus.retex.business.dto.aircraft.AircraftTypeIdListDto;
import com.airbus.retex.business.dto.aircraft.AircraftTypeLightDto;
import com.airbus.retex.business.dto.aircraft.AircraftVersionLightDto;
import com.airbus.retex.service.aircraft.IAircraftService;
import com.airbus.retex.utils.ConstantUrl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(value = "Aircraft", tags = {"Aircraft"})
@RestController
public class AircraftController {

    @Autowired
    private IAircraftService aircraftService;

    @ApiOperation("Get list of aircraft family")
    @GetMapping(ConstantUrl.API_AIRCRAFTS_FAMILIES)
    @Secured({"ROLE_REQUEST:READ", "ROLE_FILTERING:READ"})
    public ResponseEntity<List<AircraftFamilyLightDto>> getAllAircraftFamilies() {
        return ResponseEntity.ok().body(aircraftService.getAllAircraftFamilies());
    }


    @ApiOperation("Get list of aircraft type")
    @GetMapping(ConstantUrl.API_AIRCRAFTS_TYPES)
    @Secured({"ROLE_REQUEST:READ", "ROLE_FILTERING:READ"})
    public ResponseEntity<List<AircraftTypeLightDto>> getAllAircraftTypes(@Nullable @RequestParam(value = "aircraftFamily", required = false)  Long aircraftFamily) {
        return ResponseEntity.ok().body(aircraftService.getAllAircraftType(aircraftFamily));
    }


    @ApiOperation("Get list of aircraft versioning")
    @GetMapping(ConstantUrl.API_AIRCRAFTS_VERSIONS)
    @Secured({"ROLE_REQUEST:READ", "ROLE_FILTERING:READ"})
    public ResponseEntity<List<AircraftVersionLightDto>> getAllAircraftVersions(@Nullable @RequestParam(value = "aircraftType", required = false) Long aircraftType) {
        return ResponseEntity.ok().body(aircraftService.getAllAircraftVersion(List.of(aircraftType)));
    }


    @ApiOperation("Get list of aircraft versioning")
    @PostMapping(ConstantUrl.API_AIRCRAFTS_VERSIONS)
    @Secured({"ROLE_REQUEST:READ", "ROLE_FILTERING:READ"})
    public ResponseEntity<List<AircraftVersionLightDto>> getAllAircraftVersions(
            @RequestBody @Valid AircraftTypeIdListDto aircraftTypeIds
    ) {
        return ResponseEntity.ok().body(aircraftService.getAllAircraftVersion(aircraftTypeIds.getAircraftTypeIds()));
    }
}
