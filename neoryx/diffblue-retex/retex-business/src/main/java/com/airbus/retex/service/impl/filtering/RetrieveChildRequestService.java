package com.airbus.retex.service.impl.filtering;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.specification.ChildRequestSpecification;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.persistence.childRequest.ChildRequestRepository;
import com.airbus.retex.service.filtering.IRetrieveChildRequestService;

@Service
@Transactional(rollbackFor = Exception.class)
public class RetrieveChildRequestService implements IRetrieveChildRequestService {
    private static final String PART_NUMBER = "partNumber";
	@Autowired
    private ChildRequestRepository childRequestRepository;

    /**
     * Returns a childRequest with PN/SN inputs and Airbus rules
     * @param partNumber
     * @param serialNumber
     * @return
     * @throws FunctionalException
     */
    @Override
    public Optional<ChildRequest> getChildRequest(final String partNumber, final String serialNumber) throws FunctionalException {
        Specification<ChildRequest> specification = Specification.where(ChildRequestSpecification.filterByStatus(EnumStatus.VALIDATED));

        // Priorité 1
        List<ChildRequest> childRequests = childRequestRepository.findAll(specification
                .and(ChildRequestSpecification.filterByISIROrigin())
                .and(ChildRequestSpecification.filterByPart(PART_NUMBER, partNumber))
                .and(ChildRequestSpecification.filterBySerialNumber(serialNumber)));
        if (!childRequests.isEmpty()) {
            return Optional.of(checkChildRequest(childRequests, partNumber, serialNumber));
        }

        // Priorité 2
        Optional<ChildRequest> optionalChildRequest = checkPriorities(specification, partNumber, serialNumber, "CIVP");
        if (optionalChildRequest.isPresent()) {
            return optionalChildRequest;
        }
        // Priorité 3
        optionalChildRequest = checkPriorities(specification, partNumber, serialNumber, "RETEX");
        return optionalChildRequest;
    }

    /**
     * Check if there is no multiple ChildRequests and return the only one
     *
     * @param childRequests
     * @param partNumber
     * @param serialNumber
     * @return
     * @throws FunctionalException
     */
    private ChildRequest checkChildRequest(List<ChildRequest> childRequests, String partNumber, String serialNumber) throws FunctionalException {
        if (childRequests.size() > 1) {
            throw new FunctionalException("retex.error.drt.creation.multiple.childrequest.found", partNumber, serialNumber);
        }
        return childRequests.get(0);
    }

    /**
     * Return the good childRequest with priorities
     * This is use with ISIR and RETEX Origin
     * @param specification
     * @param partNumber
     * @param serialNumber
     * @param originName
     * @return
     * @throws FunctionalException
     */
    private Optional<ChildRequest> checkPriorities(Specification<ChildRequest> specification, String partNumber,
                                                   String serialNumber, String originName) throws FunctionalException {
        List<ChildRequest> childRequests = childRequestRepository.findAll(specification
                .and(ChildRequestSpecification.filterByCIVPorRETEXOrigin(originName))
                .and(ChildRequestSpecification.filterByPart(PART_NUMBER, partNumber))
                .and(ChildRequestSpecification.filterBySerialNumber(serialNumber)));

        if (!childRequests.isEmpty()) {
            return Optional.of(checkChildRequest(childRequests, partNumber, serialNumber));
        } else {
            childRequests = childRequestRepository.findAll(specification
                    .and(ChildRequestSpecification.filterByCIVPorRETEXOrigin(originName))
                    .and(ChildRequestSpecification.filterByPart(PART_NUMBER, partNumber)));
            if (!childRequests.isEmpty()) {
                return Optional.of(checkChildRequest(childRequests, partNumber, serialNumber));
            } else {
                childRequests = childRequestRepository.findAll(specification
                        .and(ChildRequestSpecification.filterByCIVPorRETEXOrigin(originName))
                        .and(ChildRequestSpecification.filterByPart("partNumberRoot", partNumber)));

                if (!childRequests.isEmpty()) {
                    return Optional.of(checkChildRequest(childRequests, partNumber, serialNumber));
                }
            }
        }
        return Optional.empty();
    }
}
