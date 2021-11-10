package com.airbus.retex.service.historization;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.persistence.request.RequestRepository;
import com.airbus.retex.service.impl.request.RequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class HistorizationSafeDeleteServiceIT extends AbstractServiceIT {

    private static final String REF_FIRST = "REF-FIRST";
    private static final String REF_SECOND = "REF-SECOND";

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestServiceImpl requestService;

    private Request firstRequestVersion;
    private Request secondRequestVersion;

    @BeforeEach
    public void before() {
        runInTransaction(() -> {
            firstRequestVersion = dataSetInitializer.createRequest("Test request", (request) -> {
                request.setStatus(EnumStatus.CREATED);
                request.setReference(REF_FIRST);
                request.setAircraftFamily(dataSetInitializer.reference(dataset.aircraft_family_1));
            });
        });

        runInTransaction(() -> {
            secondRequestVersion = requestRepository.getOne(firstRequestVersion.getId());
            secondRequestVersion.setReference(REF_SECOND);
            secondRequestVersion.setStatus(EnumStatus.VALIDATED);
            secondRequestVersion.setAircraftFamily(dataSetInitializer.reference(dataset.aircraft_family_2));
            secondRequestVersion = requestRepository.save(secondRequestVersion);
        });
    }

    @Test
    public void checkTestObject() {
        assertThat(firstRequestVersion.getReference(), equalTo(REF_FIRST));
        assertThat(secondRequestVersion.getReference(), equalTo(REF_SECOND));
        checkValues(secondRequestVersion.getId(), REF_SECOND, dataset.aircraft_family_2.getId());
    }

    @Test
    public void safeDelete_ok() throws FunctionalException {
        requestService.safeDelete(secondRequestVersion.getId());
        checkValues(secondRequestVersion.getId(), REF_FIRST, dataset.aircraft_family_1.getId());
    }

    private void checkValues(final Long requestId, final String expectedRef, final Long expectedAircraftFamilyId) {
        runInTransaction(() -> {
            Request actualVersion = requestRepository.getOne(requestId);
            assertThat(actualVersion.getReference(), equalTo(expectedRef));
            assertThat(actualVersion.getAircraftFamily().getId(), equalTo(expectedAircraftFamilyId));
        });

    }
}

