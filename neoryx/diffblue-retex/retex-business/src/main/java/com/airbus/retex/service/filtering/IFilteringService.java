package com.airbus.retex.service.filtering;

import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.filtering.*;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.common.Language;


public interface IFilteringService {

    //DEBUG TODO add api doc

    PageDto<FilteringDto> findFilteringsWithFilters(FilteringFiltersDto filteringFiltersDto) throws FunctionalException;

    FilteringFullDto findFilteringById(final Long id, Language language) throws FunctionalException;

    FilteringDto createFiltering(FilteringCreationDto filteringCreationDto) throws FunctionalException;

    Long createDrt(final Long id) throws FunctionalException;

    FilteringDto updateFiltering(Long id, FilteringUpdateDto filteringUpdateDto) throws FunctionalException;

}
