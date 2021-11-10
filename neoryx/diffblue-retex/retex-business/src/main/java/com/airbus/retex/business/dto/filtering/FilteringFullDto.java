package com.airbus.retex.business.dto.filtering;

import com.airbus.retex.business.dto.aircraft.AircraftFamilyLightDto;
import com.airbus.retex.business.dto.aircraft.AircraftTypeLightDto;
import com.airbus.retex.business.dto.aircraft.AircraftVersionLightDto;
import com.airbus.retex.business.dto.media.MediaDto;
import com.airbus.retex.business.dto.mpn.MpnDto;
import com.airbus.retex.business.util.ConstantRegex;
import com.airbus.retex.model.common.EnumFilteringPosition;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class FilteringFullDto extends FilteringDto {

    private AircraftFamilyLightDto aircraftFamily;

    private AircraftTypeLightDto aircraftType;

    private AircraftVersionLightDto aircraftVersion;

    @Pattern(regexp = ConstantRegex.REGEX_NUMBER)
    private String aircraftSerialNumber;

    private String associatedRequest;

    private LocalDate filteringDate;

    private LocalDate lastModified;

    private EnumFilteringPosition position;

    private String notification;

    private Boolean canCreateDrt;

    private MpnDto mpn;

    private Set<MediaDto> medias;

    private MediaDto partMedia;
}
