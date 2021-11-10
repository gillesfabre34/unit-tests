package com.airbus.retex.service.impl.historization;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.dto.versioning.VersionDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.basic.AbstractHistorizableBaseModel;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.service.historization.IHistorizationService;


@Service
@Transactional(rollbackFor = Exception.class)
public abstract class AbstractHistorizationServiceImpl<T extends AbstractHistorizableBaseModel> implements IHistorizationService<T> {

    private static final String RETEX_ERROR_HISTORIZATION_INVALID_ENTITY_ID = "retex.error.historization.invalid.entity.id";

	@Autowired
    private EntityManager entityManager;

    @Autowired
    private JpaRepository<T, Long> repository;


    private static LocalDateTime convertToLocalDateTime(final Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private static Date convertToDate(final LocalDateTime dateToConvert){
        return java.util.Date
                .from(dateToConvert.atZone(ZoneId.systemDefault())
                        .toInstant());
    }

    @Override
    public List<VersionDto> getVersions(final Class<T> cls, final Long id) {

        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        List<VersionDto> versionList = new ArrayList<>();
        VersionDto previousVersionDto = null;
        // Get the list of all revisions (even those that are really deleted)
        List<Number> revisions = auditReader.getRevisions(cls, id);
        Long currentVersion = null;
        for (Number revision : revisions) {
            // find the instance from the audit table (don't include deleted one)
            T entityInstance = auditReader.find(cls, cls.getTypeName(), id, revision, false);
            if (!(entityInstance instanceof AbstractHistorizableBaseModel)) {
                continue;
            }

            AbstractHistorizableBaseModel versionableEntityInstance = entityInstance;
            EnumStatus versionStatus = versionableEntityInstance.getStatus();
            if (versionStatus.equals(EnumStatus.DELETED)) {
                continue;
            }

            currentVersion = versionableEntityInstance.getVersion();
            LocalDateTime convertedDate = convertToLocalDateTime(auditReader.getRevisionDate(revision));
            // We use the previous DTO as the CREATED ONE is newer than the previous one
            if (versionStatus.equals(EnumStatus.CREATED) && (previousVersionDto != null) && previousVersionDto.getStatus().equals(EnumStatus.CREATED)) {
                previousVersionDto.setDateUpdate(convertedDate);
                // When revisions are in created status the version stays untouched ...only the date changes
            } else {
                if (previousVersionDto != null) {
                    previousVersionDto.setIsLatestVersion(false);
                }
                previousVersionDto = new VersionDto(
                    convertedDate,
                    currentVersion,
                    versionStatus,
                    true);

                int index = previousVersionDto.getVersionNumber().intValue() - 1;
                if (versionList.size() > index) {
                    versionList.set(index, previousVersionDto);
                } else {
                    versionList.add(previousVersionDto);
                }
            }
        }

        int size = versionList.size();
        for(int i = 0; i < size; i++) {
            if (i < size-1 && versionList.get(i).getStatus().equals(EnumStatus.VALIDATED)) {
                versionList.get(i).setStatus(EnumStatus.OBSOLETED);
            }
        }

        return versionList;
    }


    @Override
    public T findSecondFromHeadAuditedRevision(final T entity, final Class<T> cls , final Long id) throws FunctionalException{

        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        Number headRevisionNumber = getSecondFromHeadRevisionNumber(auditReader, cls, id);
        if ((headRevisionNumber != null ) &&( entity instanceof AbstractHistorizableBaseModel)) {
            return auditReader.find(cls, id, headRevisionNumber);
        }
        throw new FunctionalException("retex.error.historization.invalid.super.class");

    }

    @Override
    public void safeDelete(final Long entityId) throws FunctionalException {
        if (entityId == null) {
            throw new FunctionalException(RETEX_ERROR_HISTORIZATION_INVALID_ENTITY_ID);
        }

        Optional<T> lastVersionOpt = repository.findById(entityId);

        if (!lastVersionOpt.isPresent()) {
            throw new FunctionalException(RETEX_ERROR_HISTORIZATION_INVALID_ENTITY_ID);
        }

        T lastVersion = lastVersionOpt.get();

        if (lastVersion.getStatus() == EnumStatus.CREATED) {
            repositoryDelete(lastVersion);
        } else if (lastVersion.getStatus() == EnumStatus.VALIDATED) {
            lastVersion.setStatus(EnumStatus.DELETED);
            T previousVersion = findSecondFromHeadAuditedRevision(lastVersion, (Class<T>) lastVersion.getClass(), lastVersion.getId());
            repositorySave(lastVersion, false);
            if (previousVersion != null) {
                copyFrom(previousVersion, lastVersion);
                repositorySave(lastVersion, false);
            }
        }
    }

    @Override
    public T safeSave(final T entity, final boolean incrementVersion, final boolean flush) throws  FunctionalException {
		if (entity instanceof AbstractHistorizableBaseModel) {
            AbstractHistorizableBaseModel versionedEntity = entity;
            if (versionedEntity.getStatus() == EnumStatus.VALIDATED) {
                if (versionedEntity.getVersion() == null) {
                    throw new FunctionalException("retex.error.historization.invalid.revision.number");
                }
                if (incrementVersion) {
                    versionedEntity.setVersion(versionedEntity.getVersion() + 1);
                }
                return repositorySave(entity, flush);
            }
        }
        return repositorySave(entity, flush);
    }
    @Override
    public void repositoryDelete(final T entity) throws FunctionalException {
        repository.delete( entity);
    }

    @Override
    public T repositorySave(final T entity, final boolean flush) {
        if (flush) {
            return repository.saveAndFlush(entity);
        }
        return  repository.save(entity);
    }

    @Override
    public Optional<T> getSpecificVersion(final Class<T> cls, final Long id, final Long version, final LocalDateTime localDateTime) throws FunctionalException{
        AuditReader auditReader = AuditReaderFactory.get(entityManager);
        Date date;
        if (id == null) {
            throw new FunctionalException(RETEX_ERROR_HISTORIZATION_INVALID_ENTITY_ID);
        }
        if (version == null) {
            throw new FunctionalException("retex.error.historization.invalid.entity.version");
        }

        if (localDateTime == null) {
            List<VersionDto> versions = getVersions(cls, id);
            for (VersionDto versionDto : versions) {
                if (versionDto.getVersionNumber().equals(version))  {
                    date = convertToDate(versionDto.getDateUpdate());
                    return Optional.ofNullable(auditReader.find(cls, id, date));
                }
            }
            return Optional.ofNullable(null);
        }else {
            date = convertToDate(localDateTime);
            return Optional.ofNullable(auditReader.find(cls, id, date));
        }
    }

    private Number getSecondFromHeadRevisionNumber(final AuditReader auditReader , final Class<T> cls, final Long id) {
        Number result = null;
        List<Number> revisions = auditReader.getRevisions(cls, id);
        if (!revisions.isEmpty() && (revisions.size() > 1)) {
            result = revisions.get(revisions.size() - 2);
        }
        return result;
    }


}
