package com.airbus.retex.persistence.childRequest;


import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhysicalPartRepository extends JpaRepository<PhysicalPart, Long>, CustomJpaRepository<PhysicalPart> {

    List<PhysicalPart> findByChildRequestAndSerialNumberNotIn(ChildRequest childRequest, List<String> serialNumbers);

    Optional<PhysicalPart> findByChildRequestAndSerialNumber(ChildRequest childRequest, String serialNumber);

    Optional<PhysicalPart> findByPartPartNumberAndSerialNumber(String partNumber, String serialNumber);

    Optional<PhysicalPart> findByEquipmentNumber(String equipmentNumber);

    boolean existsBySerialNumberAndPartAndChildRequestNot(String serialNumber, Part part, ChildRequest childRequest);

    boolean existsByPart(Part part);
}
