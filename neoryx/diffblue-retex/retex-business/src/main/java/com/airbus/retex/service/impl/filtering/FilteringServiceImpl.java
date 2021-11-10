package com.airbus.retex.service.impl.filtering;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.filtering.FilteringCreationDto;
import com.airbus.retex.business.dto.filtering.FilteringDto;
import com.airbus.retex.business.dto.filtering.FilteringFiltersDto;
import com.airbus.retex.business.dto.filtering.FilteringFullDto;
import com.airbus.retex.business.dto.filtering.FilteringUpdateDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.model.filtering.specification.FilteringSpecification;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.persistence.aircraft.AircraftFamilyRepository;
import com.airbus.retex.persistence.aircraft.AircraftTypeRepository;
import com.airbus.retex.persistence.aircraft.AircraftVersionRepository;
import com.airbus.retex.persistence.childRequest.ChildRequestRepository;
import com.airbus.retex.persistence.childRequest.PhysicalPartRepository;
import com.airbus.retex.persistence.drt.DrtRepository;
import com.airbus.retex.persistence.filtering.FilteringRepository;
import com.airbus.retex.persistence.part.PartRepository;
import com.airbus.retex.service.filtering.IFilteringService;
import com.airbus.retex.service.filtering.IRetrieveChildRequestService;
import com.airbus.retex.service.impl.filtering.mapper.FilteringDtoMapper;
import com.airbus.retex.service.impl.filtering.mapper.FilteringFullDtoMapper;
import com.airbus.retex.service.impl.filtering.mapper.FilteringMapper;
import com.airbus.retex.service.media.IMediaService;


@Service
@Transactional(rollbackFor = Exception.class)
public class FilteringServiceImpl implements IFilteringService {

    private static final String RETEX_ERROR_FILTERING_NOT_FOUND = "retex.error.filtering.not.found";

	@Autowired
    private IRetrieveChildRequestService retrieveChildRequestService;

    @Autowired
    private FilteringMapper filteringMapper;

    @Autowired
    private FilteringDtoMapper filteringDtoMapper;

    @Autowired
    private FilteringRepository filteringRepository;

    @Autowired
    private PhysicalPartRepository physicalPartRepository;

    @Autowired
    private PartRepository partRepository;

    @Autowired
    private DrtRepository drtRepository;

    @Autowired
    private ChildRequestRepository childRequestRepository;

    @Autowired
    private FilteringFullDtoMapper filteringFullDtoMapper;

    @Autowired
    AircraftFamilyRepository aircraftFamilyRepository;

    @Autowired
    AircraftTypeRepository aircraftTypeRepository;

    @Autowired
    AircraftVersionRepository aircraftVersionRepository;

    @Autowired
    IMediaService iMediaService;

    @Override
    public PageDto<FilteringDto> findFilteringsWithFilters(FilteringFiltersDto filteringDto){
        Pageable pageable = PageRequest.of(filteringDto.getPage(), filteringDto.getSize(), Sort.by(Sort.Direction.DESC,"id"));
        Page<Filtering> filterings = filteringRepository.findAll(buildSpecification(filteringDto), pageable);
        return new PageDto<>(filteringDtoMapper.convert(filterings.getContent()), filterings.getTotalElements(), filterings.getTotalPages());
    }

    /**
     * @param filtering : SerialNumber, PartNumber
     * @return Specification<Filtering>
     */
    private Specification<Filtering> buildSpecification(FilteringFiltersDto filtering) {
        Specification<Filtering> specification = Specification.where(null);

        if (null != filtering.getPartNumber() && null != filtering.getSerialNumber()) {
            specification = specification.and(FilteringSpecification.filterByPartNumberAndSerialNumber(filtering.getPartNumber(),filtering.getSerialNumber()));
        }

        if (null != filtering.getSerialNumber()) {
            specification = specification.and(FilteringSpecification.filterBySerialNumber(filtering.getSerialNumber()));
        }

        if (null != filtering.getPartNumber()) {
            specification = specification.and(FilteringSpecification.filterByPartNumber(filtering.getPartNumber()));
        }


        return specification;
    }


    @Override
    public FilteringFullDto findFilteringById(Long id, Language language) throws FunctionalException {
        Filtering filtering = filteringRepository.findById(id).orElseThrow(
                () -> new NotFoundException(RETEX_ERROR_FILTERING_NOT_FOUND));
        return filteringFullDtoMapper.convert(filtering, language);
    }

    @Override
    public FilteringDto createFiltering(FilteringCreationDto filteringCreationDto) throws FunctionalException {

        //DEBUG TODO: add service for creating phyparts to check uniqueness of PN/SN and EN

        String pn = filteringCreationDto.getPartNumber();
        String sn = filteringCreationDto.getSerialNumber();
        String en = filteringCreationDto.getEquipmentNumber();

        Filtering item = new Filtering();
        item.setStatus(EnumStatus.CREATED);

        Optional<PhysicalPart> opp = physicalPartRepository.findByEquipmentNumber(en);
        if (opp.isPresent()) {
            PhysicalPart pp = opp.get();
            if (filteringRepository.findByPhysicalPart(pp).isPresent()) {
                // a filtering already exists with this equipment number
                throw new FunctionalException("retex.error.filtering.creation.en.found",
                        pp.getPart().getPartNumber(), pp.getSerialNumber());
            }
        }

        opp = physicalPartRepository.findByPartPartNumberAndSerialNumber(pn, sn);
        if (opp.isPresent()) {
            PhysicalPart pp = opp.get();
            if (filteringRepository.findByPhysicalPart(pp).isPresent()) {
                // a filtering already exists with this physical part
                throw new FunctionalException("retex.error.filtering.creation.pn.sn.found");
            }
            item.setPhysicalPart(pp);
            if (pp.getEquipmentNumber() == null) {
                pp.setEquipmentNumber(en);
                physicalPartRepository.saveAndFlush(pp);
                physicalPartRepository.detach(pp);
            } else if (!pp.getEquipmentNumber().equals(en)) {
                // physical part exists but with another equipment number
                throw new FunctionalException("retex.error.filtering.creation.en.different.for.pn.sn",
                        pp.getEquipmentNumber());
            }
        } else {
            Optional<Part> p = partRepository.findPartByPartNumberAndIsLatestVersionTrue(pn);
            if (!p.isPresent()) {
                // no part with this part number found
                throw new FunctionalException("retex.error.filtering.creation.pn.not.found");
            }

            // create new physical part
            PhysicalPart pp = new PhysicalPart();
            pp.setPart(p.get());
            pp.setSerialNumber(sn);
            pp.setEquipmentNumber(en);

            item.setPhysicalPart(pp);

            physicalPartRepository.saveAndFlush(pp);
            physicalPartRepository.detach(pp);
        }

        filteringRepository.saveAndFlush(item);
        filteringRepository.detach(item);

        item = filteringRepository.getOne(item.getId());
        return filteringDtoMapper.convert(item);
    }

    @Override
    public Long createDrt(final Long filteringId) throws FunctionalException {
        Filtering filtering = filteringRepository.findById(filteringId).orElseThrow(
                () -> new NotFoundException(RETEX_ERROR_FILTERING_NOT_FOUND));
        String pn = filtering.getPhysicalPart().getPart().getPartNumber();
        String sn = filtering.getPhysicalPart().getSerialNumber();
        ChildRequest childRequest = retrieveChildRequestService.getChildRequest(pn, sn).orElseThrow(
                () -> new FunctionalException("retex.error.drt.creation.childrequest.not.found", new String[]{pn, sn})
        );
        Drt drt = new Drt();
        drt.setChildRequest(childRequest);
        drt.setFiltering(filtering);
        drt.setIntegrationDate(LocalDate.now());
        drt.setStatus(EnumStatus.CREATED);
        drt = drtRepository.save(drt);

        filtering.setDrt(drt);
        filtering.setLastModified(LocalDate.now());
        filtering.setStatus(EnumStatus.IN_PROGRESS);
        filteringRepository.save(filtering);

        childRequest.addDrt(drt);
        childRequestRepository.save(childRequest);

        return drt.getId();
    }

    @Override
    public FilteringDto updateFiltering(Long id, FilteringUpdateDto filteringUpdateDto) throws FunctionalException {
        Filtering filtering = filteringRepository.findById(id).orElseThrow(() -> new NotFoundException(RETEX_ERROR_FILTERING_NOT_FOUND));

        if (filtering.getStatus() == EnumStatus.CLOSED) {
            throw new FunctionalException("retex.error.filtering.update.closed");
        }

        validateFilteringUpdateDto(filteringUpdateDto);

        if (filteringUpdateDto.getMedias() != null) {
            Set<Media> medias = iMediaService.getAsPermanentMedias(filteringUpdateDto.getMedias());
            for(Media media:medias) {
                filtering.addMedia(media);
            }
        }

        filteringMapper.update(filteringUpdateDto, filtering);
        if (filtering.getAircraftSerialNumber() != null && Integer.valueOf(filtering.getAircraftSerialNumber()) == 0) {
            filtering.setAircraftSerialNumber(null);
        }
        filtering.setLastModified(LocalDate.now());

        filtering = filteringRepository.save(filtering);
        return filteringDtoMapper.convert(filtering);
    }

    private void validateFilteringUpdateDto(FilteringUpdateDto filteringUpdateDto) throws FunctionalException {
        if (filteringUpdateDto.getAircraftFamily() != null) {
            Long id = filteringUpdateDto.getAircraftFamily().getId();
            if (id != null && id == 0) {
                id = null;
            }
            if (id != null) {
                if (!aircraftFamilyRepository.existsById(id)) {
                    throw new NotFoundException("retex.error.aircraft.family.not.found");
                }
            } else {
                filteringUpdateDto.setAircraftFamily(null);
            }
        }
        if (filteringUpdateDto.getAircraftType() != null) {
            Long id = filteringUpdateDto.getAircraftType().getId();
            if (id != null && id == 0) {
                id = null;
            }
            if (id != null) {
                if (!aircraftTypeRepository.existsById(id)) {
                    throw new NotFoundException("retex.error.aircraft.type.not.found");
                }
            } else {
                filteringUpdateDto.setAircraftType(null);
            }
        }
        if (filteringUpdateDto.getAircraftVersion() != null) {
            Long id = filteringUpdateDto.getAircraftVersion().getId();
            if (id != null && id == 0) {
                id = null;
            }
            if (id != null) {
              if(!aircraftVersionRepository.existsById(id)) {
                    throw new NotFoundException("retex.error.aircraft.version.not.found");
              }
            } else {
                filteringUpdateDto.setAircraftVersion(null);
            }
        }
    }
}
