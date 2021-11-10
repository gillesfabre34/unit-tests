package com.airbus.retex.business.dto.request;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.aircraft.AircraftFamilyLightDto;
import com.airbus.retex.business.dto.aircraft.AircraftTypeLightDto;
import com.airbus.retex.business.dto.aircraft.AircraftVersionLightDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestDetailsDto implements Dto {
    @NotNull
    private Long originId;
    @NotNull
    private String reference;
    private String originComment;
    private String originUrl;
    private List<UUID> originMedias;
    @Size(min = 1)
    private List<Long> technicalManagerIds;
    @Size(min = 1)
    private List<Long> operatorIds;
    private String specComment;
    private List<UUID> specMedias;
    @NotNull
    private String oetp;
    private AircraftFamilyLightDto aircraftFamily;
    private List<AircraftTypeLightDto> aircraftTypes;
    private List<AircraftVersionLightDto> aircraftVersions;
    @NotNull
    private String ataCode;
    private Long missionTypeId;
    private Long environmentId;
    @NotNull
    @Size(min = 1)
    private List<Long> clientIds;
    private Boolean isDeletable;
}
