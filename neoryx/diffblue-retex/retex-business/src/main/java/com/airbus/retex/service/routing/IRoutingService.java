package com.airbus.retex.service.routing;

import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.functionalArea.FunctionalAreaFullDto;
import com.airbus.retex.business.dto.inspection.RoutingInspectionDetailHighlightDto;
import com.airbus.retex.business.dto.operation.ListOperationDto;
import com.airbus.retex.business.dto.operation.ManageOperationDto;
import com.airbus.retex.business.dto.operation.OperationFullDto;
import com.airbus.retex.business.dto.routing.*;
import com.airbus.retex.business.dto.step.StepUpdateDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.model.user.User;

import java.util.List;

public interface IRoutingService {

    /**
     * get Routings
     */
    PageDto<RoutingDto> findRoutings(RoutingFilteringDto filtering);


    /**
     * get Routings by Id
     */
    RoutingFullDto findRoutingById(Long naturalId, Long version) throws NotFoundException;

    /**
     * deleteVersion a routing
     *
     * @param naturalId
     * @throws FunctionalException
     */
    void deleteRouting(Long naturalId) throws FunctionalException;

    /**
     * Create a routing with PartNumber or PartNumberRoot and translated name
     *
     * @param routingCreationDto
     * @param creator
     * @return
     */
    RoutingCreatedDto createRouting(RoutingCreationDto routingCreationDto, User creator) throws FunctionalException;

    /**
     * duplicate a routing
     *
     * @param routingCreationDto
     * @param creator
     * @param oldRoutingId
     * @return
     * @throws FunctionalException
     */
    RoutingCreatedDto duplicateRouting(RoutingCreationDto routingCreationDto, User creator, Long oldRoutingId) throws FunctionalException;

    /**
     * @param idRouting
     * @param manageOperationDto
     * @return
     * @throws FunctionalException
     */
    List<OperationFullDto> manageOperation(Long idRouting, ManageOperationDto manageOperationDto) throws FunctionalException;

    /**
     * @param idRouting
     * @return
     * @throws NotFoundException
     */
    ListOperationDto getOperationByRoutingId(Long idRouting, Long version) throws NotFoundException;

    /**
     * Return header inspection informations
     * @param operationId
     * @param functionalAreaId
     * @return
     * @throws FunctionalException
     */
    FunctionalAreaFullDto getFunctionalAreaDtoByRoutingAndTaskId(Long routingNaturalId, Long operationId, Long functionalAreaId, Long version) throws FunctionalException;

    /**
     * Return details inspection informations
     * @param operationId
     * @param taskId
     * @return
     * @throws FunctionalException
     */
    RoutingInspectionDetailHighlightDto getListInspectionByOperationAndTask(Long routingNaturalId, Long operationId, Long taskId, Long version) throws FunctionalException;

    /**
     * @param routingNaturalId
     * @param operationId
     * @param taskId
     * @param stepUpdateDtoList
     * @throws FunctionalException
     */
    void putListInspectionDetailByPostId(Long routingNaturalId, Long operationId, Long taskId, List<StepUpdateDto> stepUpdateDtoList) throws FunctionalException;

    /**
     * @param id
     * @throws FunctionalException
     */
    void setRoutingStatusToValidated(Long id) throws FunctionalException;

    /**
     * @param routingNaturalId
     * @throws FunctionalException
     */
    void updateToLatestPart(Long routingNaturalId) throws FunctionalException;

    /**
     * @param routingNaturalId
     * @param routingComponentIndexNaturalId
     * @throws FunctionalException
     */
    void updateToLatestRoutingComponent(Long routingNaturalId, Long routingComponentIndexNaturalId) throws FunctionalException;
}
