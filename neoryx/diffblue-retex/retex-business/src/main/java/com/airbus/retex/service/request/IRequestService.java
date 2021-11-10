package com.airbus.retex.service.request;

import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.request.*;
import com.airbus.retex.business.dto.user.UserLightDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.user.User;

import java.util.List;


public interface IRequestService {
    /**
     *  List of  requests.
     *  Translated properties are filtered / sort according to the given language
     */
    PageDto<RequestDto> findRequestsWithFilters(RequestFilteringDto requestFiltering, Language lang);

    /**
     *
     * @return all Users referenced as request requester
     */
    List<UserLightDto> getAllRequesters();

    RequestDto createRequest(RequestCreationDto requestCreationDto, User requester) throws FunctionalException;

    /**
     * Finds a request by ID.
     * @param id the ID of the request
     * @param version the version
     * @return the result
     * @throws FunctionalException when the request with this ID is not found or other errors.
     */
    RequestFullDto findRequestById(final Long id, final Long version) throws FunctionalException;

    /**
     * Updates a request HEADER.
     * @param id the request ID.
     * @param requestCreationDto the dto for creation
     * @return the created dto
     * @throws FunctionalException if any problem
     */
    RequestDto updateRequest(final Long id, final RequestCreationDto requestCreationDto) throws FunctionalException;

    /**
     * Updates the detail of the Request.
     * @param id the request ID
     * @param requestDetailsDto the dto for update
     * @param validated the validation flag
     * @return
     * @throws FunctionalException
     */
    RequestDetailsDto updateRequestDetail(final Long id, final RequestUpdateDto requestDetailsDto, final boolean validated) throws FunctionalException;

    /**
     * Get list of technical Managers of request
     * @param id
     * @return
     * @throws FunctionalException
     */
    List<String> getTechnicalManagersOfRequest(final Long id) throws FunctionalException;

    /**
     * delete a request
     *
     * @param id
     * @throws FunctionalException
     */
    void deleteRequest(final Long id) throws FunctionalException;

    void updateRequestStatus(final Long id, EnumStatus newStatus) throws FunctionalException;

}
