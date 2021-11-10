package com.airbus.retex.service;

import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.business.dto.PageDto;

public interface IService<D extends Dto> {
    PageDto<D> search();
}
