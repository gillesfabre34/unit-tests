package com.airbus.retex.service.historization;

import com.airbus.retex.AbstractServiceIT;



import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.request.Request;
import com.airbus.retex.persistence.childRequest.ChildRequestRepository;
import com.airbus.retex.persistence.request.RequestRepository;

import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.function.Consumer;



public abstract class HistorizationServiceAbstract extends AbstractServiceIT {//TODO Is this class usefull ? (should use Datasetinitializer ?)


    @Autowired
    protected RequestRepository requestRepository;

    @Autowired
    protected ChildRequestRepository childRequestRepository;




    protected Request createRequest(String name ) {
        return createRequest(name ,r->{});
    }

    protected Request createRequest(String name, Consumer<Request> modifyRequest) {
        Request request = new Request();
        request.setStatus(EnumStatus.CREATED);
        request.setReference("RQ_" + dataSetInitializer.getNextCode(10));
        request.setCreationDate(LocalDateTime.now());
        request.setAirbusEntity(dataset.airbusEntity_france);
        request.setRequester(dataset.user_superAdmin);
        request.setOrigin(dataset.ORIGIN_CIVP);
        //request.setValidatorId(dataset.user_simpleUser2.getId());
        request.setDueDate(LocalDate.now().plusDays(3) );
        request.setEnvironment(dataset.environment_1);
        request.setAircraftFamily(dataset.aircraft_family_1);
        request.addAircraftType(dataset.aircraft_type_1);
        request.addAircraftVersion(dataset.aircraft_version_1);
        request.setMissionType(dataset.mission_1);
        //request.addClient(dataset.client_1);
        //request.addOperator(dataset.user_simpleUser);
        request.setOetp("");
        request.setOriginComment("");
        request.setSpecComment("");
        request.addOriginMedia(dataset.media_1);
        request.addSpecMedia(dataset.media_1);
        //request.setTechnicalResponsibles(new HashSet<>(Arrays.asList(dataset.user_superAdmin)));//TODO change wither user as thechnical responsable after merge with US#12°

        modifyRequest.accept(request);
        request = requestRepository.save(request);


        return request;
    }
    protected ChildRequest createChildRequest( ) {
        return createChildRequest(cr -> {});

    }

    protected ChildRequest createChildRequest( Consumer<ChildRequest> modifyRequest) {
        ChildRequest childRequest = new ChildRequest();
        childRequest.setStatus(EnumStatus.CREATED);
        childRequest.setDrtToInspect(55L);
        childRequest.setModulation(25);


        childRequest.setMissionType(dataset.mission_1);

        modifyRequest.accept(childRequest);
        if (childRequest.getParentRequest() != null) {
            Request parentRequest = childRequest.getParentRequest();
            parentRequest.addChildRequest(childRequest);
            requestRepository.save(parentRequest);
        }
        childRequest = childRequestRepository.save(childRequest);



        return childRequest;
    }

}