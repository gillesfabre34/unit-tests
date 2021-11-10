package com.airbus.retex.business.dto.request;


import com.airbus.retex.business.dto.aircraft.AircraftFamilyLightDto;
import com.airbus.retex.business.dto.aircraft.AircraftTypeLightDto;
import com.airbus.retex.business.dto.aircraft.AircraftVersionLightDto;
import com.airbus.retex.business.dto.client.ClientLightDto;
import com.airbus.retex.business.dto.environment.EnvironmentLightDto;
import com.airbus.retex.business.dto.media.MediaDto;
import com.airbus.retex.business.dto.mission.MissionTypeLightDto;
import com.airbus.retex.business.dto.user.UserLightDto;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.request.RequestFieldsEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class RequestFullDto extends RequestDto {
    private Map<Language, Map<RequestFieldsEnum, String>> translatedFields;

    private UserLightDto validator;

    private Long originId;
    private String originComment;
    private String originURL;
    private Set<MediaDto> originMedias;

    private String specComment;
    private Set<MediaDto> specMedias;

    private String oetp;

    private List<UserLightDto> operators;
    private List<UserLightDto> technicalResponsibles;

    private AircraftFamilyLightDto aircraftFamily;
    private List<AircraftTypeLightDto> aircraftTypes;
    private List<AircraftVersionLightDto> aircraftVersions;

    private String ataCode;

    private MissionTypeLightDto missionType;
    private EnvironmentLightDto environment;

    private List<ClientLightDto> clients;


}
