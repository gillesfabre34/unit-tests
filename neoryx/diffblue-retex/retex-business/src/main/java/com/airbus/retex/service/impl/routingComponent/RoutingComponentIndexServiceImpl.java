package com.airbus.retex.service.impl.routingComponent;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentCreateUpdateDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentFilteringDto;
import com.airbus.retex.business.dto.routingComponent.RoutingComponentIndexDto;
import com.airbus.retex.business.dto.versioning.VersionDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.routingComponent.specification.RoutingComponentSpecification;
import com.airbus.retex.persistence.routingComponent.RoutingComponentIndexRepository;
import com.airbus.retex.service.impl.routingComponent.mapper.RoutingComponentIndexServiceDtoMapper;
import com.airbus.retex.service.impl.versionable.AbstractVersionableService;
import com.airbus.retex.service.routingComponent.IRoutingComponentIndexService;
import com.airbus.retex.service.versioning.IVersionableService;

@Service
@Transactional(rollbackFor = Exception.class)
public class RoutingComponentIndexServiceImpl
        extends AbstractVersionableService<RoutingComponentIndex, Long, RoutingComponentCreateUpdateDto>
        implements IRoutingComponentIndexService, IVersionableService {

    @Autowired
    private RoutingComponentIndexRepository routingComponentIndexRepository;
    @Autowired
    private DtoConverter dtoConverter;
    @Autowired
    private NameComputeServiceImpl nameComputeServiceImpl;
    @Autowired
    private RoutingComponentIndexServiceDtoMapper routingComponentIndexServiceDtoMapper;

    @Override
    public PageDto<RoutingComponentIndexDto> findRoutingComponentsIndex(RoutingComponentFilteringDto filtering, Locale locale) {
        Pageable pageable = PageRequest.of(filtering.getPage(), filtering.getSize(), Sort.by("creationDate"));

        Page<RoutingComponentIndex> pageItemRoutingComponentsIndex = routingComponentIndexRepository.findAllLastVersions(buildSpecification(filtering), pageable);
        nameComputeServiceImpl.computeNames(pageItemRoutingComponentsIndex, locale);

        return new PageDto<>(routingComponentIndexServiceDtoMapper.pageRoutingComponentIndexToDto(pageItemRoutingComponentsIndex),
                pageItemRoutingComponentsIndex.getTotalElements(), pageItemRoutingComponentsIndex.getTotalPages());
    }

    @Override
    public void deleteRoutingComponent(Long id) throws FunctionalException {
        Optional<RoutingComponentIndex> routingComponentIndexOpt = routingComponentIndexRepository.findLastVersionByNaturalId(id);
        if(routingComponentIndexOpt.isPresent()){
            deleteVersion(routingComponentIndexOpt.get().getNaturalId());
        } else {
            throw new NotFoundException("retex.error.routingComponent.not.found");
        }
    }

    /**
 * @param filtering
 * @return
     */
    private Specification<RoutingComponentIndex> buildSpecification(RoutingComponentFilteringDto filtering) {
        Specification<RoutingComponentIndex> specification = Specification.where(null);

        if (null != filtering.getOperationTypeId()) {
            specification = specification.and(RoutingComponentSpecification.filterByOperation(filtering.getOperationTypeId()));
        }

        if (null != filtering.getFunctionalityId()) {
            specification = specification.and(RoutingComponentSpecification.filterByFunctionality(filtering.getFunctionalityId()));
        }

        if (null != filtering.getTodoListNameId()) {
            specification = specification.and(RoutingComponentSpecification.filterByTodoListName(filtering.getTodoListNameId()));
        }

        if (null != filtering.getSubTaskId()) {
            specification = specification.and(RoutingComponentSpecification.filterByDamage(filtering.getSubTaskId()));
        }

        if (null != filtering.getInspectionId()) {
            specification = specification.and(RoutingComponentSpecification.filterByInspection(filtering.getInspectionId()));
        }

        if (null != filtering.getStatus()) {
            specification = specification.and(RoutingComponentSpecification.filterByStatus(filtering.getStatus().name()));
        }
        return specification;
    }

    @Override
    public List<VersionDto> findAllVersionsByNaturalId(Long naturalId) {
        List<RoutingComponentIndex> routingComponentIndexList = routingComponentIndexRepository.findAllVersionsByNaturalId(naturalId);
        return  dtoConverter.toDtos(routingComponentIndexList, VersionDto.class);
    }

    @Override
    protected void mapDtoToVersion(RoutingComponentCreateUpdateDto updateDto, RoutingComponentIndex version){
        // do nothing
    }

    @Override
    protected RoutingComponentIndex cloneVersion(RoutingComponentIndex version) {
        return null;
    }
}


