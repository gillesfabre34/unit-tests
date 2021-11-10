package com.airbus.retex.service.impl.treatment;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.treatment.TreatmentDto;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.treatment.Treatment;
import com.airbus.retex.persistence.treatment.TreatmentRepository;
import com.airbus.retex.service.treatment.ITreatmentService;

@Service
@Transactional(rollbackFor = Exception.class)
public class TreatmentServiceImpl implements ITreatmentService {

    @Autowired
    TreatmentRepository treatmentRepository;

    @Autowired
    private DtoConverter dtoConverter;


    @Override
    public List<TreatmentDto> getAllTreatments(Language language) {

        List<Treatment> treatments = treatmentRepository.findAll();
        List<TreatmentDto> treatmentDtos = new ArrayList<>();

        for(var treatment : treatments) {
            treatmentDtos.add(dtoConverter.convert(treatment, TreatmentDto::new));
        }
        return treatmentDtos;

    }
}
