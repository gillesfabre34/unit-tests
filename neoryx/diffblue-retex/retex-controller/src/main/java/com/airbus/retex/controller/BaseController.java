package com.airbus.retex.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.service.IService;

public class BaseController<S extends IService, D extends Dto> implements IController<D> {

    @Autowired
    protected S service;

    @Override
    public ResponseEntity<PageDto<D>> search() {
        return ResponseEntity.ok().body(service.search());
    }

    @Override
    public ResponseEntity<List<D>> all() {
        return ResponseEntity.ok().body(service.search().getResults());
    }
}
