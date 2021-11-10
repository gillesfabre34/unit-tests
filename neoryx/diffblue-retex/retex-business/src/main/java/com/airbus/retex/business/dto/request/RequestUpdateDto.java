package com.airbus.retex.business.dto.request;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.user.ReferenceDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpdateDto implements Dto {

    @NotNull
    private Long originId;

    @NotNull
    private String reference;

    private String originComment;

    private String originUrl;

    //@NotNull FIXME add not null
    private List<UUID> originMedias = new ArrayList<>();

    @Size(min = 1)
    private List<Long> technicalManagerIds;

    @Size(min = 1)
    private List<Long> operatorIds;

    private String specComment;

    //@NotNull FIXME add not null
    private List<UUID> specMedias = new ArrayList<>();

    @NotNull
    private String oetp;


    private ReferenceDto<Long> aircraftFamily;

    @NotNull
    private List<Long> aircraftTypes = new ArrayList<>();

    @NotNull
    private List<Long> aircraftVersions = new ArrayList<>();

    @NotNull
    private String ataCode;


    private ReferenceDto<Long> missionType;


    private ReferenceDto<Long> environment;

    @NotNull
    @Size(min = 1)
    private List<Long> clientIds;

}
