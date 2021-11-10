package com.airbus.retex.service.historization;
import com.airbus.retex.business.dto.versioning.VersionDto;
import com.airbus.retex.business.exception.FunctionalException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface IHistorizationService<T> {
    /**
     * Retrieves all revisions (excepted those that have DELETED status and those that are not really deleted .... olders FIRST.
     * The CREATED revisions are grouped together....it is not possible to have for example 3 entries with version 1!
     * @param cls the entity class
     * @param id the id of the entity
     * @return the revision list
     */
    List<VersionDto> getVersions(final Class<T> cls, final Long id);

    /**
     * Returns a specific version or null if not found.
     * @param cls the entity class
     * @param id the id of the entity
     * @param version the required version of the entity
     * @param localDateTime the creation date (if not specified "null") latest is found
     * @return the found entity from audit table or NULL
     */
    Optional<T> getSpecificVersion(final Class<T> cls, final Long id, final Long version, final LocalDateTime localDateTime) throws FunctionalException;

    /**
     * Retrieves the latest revision.
     * @param cls the entity instance which we want to retrieve the previous version
     * @param id the id of the entity
     * @return the lates audited version
     * @throws FunctionalException
     */
    T findSecondFromHeadAuditedRevision(final T entity, final Class<T> cls, final Long id) throws FunctionalException;


    /**
     * Performs a safe delete. This delete will do the required stuffs (put to status DELETED and rollback to previous version or a real and physical delete if status CREATED)
     * @param entityId the entity ID for the delete
     * @throws FunctionalException if the entity super class is not related to historization service.
     */
    void safeDelete(final Long entityId) throws FunctionalException;
    /**
     * Performs a safe save. This save increment the version if entity is in status  VALIDATED
     * @param entity the entity instance which we want to delete
     * @param incrementVersion increment or not the version number
     * @param flush is true it make a save and flush instead of save
     */
    T safeSave(final T entity, final boolean incrementVersion, final boolean flush) throws FunctionalException;

    void repositoryDelete(final T entity) throws FunctionalException;

    /**
     * Makes a repository call for saving
     * @param entity instance to save
     * @param flush is true it make a save and flush instead of save
     *
     * @return the saved instance
     */
    T repositorySave(final T entity, boolean flush);

    void copyFrom(final T source, final T target);

    /**
     * Functions that verfifies if the entity is deletable.
     * @param entity the entity
     * @return true if entity is deletable
     */
    Optional<FunctionalException> checkIsDeletable(final T entity);
}
