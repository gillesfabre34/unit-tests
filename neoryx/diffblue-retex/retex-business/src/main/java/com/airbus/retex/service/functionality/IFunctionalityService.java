package com.airbus.retex.service.functionality;
import com.airbus.retex.business.dto.functionality.FunctionalityLightDto;

import java.util.List;
public interface IFunctionalityService {
    List<FunctionalityLightDto> getFunctionalities();
}
