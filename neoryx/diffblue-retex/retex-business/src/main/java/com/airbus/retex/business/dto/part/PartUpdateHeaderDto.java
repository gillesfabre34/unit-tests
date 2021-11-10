package com.airbus.retex.business.dto.part;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.model.common.EnumStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartUpdateHeaderDto implements Dto {
    @NotNull
    private Long partDesignationId;

    @Nullable
    private String ataCode;

    private EnumStatus status;

    private List<String> mpnCodes;

}
