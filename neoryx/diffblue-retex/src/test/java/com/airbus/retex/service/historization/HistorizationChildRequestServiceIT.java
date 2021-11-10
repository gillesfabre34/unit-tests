package com.airbus.retex.service.historization;

import com.airbus.retex.business.dto.versioning.VersionDto;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.client.Client;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.persistence.childRequest.ChildRequestRepository;

import com.airbus.retex.service.impl.childRequest.ChildRequestServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;


public class HistorizationChildRequestServiceIT extends HistorizationServiceAbstract {

    private Request parentRequest1;

    private ChildRequest childRequest11;

    private ChildRequest childRequest12;
    private Client        client1;
    private Client        client2;
    private PhysicalPart phPart1;
    private PhysicalPart phPart2;

    @Autowired
    private ChildRequestServiceImpl childRequestService;

    @Autowired
    private ChildRequestRepository childRequestRepository;

    private static final Long DRT_TO_INSPECT = 100L;
    @BeforeEach
    public void before() {
        runInTransaction(() -> {
            parentRequest1 = createRequest("parentRequest1");
        });
        runInTransaction(() -> {
            childRequest11 = createChildRequest(cr -> {
                cr.setParentRequest(parentRequest1);
                cr.setStatus(EnumStatus.CREATED);
                cr.setVersion(1L);
            });
        });
        // Envers 1st revision with RevisionType.ADD
        // Retex version 1.0
        runInTransaction(() -> {
            //parentRequest1 = requestRepository.getOne(parentRequest1.getId());

            childRequest12 = createChildRequest(cr -> {
                cr.setParentRequest(parentRequest1);
                cr.setStatus(EnumStatus.CREATED);
                cr.setVersion(1L);
                // Envers 1st revision with RevisionType.ADD
                // Retex version 1.0
            });
        });
        runInTransaction(() -> {
            //childRequest11 = childRequestRepository.getOne(childRequest11.getId());
            client1 =dataSetInitializer.createClient("client1");
            client2 =dataSetInitializer.createClient("client2");
            childRequest11.addClient(client1);
            childRequest11.addClient(client2);
            phPart1 = dataSetInitializer.createPhysicalPart(childRequest11, "sn10");
            phPart2 = dataSetInitializer.createPhysicalPart(childRequest11, "sn20");
            childRequest11.addPhysicalPart(phPart1);
            childRequest11.addPhysicalPart(phPart2);
            childRequest11 = childRequestRepository.save(childRequest11);
            // Envers 2nd revision with RevisionType.MOD
            // Retex version 1.0

        });
        runInTransaction(() -> {
            //childRequest11 = childRequestRepository.getOne(childRequest11.getId());
            childRequest11.setAircraftFamily(dataset.aircraft_family_2);
            childRequest11 = childRequestRepository.save(childRequest11);
            // Envers 3rd revision with RevisionType.MOD
            // Retex version 1.0
        });
        runInTransaction(() -> {
            //childRequest11 = childRequestRepository.getOne(childRequest11.getId());
            childRequest11.setStatus(EnumStatus.VALIDATED);
            childRequest11.setVersion(childRequest11.getVersion() + 1);
            childRequest11.setDrtToInspect(DRT_TO_INSPECT);
            childRequest11 = childRequestRepository.save(childRequest11);

            // Envers 4th revision with RevisionType.MOD
            // Retex version 2.0

        });
        runInTransaction(() -> {
            childRequest11.setDrtToInspect(DRT_TO_INSPECT * 2);
            childRequest11.setVersion(childRequest11.getVersion() + 1);
            childRequest11 = childRequestRepository.save(childRequest11);
            // Envers 5th revision with RevisionType.MOD
            // Retex version 3.0
        });
    }

    @Test
    public void getVersions_after_delete_ok ()  {
        assertDoesNotThrow(() -> {
            runInTransaction(() -> {
                childRequest11 = childRequestRepository.getOne(childRequest11.getId());
                childRequest11.setStatus(EnumStatus.DELETED);
                childRequest11 = childRequestRepository.save(childRequest11);
                // Envers 6th revision with RevisionType.MOD
                // Retex version 3.0
            });
            Long childRequest11_ID = childRequest11.getId();
            Long childRequest12_ID = childRequest12.getId();
            List<VersionDto> dtoList = childRequestService.getVersions(ChildRequest.class, childRequest11_ID);
            assertThat(dtoList.get(dtoList.size()-1).getVersionNumber(), equalTo(3L));
            assertThat(dtoList.size(), equalTo(3));
            childRequestRepository.delete(childRequest11);
            // Envers 7th revision with RevisionType.DEL
            // After the delete the get version should still return 5 items
            dtoList =childRequestService.getVersions(ChildRequest.class, childRequest11_ID);
            assertThat(dtoList.get(dtoList.size()-1).getVersionNumber(), equalTo(3L));
            assertThat(dtoList.size(), equalTo(3));

            // Reading the THIRD envers revision => it is not index 3...the audit table revisions for a given id is not incremented by ONE !
            Optional<ChildRequest> specificRevisionOpt = childRequestService.getSpecificVersion(ChildRequest.class, childRequest11.getId(), dtoList.get(0).getVersionNumber(), dtoList.get(0).getDateUpdate());
            CheckFoundSpecificVersion(specificRevisionOpt);
            specificRevisionOpt = childRequestService.getSpecificVersion(ChildRequest.class, childRequest11.getId(), dtoList.get(0).getVersionNumber(), null);
            CheckFoundSpecificVersion(specificRevisionOpt);

            dtoList =childRequestService.getVersions(ChildRequest.class, childRequest12_ID);
            assertThat(dtoList.get(dtoList.size()-1).getVersionNumber(), equalTo(1L));
            childRequestRepository.delete(childRequest12);
            dtoList =childRequestService.getVersions(ChildRequest.class, childRequest12_ID);
            assertThat(dtoList.get(dtoList.size()-1).getVersionNumber(), equalTo(1L));
        });

    }

    private void CheckFoundSpecificVersion(Optional<ChildRequest> specificRevisionOpt) {
        assertTrue(specificRevisionOpt.isPresent(), "The child request must be found");
        ChildRequest specificRevision = specificRevisionOpt.get();
        assertThat(specificRevision.getId(), equalTo(childRequest11.getId()));
        assertThat(specificRevision.getStatus(), equalTo(EnumStatus.CREATED));
    }

    @Test
    public void testPhysicalDelete_ok() {
        runInTransaction(() -> {
            assertDoesNotThrow(() -> {
                childRequest12 = childRequestRepository.getOne(childRequest12.getId());
                childRequestService.safeDelete(childRequest12.getId());
                assertTrue(childRequestRepository.findById(childRequest12.getId()).isEmpty(), "The child request must be deleted");
            });
        });
    }
    @Test
    public void testLogicalDelete_ok() {
        Long currentChildRequestVersion = childRequest11.getVersion();
        runInTransaction(() -> {
            assertDoesNotThrow(() -> {
                // child request 11 is in validated status ! the current instance should be flagged DELETED and we roleback to previous version
                childRequestService.safeDelete(childRequest11.getId());
            });
        });
        // check we're back to last validated
        checkValues(childRequest11.getId(), currentChildRequestVersion - 1, EnumStatus.VALIDATED);

        runInTransaction(() -> {
            ChildRequest persistedCR = childRequestRepository.getOne(childRequest11.getId());
            assertThat(persistedCR.getDrtToInspect(), equalTo(DRT_TO_INSPECT));
        });
    }

    @Test
    public void testVersionIncrement_Validated_Status_ok() {
        runInTransaction(() -> {
            childRequest11 = childRequestRepository.getOne(childRequest11.getId());
            childRequest11.setAircraftFamily(dataset.aircraft_family_2);
            assertDoesNotThrow(() -> {
                childRequestService.safeSave(childRequest11, true, false);
            });
        });
        checkValues(childRequest11.getId(), 4L, EnumStatus.VALIDATED);
    }

    @Test
    public void testVersionIncrement_Create_Status_ok() {
        runInTransaction(() -> {
            childRequest12 = childRequestRepository.getOne(childRequest12.getId());
            childRequest12.setAircraftFamily(dataset.aircraft_family_2);
            assertDoesNotThrow(() -> {
                childRequestService.safeSave(childRequest12, true, false);
            });
        });
        checkValues(childRequest12.getId(), 1L, EnumStatus.CREATED);
    }

    private void checkValues(final Long childRequestID, final Long expectedVersion, final EnumStatus expectedStatus) {
        Optional<ChildRequest> childRequestOpt = childRequestRepository.findById(childRequestID);
        assertThat(childRequestOpt.isPresent(), is(true));
        ChildRequest latestChildRequest = childRequestOpt.get();
        assertThat(latestChildRequest.getVersion(), equalTo(expectedVersion));
        assertThat(latestChildRequest.getStatus(), equalTo(expectedStatus));
    }
}