package com.airbus.retex.persistence.impl;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.model.basic.AbstractVersionableModel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.persistence.VersionableRepository;


public class VersionableRepositoryImpl<T, I> extends SimpleJpaRepository<T, I> implements VersionableRepository<T, I> {

    private final JpaEntityInformation<T, ?> entityInformation;


    private final boolean isVersionable;//FIXME remove useless test of this boolean in custom methods of VersionableRepository

    /**
     * Creates a new {@link VersionableRepositoryImpl} to manage objects of the given {@link VersionableRepositoryImpl}.
     *
     * @param entityInformation must not be {@literal null}.
     * @param entityManager must not be {@literal null}.
     */
    public VersionableRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);

        this.entityInformation = entityInformation;

        //if versionable entity
        isVersionable = AbstractVersionableModel.class.isAssignableFrom(entityInformation.getJavaType());
    }

    public VersionableRepositoryImpl(Class<T> domainClass, EntityManager em) {
        this(JpaEntityInformationSupport.getEntityInformation(domainClass, em), em);
    }

    private void checkIsVersionable() {
        if(!isVersionable) {
            throw new UnsupportedOperationException("The entity "
                    +entityInformation.getJavaType().getSimpleName()
                    +"is not a subtype of "+AbstractVersionableModel.class.getSimpleName());
        }
    }

    @Override
    public List<T> findAllValidatedVersions() {
        checkIsVersionable();
        return super.findAll(isLatestVersion());
    }

    @Override
    public List<T> findAllLastVersions() {
        checkIsVersionable();
        return super.findAll(byStatus(EnumStatus.VALIDATED));
    }

    @Override
    public Page<T> findAllLastVersions(Pageable pageable) {
        checkIsVersionable();
        return super.findAll(isLatestVersion(), pageable);
    }

    @Override
    public Page<T> findAllLastVersions(Specification<T> specification, Pageable pageable) {
        checkIsVersionable();
        return super.findAll(specification.and(isLatestVersion()), pageable);
    }

    @Override
    public List<T> findAllLastVersions(Specification<T> specification) {
        checkIsVersionable();
        return super.findAll(specification.and(isLatestVersion()));
    }

    @Override
    @Transactional
    public <S extends T> S saveVersion(S entity) {
        checkIsVersionable();
        return super.save(entity);
    }

    @Override
    public Optional<T> findByNaturalIdAndVersion(Long naturalId, Long version) {
        checkIsVersionable();
        if (null == version) {
            return findLastVersionByNaturalId(naturalId);
        }
        Specification<T> spec = isVersionNumber(version);
        spec = spec.and(byNaturalId(naturalId));
        return super.findOne(spec);
    }

    @Override
    public void updateLastValidatedEntityToObsolate(Long naturalId) {
        checkIsVersionable();

        Specification<T> spec = isNotLatestVersion();
        spec = spec.and(byStatus(EnumStatus.VALIDATED));
        spec = spec.and(byNaturalId(naturalId));

        Optional<T> opt = super.findOne(spec);
        if(opt.isPresent()){
            T entity = opt.get();
            if(entity instanceof AbstractVersionableModel) {
                ((AbstractVersionableModel) entity).setStatus(EnumStatus.OBSOLETED);
                super.save(entity);
            }
        }
    }

    @Override
    public Optional<T> findPreviousVersionByNaturalId(Long naturalId) {
        checkIsVersionable();
        Specification<T> spec = isNotLatestVersion();
        spec = spec.and(byNaturalId(naturalId));
        Sort sort =byDateUpdate(Sort.Direction.DESC);
        List<T> listT = super.findAll(spec, sort);
        if(listT.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(listT.get(0));
    }

    @Override
    public Optional<T> findLastVersionByNaturalId(Long naturalId){
        checkIsVersionable();

        Specification<T> spec = isLatestVersion()
                .and(byNaturalId(naturalId));
        return super.findOne(spec);
    }

    @Override
    public Optional<T> findValidatedVersionByNaturalId(Long naturalId) {
        checkIsVersionable();

        Specification<T> spec = byStatus(EnumStatus.VALIDATED)
                .and(byNaturalId(naturalId));
        return super.findOne(spec);
    }

    @Override
    public List<T> findAllVersionsByNaturalId(Long naturalId){
        checkIsVersionable();

        Specification<T> spec = byNaturalId(naturalId);
        Sort sort =byDateUpdate(Sort.Direction.ASC);
        return super.findAll(spec, sort);
    }

    @Override
    public void deleteVersion(T version) {
        checkIsVersionable();

        super.delete(version);
    }

    private Specification<T> isLatestVersion(){
        return (root, query, cb) -> cb.equal(root.get("isLatestVersion"), true);
    }

    private Specification<T> isNotLatestVersion(){
        return (root, query, cb) -> cb.equal(root.get("isLatestVersion"), false);
    }

    private Specification<T> isVersionNumber(Long version) {
        return (root, query, cb) -> cb.equal(root.get("versionNumber"), version);
    }

    private Specification<T> byNaturalId(Long naturalId) {
        return (root, query, cb) -> cb.equal(root.get("naturalId"), naturalId);
    }

    private Specification<T> byStatus(EnumStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    private Sort byDateUpdate(Sort.Direction direction){
        return Sort.by(direction,"dateUpdate");
    }

}
