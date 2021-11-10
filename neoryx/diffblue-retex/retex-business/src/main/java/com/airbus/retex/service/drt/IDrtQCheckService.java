package com.airbus.retex.service.drt;

import com.airbus.retex.business.dto.inspection.InspectionQCheckValueDto;
import com.airbus.retex.business.exception.FunctionalException;

import java.util.List;

public interface IDrtQCheckService {

    /**
     *
     * @param drtId technical id of drt
     * @param operationId operation naturalId
     * @param taskId FunctionalArea NaturalId or TodoList NaturalId
     * @param inspectionQCheckValueDto inspectionQCheckValueDto
     * @param userId user id
     * @throws FunctionalException
     */
    void putInspectionQCheck(Long drtId, Long operationId, Long taskId, List<InspectionQCheckValueDto> inspectionQCheckValueDto, final Long userId, boolean validate) throws FunctionalException;

}
