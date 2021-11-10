package com.airbus.retex.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.PageDto;

public interface IController<D extends Dto> {
    ResponseEntity<PageDto<D>> search();
    ResponseEntity<List<D>> all();
}
