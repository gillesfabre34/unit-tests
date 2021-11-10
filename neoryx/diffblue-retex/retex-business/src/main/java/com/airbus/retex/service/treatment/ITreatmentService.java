package com.airbus.retex.service.treatment;

import com.airbus.retex.business.dto.treatment.TreatmentDto;
import com.airbus.retex.model.common.Language;

import java.util.List;

public interface ITreatmentService {
    List<TreatmentDto> getAllTreatments(Language language);
}
