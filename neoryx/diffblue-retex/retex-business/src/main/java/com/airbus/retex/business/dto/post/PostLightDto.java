package com.airbus.retex.business.dto.post;

import com.airbus.retex.business.dto.TranslatedDto;
import com.airbus.retex.model.routing.Routing;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostLightDto extends TranslatedDto {

    private Long id;
    private String measureUnitId;
    private boolean validated;

    @Override
    public String getClassName() {
        return Routing.class.getSimpleName();
    }

    @Override
    public Long getEntityId() {
        return id;
    }
}
