package com.airbus.retex.controller;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.service.BaseService;
import com.airbus.retex.service.IService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.function.Supplier;

@Getter
@Setter
public class BaseControllerDiffBlue<S extends IService> {

    @Autowired
    public S service;
    public Object PageDto;

    public Dto searchSupplier() {
        Supplier<Dto> supplier = new Supplier<Dto>() {
            @Override
            public Dto get() {
                return null;
            }
        };
        supplier.get();
        return supplier.get();
    }


    public PageDto search() {
        Supplier<Dto> supplier = new Supplier<Dto>() {
            @Override
            public Dto get() {
                return null;
            }
        };
        IService baseService = new BaseService(supplier) {
            @Override
            public com.airbus.retex.business.dto.PageDto search() {
                return super.search();
            }
        };
        PageDto pageDto = service.search();
        return pageDto;
//        return ResponseEntity.ok().body(service.search());
    }

//    @Override
//    public ResponseEntity<List<D>> all() {
//        return ResponseEntity.ok().body(service.search().getResults());
//    }
}
