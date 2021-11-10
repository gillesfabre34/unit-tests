package com.airbus.retex.service.impl.versionable;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.business.util.ThrowingConsumer;
import com.airbus.retex.model.basic.AbstractVersionableModel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.persistence.VersionableRepository;
import com.airbus.retex.service.translate.ITranslateService;
import com.airbus.retex.service.versioning.IVersionableService;

public abstract class AbstractVersionableService<T extends AbstractVersionableModel<I>, I, D extends Dto>
        implements IVersionableService {

    @Autowired
    protected VersionableRepository<T, I> repository;
    @Autowired
    protected ITranslateService translateService;

    protected T createVersion(T newVersionable){
        if(newVersionable.getTechnicalId() != null) {
            throw new IllegalArgumentException("A new versionable cannot be created with a techId != null");
        }
        if(newVersionable.getNaturalId() != null) {
            throw new IllegalArgumentException("A new versionable cannot be created with a naturalId != null");
        }
        return internalSave(newVersionable, true);
    }

    private T internalSave(T version, boolean isNewVersionable) {
        if(isNewVersionable) {
            version.setVersionNumber(1L);
            version.setCreationDate(LocalDateTime.now());
        }
        version.setIsLatestVersion(true);
        version.setDateUpdate(LocalDateTime.now());
        return repository.saveVersion(version);
    }

    protected T updateVersion(Long naturalId, ThrowingConsumer<T, FunctionalException> updateVersion) throws FunctionalException {
        Optional<T> lastVersionOpt = repository.findLastVersionByNaturalId(naturalId);
        if (!lastVersionOpt.isPresent()) {
            throw new FunctionalException("retex.generic.notFound");
        }
        T lastVersion = lastVersionOpt.get();
        Long lastVersionNumber = lastVersion.getVersionNumber();

        T versionToUpdate;
        if(EnumStatus.VALIDATED.equals(lastVersion.getStatus())) { // Create new version
            versionToUpdate = cloneVersion(lastVersion);
            updateVersion.accept(versionToUpdate);
            versionToUpdate.setVersionNumber(lastVersionNumber+1);
            lastVersion.setIsLatestVersion(false); //maj bool last version
            repository.saveVersion(lastVersion);
        } else { // Update old version
            versionToUpdate = lastVersion;
            updateVersion.accept(versionToUpdate);
        }

        if(EnumStatus.VALIDATED.equals(versionToUpdate.getStatus())) {
            repository.updateLastValidatedEntityToObsolate(naturalId);
        }
        return internalSave(versionToUpdate, false);
    }

    public void deleteVersion(Long naturalId) throws FunctionalException {
        Optional<T> lastVersionOpt = repository.findLastVersionByNaturalId(naturalId);
        if (!lastVersionOpt.isPresent()) {
            throw new NotFoundException("retex.generic.notFound");
        }
        T lastVersion = lastVersionOpt.get();
        if(!lastVersion.getIsLatestVersion()){
            throw new NotFoundException("retex.not.latest");
        }

        Optional<T> optOldEntity = repository.findPreviousVersionByNaturalId(naturalId);
        if(optOldEntity.isPresent()){ // Si version Precédente existe
            T oldEntity = optOldEntity.get();
            if (EnumStatus.OBSOLETED.equals(oldEntity.getStatus())) {
                oldEntity.setStatus(EnumStatus.VALIDATED);
            }
            oldEntity.setIsLatestVersion(true);
            repository.saveVersion(oldEntity);
        }
        repository.deleteVersion(lastVersion);
    }

    protected abstract void mapDtoToVersion(D updateDto, T version) throws FunctionalException ;

    protected abstract T cloneVersion(T version);

}
