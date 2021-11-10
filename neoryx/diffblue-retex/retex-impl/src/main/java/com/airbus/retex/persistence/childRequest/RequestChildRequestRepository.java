package com.airbus.retex.persistence.childRequest;

import com.airbus.retex.model.request.RequestChildRequest;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestChildRequestRepository extends JpaRepository<RequestChildRequest, Long>, CustomJpaRepository<RequestChildRequest> {

}
