package com.airbus.retex.service.impl.measureUnit;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.measureUnit.MeasureUnitDto;
import com.airbus.retex.business.dto.measureUnit.MeasureUnitsFieldsEnum;
import com.airbus.retex.model.mesureUnit.MeasureUnit;
import com.airbus.retex.persistence.measureUnit.MeasureUnitRepository;
import com.airbus.retex.service.measureUnit.IMeasureUnitService;
import com.airbus.retex.service.translate.ITranslateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class MeasureUnitServiceImpl implements IMeasureUnitService {

    @Autowired
    private MeasureUnitRepository measureUnitRepository;
    @Autowired
    private DtoConverter dtoConverter;
    @Autowired
    private ITranslateService iTranslateService;

    @Override
    public List<MeasureUnitDto> getAllUnits() {
        List<MeasureUnit> measureUnits = measureUnitRepository.findAll();

        List<MeasureUnitDto> measureUnitDtoList = new ArrayList<>();
        measureUnits.stream().forEach(measureUnit -> {
            MeasureUnitDto measureUnitDto = dtoConverter.convert(measureUnit, MeasureUnitDto::new);
            measureUnitDto.setTranslatedFields(iTranslateService.getTranslatedFields(measureUnit, EnumSet.allOf(MeasureUnitsFieldsEnum.class)));
            measureUnitDtoList.add(measureUnitDto);
        });

        return measureUnitDtoList;
    }
}
