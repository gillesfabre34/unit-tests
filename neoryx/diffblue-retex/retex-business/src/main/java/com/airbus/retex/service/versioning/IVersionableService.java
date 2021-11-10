package com.airbus.retex.service.versioning;

import java.util.List;

import com.airbus.retex.business.dto.versioning.VersionDto;

/**
 * Implements this interface on versionable entity's service class.
 */
public interface IVersionableService {

    /**
     * Returns all versions of given item.
     *
     * @param naturalId
     * @return
     */
    List<VersionDto> findAllVersionsByNaturalId(Long naturalId);
}
