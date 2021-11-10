package com.airbus.retex.service.filtering;

import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.childrequest.ChildRequest;

import java.util.Optional;

public interface IRetrieveChildRequestService {
    /**
     * Returns a childRequest with PN/SN inputs and Airbus rules
     *
     * @param partNumber
     * @param serialNumber
     * @return
     * @throws FunctionalException
     */
    Optional<ChildRequest> getChildRequest(final String partNumber, final String serialNumber) throws FunctionalException;
}
