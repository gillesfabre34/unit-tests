package com.airbus.retex.service.drt;

import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.drt.DrtDto;
import com.airbus.retex.business.dto.drt.DrtFilteringDto;
import com.airbus.retex.business.dto.drt.DrtHeaderDto;
import com.airbus.retex.business.dto.functionalArea.FunctionalAreaHeaderDto;
import com.airbus.retex.business.dto.inspection.InspectionDetailsDto;
import com.airbus.retex.business.dto.inspection.InspectionValueDto;
import com.airbus.retex.business.dto.operation.ListDrtOperationDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.business.exception.TechnicalError;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.operation.OperationFunctionalArea;

import java.util.List;

public interface IDrtService {

    /**
     *
     * @param userId user id
     * @param drtFilteringDto drtFilteringDto
     * @return PageDto<DrtDto>
     */
    PageDto<DrtDto> findDrtsWithFilteringAndUserRoles(final Long userId, DrtFilteringDto drtFilteringDto);

    /**
     *
     * @param drtId technical id of drt
     * @return ListOperationDto
     * @throws FunctionalException
     */
    ListDrtOperationDto getOperationsByDrtId(Long drtId) throws FunctionalException;

    /**
     *
     * @param drtId technical id of drt
     * @param language langue
     * @return DrtHeaderDto
     * @throws FunctionalException
     */
    DrtHeaderDto getHeader(Long drtId, Language language) throws FunctionalException;

    /**
     *
     * @param drtId technical id of drt
     * @return Drt
     * @throws NotFoundException
     */
    Drt findDrtById(Long drtId) throws NotFoundException;

    /**
     * @param drtId       drt technical Id
     * @param operationId operation Natural Id
     * @param taskId      functionalArea Natural Id if RoutingComponent / todolistNaturalId if TodoList
     * @return List<InspectionDetailsDto>
     * @throws FunctionalException
     */
    FunctionalAreaHeaderDto getOperationTaskHeader(Long drtId, Long operationId, Long taskId) throws FunctionalException;

    Operation getOperationByDrtAndOperation(Long drtId, Long operationId) throws FunctionalException;

    /**
     * Returns inspection details of each "routing component" for an operation, by operation type
     *
     * @param drtId       technical id of drt
     * @param operationId operation naturalId
     * @param taskId      FunctionalArea NaturalId or TodoList NaturalId
     * @return List<InspectionDetailsDto>
     * @throws FunctionalException
     */
    List<InspectionDetailsDto> getInspectionDetails(Long drtId, Long operationId, Long taskId) throws FunctionalException;

    OperationFunctionalArea getOperationFunctionalArea(Operation operation, Long taskId) throws FunctionalException;

    /**
     * @param drtId              technical id of drt
     * @param operationId        operation naturalId
     * @param taskId             FunctionalArea NaturalId or TodoList NaturalId
     * @param inspectionValueDto inspectionValueDto
     * @param userId             user id
     * @throws FunctionalException
     */
    void putInspectionDetails(Long drtId, Long operationId, Long taskId, InspectionValueDto inspectionValueDto, final Long userId, boolean validate) throws FunctionalException;

    /**
     * Return true if drt can be set to targetStatus.
     *
     * @param drt
     * @throws TechnicalError
     */
    boolean isValidatable(Drt drt);

    /**
     * Returns true if the given DRT can be closed.
     *
     * @param drt
     * @return
     */
    boolean isClosable(Drt drt);

    /**
     * Set the DRT to validate
     *
     * @param drt
     * @return
     */
    void validateDrt(Drt drt);

    /**
     * Returns true if the given DRT can be closed.
     *
     * @param drtId
     * @return
     */
    void closeDrt(Long drtId) throws FunctionalException;
}
