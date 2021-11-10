package com.airbus.retex.service.childRequest;

import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.childRequest.ChildRequestDetailDto;
import com.airbus.retex.business.dto.childRequest.ChildRequestFilteringDto;
import com.airbus.retex.business.dto.childRequest.ChildRequestFullDto;
import com.airbus.retex.business.dto.childRequest.ChildRequestLightDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import org.springframework.http.ResponseEntity;


public interface IChildRequestService {

    /**
     * Creates a Child request and associated it with the parent request given as param.
     *
     * @param parentRequestId the parent request ID.
     * @return the created child request ID.
     * @throws NotFoundException in case that the given parent request ID is not associated with any parent request.
     */
    ResponseEntity createChildRequest(Long parentRequestId, ChildRequestDetailDto childRequestCreateDto) throws FunctionalException;

    /**
     * Updates a child request identified with childRequestId.
     * @param childRequestId the child request ID that we want to update
     * @param updateDto the DTO used for updating the child request .
     * @return updated child request dto
     * @throws FunctionalException in case of different possible errors
     */
    ChildRequestDetailDto updateChildRequest(final Long childRequestId, final ChildRequestDetailDto updateDto) throws FunctionalException;

    /**
     * Closes a child request identified with childRequestId.
     * @param childRequestId the child request ID that we want to update
     * @param newStatus the new status to use
     * @return closed child request
     * @throws FunctionalException in case that the given child request id is not found.
     */
    ChildRequest updateChildRequestStatus(final Long childRequestId, final EnumStatus newStatus) throws FunctionalException;

    /**
     * Deletes a child request identified with childRequestId.
     * @param childRequestId the child request ID that we want to update
     *
     * @throws FunctionalException in case that the given child child can't be deleted (bad status or contains DRTs) or can't be found.
     */
    void deleteChildRequest(final Long childRequestId) throws  FunctionalException;

    /**
     * Finds a filtered list of ChildRequest of Request.
     * @param childRequestFilteringDto the com.airbus.retex.filtering DTO
     * @param requestId  The request ID
     * @param language
     * @return
     */
    PageDto<ChildRequestLightDto> findFilteredChildRequestOfRequest(ChildRequestFilteringDto childRequestFilteringDto, Long requestId, Language language);

    /**
     * Gets child request information, by id or null if not found.
     * @param id the child request ID
     * @param version the required version
     * @return the required version of child request dto
     * @throws FunctionalException if the given id is invalid
     */
    ChildRequestFullDto getChildRequest(final Long id, final Long version, final Language language) throws FunctionalException;
}
