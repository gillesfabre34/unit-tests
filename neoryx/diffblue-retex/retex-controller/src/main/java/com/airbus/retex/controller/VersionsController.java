package com.airbus.retex.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.airbus.retex.business.dto.versioning.VersionDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.service.versioning.IVersionableService;

import io.swagger.annotations.ApiOperation;

public abstract class VersionsController<T extends IVersionableService> {

    @Autowired
    private T versionRepository;

    /**
     * Returns all versioning by entity and entity ID
     *
     * @param id
     * @return
     * @throws FunctionalException
     */
    @ApiOperation(value = "Get all versioning by entity and entity ID")
    @RequestMapping(value = "/{id}/versions", method = RequestMethod.GET)
    public ResponseEntity<List<VersionDto>> getAllVersions(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(versionRepository.findAllVersionsByNaturalId(id));
    }

}
