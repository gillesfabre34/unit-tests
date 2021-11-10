package com.airbus.retex.functionalArea;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.functionalArea.FunctionalAreaCreateOrUpdateDto;
import com.airbus.retex.business.dto.part.PartCreateUpdateFunctionalAreaDto;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.persistence.functionalArea.FunctionalAreaRepository;
import com.airbus.retex.service.part.IPartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
public class FunctionalAreaServiceIT extends AbstractServiceIT {
    @Autowired
    private IPartService partService;
    @Autowired
    private FunctionalAreaRepository functionalAreaRepository;
    @Autowired
    private DtoConverter dtoConverter;

    private Part part;

    private FunctionalArea functionalArea;

    private FunctionalAreaCreateOrUpdateDto functionalAreaUpdateDto;
    private FunctionalAreaCreateOrUpdateDto functionalAreaCreateDto;
    private PartCreateUpdateFunctionalAreaDto listFunctionalAreas;

    @BeforeEach
    public void before() {
        part = super.dataSetInitializer.createPart(null);
        functionalArea = dataSetInitializer.createFunctionalArea(fa -> fa.setPart(part));

        functionalAreaUpdateDto = dtoConverter.toDto(functionalArea, FunctionalAreaCreateOrUpdateDto.class);
        functionalAreaUpdateDto.setAreaNumber("2000");
        functionalAreaUpdateDto.setFunctionalityId(dataset.functionality_teeth.getId());
        functionalAreaUpdateDto.setFaNameId(functionalArea.getFunctionalAreaName().getId());
        functionalAreaUpdateDto.setMaterial(dataset.material_15CN6.getCode());

        functionalAreaCreateDto = dtoConverter.toDto(functionalArea, FunctionalAreaCreateOrUpdateDto.class);
        functionalAreaCreateDto.setAreaNumber("1000");
        functionalAreaCreateDto.setFunctionalityId(dataset.functionality_teeth.getId());
        functionalAreaCreateDto.setId(null);
        functionalAreaCreateDto.setFaNameId(functionalArea.getFunctionalAreaName().getId());
        functionalAreaCreateDto.setMaterial(dataset.material_35NCD16.getCode());

        listFunctionalAreas = new PartCreateUpdateFunctionalAreaDto();
        List<FunctionalAreaCreateOrUpdateDto> functionalArea = new ArrayList<>();
        functionalArea.add(functionalAreaCreateDto);
        functionalArea.add(functionalAreaUpdateDto);

        listFunctionalAreas.setFunctionalAreas(functionalArea);
    }

    @Test
    public void updatePartMapping_ok() throws Exception {
        List<FunctionalAreaCreateOrUpdateDto> functionalAreaDtos = partService.createOrUpdateFunctionalityAreas(part.getNaturalId(), true, listFunctionalAreas);
        assertThat(functionalAreaDtos.size(), equalTo(listFunctionalAreas.getFunctionalAreas().size()));
        assertThat(partService.findPartById(part.getNaturalId(), null).getFunctionalAreas().size(), equalTo(listFunctionalAreas.getFunctionalAreas().size()));

    }

    @Test
    public void updatePartMapping_checkPartStatus_ok() throws Exception {
        List<FunctionalAreaCreateOrUpdateDto> functionalAreaDtos = partService.createOrUpdateFunctionalityAreas(part.getNaturalId(), false, listFunctionalAreas);
        assertThat(partService.findPartById(part.getNaturalId(), null).getStatus(), equalTo(EnumStatus.CREATED));
    }
}
