package com.airbus.retex.business.dto.childRequest;

import com.airbus.retex.business.dto.aircraft.AircraftFamilyLightDto;
import com.airbus.retex.business.dto.aircraft.AircraftTypeLightDto;
import com.airbus.retex.business.dto.aircraft.AircraftVersionLightDto;
import com.airbus.retex.business.dto.client.ClientLightDto;
import com.airbus.retex.business.dto.environment.EnvironmentLightDto;
import com.airbus.retex.business.dto.media.MediaDto;
import com.airbus.retex.business.dto.mission.MissionTypeLightDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChildRequestFullDto extends ChildRequestLightDto {
    private Set<String> serialNumbers;
    private Set<MediaDto> medias;

    // air craft infos
    private AircraftFamilyLightDto aircraftFamily;
    private List<AircraftTypeLightDto> aircraftTypes;
    private List<AircraftVersionLightDto> aircraftVersions;
    private MissionTypeLightDto missionType;

    private String ataCode;
    private EnvironmentLightDto environment;

    private Set<ClientLightDto> childRequestClients;
    private Set<ClientLightDto> parentRequestClients;

    private Long partId;
    private String routing;
    private Integer modulation;
}
