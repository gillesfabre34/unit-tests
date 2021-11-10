package com.airbus.retex.persistence.drt;

import com.airbus.retex.model.drt.AbstractDrtOperationStatus;
import com.airbus.retex.model.drt.DrtPictures;
import com.airbus.retex.persistence.CustomJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrtOperationStatusRepository extends JpaRepository<AbstractDrtOperationStatus, Long>, CustomJpaRepository<DrtPictures>, PagingAndSortingRepository<AbstractDrtOperationStatus, Long> {

}
