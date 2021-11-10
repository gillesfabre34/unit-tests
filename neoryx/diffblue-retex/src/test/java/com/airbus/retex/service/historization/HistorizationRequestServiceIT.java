package com.airbus.retex.service.historization;



import com.airbus.retex.business.dto.versioning.VersionDto;
import com.airbus.retex.model.ata.ATA;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.client.Client;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.childRequest.ChildRequestRepository;
import com.airbus.retex.persistence.request.RequestRepository;

import com.airbus.retex.service.impl.childRequest.ChildRequestServiceImpl;
import com.airbus.retex.service.impl.request.RequestServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;



public class HistorizationRequestServiceIT extends HistorizationServiceAbstract {

    private Request parentRequest1;
    private Request parentRequest2;
    private ChildRequest childRequest11;
    private ATA ata;
    private ChildRequest childRequest12;
    private Client cr1Client;
    private Client cr2Client;

    private User validatorUser;
    private User requesterUser;
    private User operatorUser;

    private User technicalResponsible1;
    private User technicalResponsible2;

    @Autowired
    private ChildRequestServiceImpl childRequestService;

    @Autowired
    private RequestServiceImpl requestService;


    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private ChildRequestRepository childRequestRepository;

    @Autowired
    private EntityManager entityManager;

    private static final String AN_ATA_CODE = "an_ata_code";
    private static final String CREATED_REF = "ref_001_2019";
    private static final String VALIDATED_REF = "ref_002_2019";



    @BeforeEach
    public void before() {
        ata = dataSetInitializer.createATA(a-> {a.setCode(AN_ATA_CODE);});
        parentRequest1 = createRequest("parentRequest1",
            r -> {r.setVersion(1L);r.setStatus(EnumStatus.CREATED);}
        );

        parentRequest2 = createRequest("parentRequest2",
            r -> {r.setVersion(1L);r.setStatus(EnumStatus.CREATED);}
        );
        // Envers 1st revision with RevisionType.ADD
        // Retex version 1.0
        runInTransaction(() -> {
                parentRequest1 = requestRepository.getOne(parentRequest1.getId());
                validatorUser = dataSetInitializer.createUser(u -> {
                    u.getRequests().add(parentRequest1);
                });
                operatorUser = dataSetInitializer.createUser(u -> {
                    u.getRequests().add(parentRequest1);
                });
                requesterUser = dataSetInitializer.createUser(u -> {
                    u.getRequests().add(parentRequest1);
                });
                technicalResponsible1 = dataSetInitializer.createUser(u -> {
                    u.getRequests().add(parentRequest1);
                });
                technicalResponsible2 = dataSetInitializer.createUser(u -> {
                    u.getRequests().add(parentRequest1);
                });
                parentRequest1.getTechnicalResponsibles().add(technicalResponsible1);
                parentRequest1.getTechnicalResponsibles().add(technicalResponsible2);

                parentRequest1.setValidator(validatorUser);
                parentRequest1.getOperators().add(operatorUser);
                parentRequest1.setRequester(requesterUser);

                childRequest11 = dataSetInitializer.createChildRequest(cr -> {
                    cr.setParentRequest(parentRequest1);
                    cr.setStatus(EnumStatus.CREATED);
                    cr.setVersion(1L);
                });

                childRequest12 = dataSetInitializer.createChildRequest(cr -> {
                    cr.setParentRequest(parentRequest1);
                    cr.setStatus(EnumStatus.CREATED);
                    cr.setVersion(1L);
                });
                parentRequest1.addChildRequest(childRequestRepository.getOne(childRequest11.getId()));
                parentRequest1.setAta(ata);
                cr1Client = dataSetInitializer.createClient("clientOfChildRequest11");
                cr2Client = dataSetInitializer.createClient("clientOfChildRequest12");
                cr1Client.getChildRequests().add(childRequest11);
                cr2Client.getChildRequests().add(childRequest12);

                parentRequest1 = requestRepository.save(parentRequest1);
                // Envers 2st revision with RevisionType.MOD
                // Retex version 1.0
            });
        runInTransaction(() -> {
                parentRequest1.setAta(dataset.ata_2);
                parentRequest1.setReference(CREATED_REF);
                parentRequest1.addChildRequest(childRequest12);
                parentRequest1 = requestRepository.save(parentRequest1);
        });
        runInTransaction(() -> {
                    // Envers 3st revision with RevisionType.MOD
                    // Retex version 1.0
                    parentRequest1.setAircraftFamily(dataset.aircraft_family_2);
                    parentRequest1 = requestRepository.save(parentRequest1);
                });
        // Envers 4st revision with RevisionType.MOD
        // Retex version 1.0
        runInTransaction(() -> {
                    parentRequest1.setStatus(EnumStatus.VALIDATED);
                    parentRequest1.setVersion(parentRequest1.getVersion() + 1);
                    parentRequest1.setReference(VALIDATED_REF);
                    parentRequest1 = requestRepository.save(parentRequest1);
                    // Envers 5st revision with RevisionType.MOD
                    // Retex version 2.0
                });
        runInTransaction(() -> {
            parentRequest1.setAircraftFamily(dataset.aircraft_family_1);
            parentRequest1.setVersion(parentRequest1.getVersion() + 1);
            parentRequest1 = requestRepository.save(parentRequest1);
            // Envers 6st revision with RevisionType.MOD
            // Retex version 3.0
        });


    }

    @Test
    public void getVersions_after_delete_ok ()  {
        assertDoesNotThrow(() -> {
            runInTransaction(() -> {
                        requestRepository.getOne(parentRequest1.getId());
                        parentRequest1.setStatus(EnumStatus.DELETED);
                        parentRequest1 = requestRepository.save(parentRequest1);
                        // Envers 7th revision with RevisionType.MOD
                        // Retex version 3.0
                    });
            Long parentRquest1_ID = parentRequest1.getId();

            List<VersionDto> versionDtoList = requestService.getVersions(Request.class, parentRequest1.getId());
            assertThat(versionDtoList.get(versionDtoList.size()-1).getVersionNumber(), equalTo(3L));
            assertThat(versionDtoList.size(), equalTo(3)); // We have 2 validated versions and One merged CREATED version
            requestRepository.delete(parentRequest1);
            // Envers 8th revision with RevisionType.DEL
            // After the delete the get version should still return 5 items
            versionDtoList = requestService.getVersions(Request.class, parentRquest1_ID);
            assertThat(versionDtoList.get(versionDtoList.size()-1).getVersionNumber(), equalTo(3L));
            assertThat(versionDtoList.size(), equalTo(3));


            // Reading the THIRD envers revision => it is not index 3...the audit table revisions for a given id is not incremented by ONE !
            Optional<Request> specificRevisionOpt = requestService.getSpecificVersion(Request.class, parentRquest1_ID, versionDtoList.get(0).getVersionNumber(), versionDtoList.get(0).getDateUpdate());
            checkFoundVersion(parentRquest1_ID, specificRevisionOpt);

            specificRevisionOpt = requestService.getSpecificVersion(Request.class, parentRquest1_ID, versionDtoList.get(0).getVersionNumber(), null);
            checkFoundVersion(parentRquest1_ID, specificRevisionOpt);

        });

    }

    private void checkFoundVersion(Long requestID, Optional<Request> foundSpecificRevisionOpt) {
        assertTrue(foundSpecificRevisionOpt.isPresent(), "The request must be present");
        Request specificRevision =foundSpecificRevisionOpt.get();
        assertThat(specificRevision.getId(), equalTo(requestID));
        assertThat(specificRevision.getStatus(), equalTo(EnumStatus.CREATED));
        assertThat(specificRevision.getAta().getCode(), equalTo(dataset.ata_2.getCode()));
        assertThat(specificRevision.getReference(), equalTo(CREATED_REF));
    }

    @Test
    public void testPhysicalDelete_ok() {
        assertDoesNotThrow(() -> {
            requestService.safeDelete(parentRequest2.getId());
            assertTrue(requestRepository.findById(parentRequest2.getId()).isEmpty(), "The PARENT request must be deleted");
        });
    }
    @Test
    public void testLogicalDelete_ok() {
        Long currentRequestVersion = parentRequest1.getVersion();
        assertDoesNotThrow(() -> {
            requestService.safeDelete(parentRequest1.getId());
        });

        checkValues(parentRequest1.getId(), currentRequestVersion - 1, dataset.aircraft_family_2.getId(), EnumStatus.VALIDATED);
    }
    @Test
    public void testVersionIncrement_Validated_Status_ok() {
        runInTransaction(() -> {
            parentRequest1 = requestRepository.getOne(parentRequest1.getId());
            parentRequest1.setAircraftFamily(dataset.aircraft_family_3);
            Long previousRequestVersion = parentRequest1.getVersion();
            assertDoesNotThrow(() -> {
                requestService.safeSave(parentRequest1, true, false);
            });
            checkValues(parentRequest1.getId(), previousRequestVersion + 1, dataset.aircraft_family_3.getId(), EnumStatus.VALIDATED);
        });

    }
    @Test
    public void testVersionIncrement_Create_Status_ok() {
        runInTransaction(() -> {
            parentRequest2 = requestRepository.getOne(parentRequest2.getId());
            parentRequest2.setAircraftFamily(dataset.aircraft_family_2);

            Long previousRequestVersion = parentRequest2.getVersion();
            assertDoesNotThrow(() -> {
                requestService.safeSave(parentRequest2, true, false);
            });
            checkValues(parentRequest2.getId(), previousRequestVersion, dataset.aircraft_family_2.getId(), EnumStatus.CREATED);
        });
    }


    private void checkValues(final Long requestID, final Long expectedVersion, final Long aircraftFamilyId, final EnumStatus expectedStatus) {
        Optional<Request> requestOpt = requestRepository.findById(requestID);
        assertTrue(requestOpt.isPresent(), "The child request must be present");
        Request latestRequest = requestOpt.get();
        assertTrue(latestRequest.getVersion().equals(expectedVersion) , "Version is not as expected");
        assertTrue(latestRequest.getAircraftFamily().getId().equals(aircraftFamilyId), "Data incorrectly persisted!");
        assertTrue(latestRequest.getStatus().equals(expectedStatus), "Data incorrectly persisted!");

    }
}