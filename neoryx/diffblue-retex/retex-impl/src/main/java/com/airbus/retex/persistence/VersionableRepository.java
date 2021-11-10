package com.airbus.retex.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

@NoRepositoryBean
public interface VersionableRepository<T, I> extends Repository<T, I> {

    List<T> findAllLastVersions();

    List<T> findAllValidatedVersions();

    Page<T> findAllLastVersions(Pageable pageable);

    List<T> findAllLastVersions(Specification<T> specification);

    Page<T> findAllLastVersions(Specification<T> specification, Pageable pageable);

    long count();//FIXME Remove or rename+reimplement ?

    <S extends T> S saveVersion(S versionEntity);

    Optional<T> findByNaturalIdAndVersion(Long naturalId, Long versionNumber);

    Optional<T> findLastVersionByNaturalId(Long naturalId);

    Optional<T> findValidatedVersionByNaturalId(Long naturalId);

    List<T> findAllVersionsByNaturalId(Long naturalId);

    void deleteVersion(T versionEntity);

    void updateLastValidatedEntityToObsolate(Long naturalId);

    Optional<T> findPreviousVersionByNaturalId(Long naturalId);
}
