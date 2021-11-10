package com.airbus.retex.service.impl.part;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.functionalArea.FunctionalAreaCreateOrUpdateDto;
import com.airbus.retex.business.dto.media.MediaDto;
import com.airbus.retex.business.dto.part.PartCreateUpdateFunctionalAreaDto;
import com.airbus.retex.business.dto.part.PartCreationDto;
import com.airbus.retex.business.dto.part.PartDto;
import com.airbus.retex.business.dto.part.PartDuplicateDto;
import com.airbus.retex.business.dto.part.PartFilteringDto;
import com.airbus.retex.business.dto.part.PartFullDto;
import com.airbus.retex.business.dto.part.PartNumberDto;
import com.airbus.retex.business.dto.part.PartUpdateHeaderDto;
import com.airbus.retex.business.dto.versioning.VersionDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.config.RetexConfig;
import com.airbus.retex.model.basic.CloningContext;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.media.Media;
import com.airbus.retex.model.part.Mpn;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.part.PartCloner;
import com.airbus.retex.model.part.specification.PartSpecification;
import com.airbus.retex.persistence.functionalArea.FunctionalAreaRepository;
import com.airbus.retex.persistence.part.PartRepository;
import com.airbus.retex.persistence.routing.RoutingRepository;
import com.airbus.retex.service.impl.part.mapper.PartMapper;
import com.airbus.retex.service.impl.versionable.AbstractVersionableService;
import com.airbus.retex.service.media.IMediaService;
import com.airbus.retex.service.part.IPartService;

import liquibase.util.file.FilenameUtils;

@Service
@Transactional(rollbackFor = Exception.class)
public class PartServiceImpl extends AbstractVersionableService<Part, Long, PartUpdateHeaderDto> implements IPartService {
    private static final String EXTENSION_FORMAT_NOT_ACCEPTED = "retex.error.media.format.denied";

    @Autowired
    private PartRepository partRepository;
    @Autowired
    private PartCloner partCloner;
    @Autowired
    private PartMapper partMapper;
    @Autowired
    private DtoConverter dtoConverter;
    @Autowired
    private FunctionalAreaRepository functionalAreaRepository;
    @Autowired
    private IMediaService mediaService;
    @Autowired
    private RoutingRepository routingRepository;
    @Autowired
    private RetexConfig retexConfig;

    @Override
    public PageDto<PartDto> findParts(PartFilteringDto filtering, EnumStatus status) {
        Pageable pageable = PageRequest.of(filtering.getPage(), filtering.getSize(), Sort.by("naturalId"));
        Page<Part> parts;
        if(null != status) {
            parts = partRepository.findAll(PartSpecification.filterByStatus(status), pageable);
        } else {
            parts = partRepository.findAllLastVersions(buildSpecification(filtering), pageable);
        }

        return new PageDto<>(partMapper.createPartDtoList(parts.getContent()), parts.getTotalElements(), parts.getTotalPages());
    }

    /**
     * @param filtering : AtaCode, PartNumber, DesignationId
     * @return Specification<Part>
     */
    private Specification<Part> buildSpecification(PartFilteringDto filtering) {
        Specification<Part> specification = Specification.where(null);

        if (null != filtering.getAtaCode()) {
            specification = specification.and(PartSpecification.filterByAtaCode(filtering.getAtaCode()));
        }

        if (null != filtering.getPartNumber()) {
            specification = specification.and(PartSpecification.filterByPartNumber(filtering.getPartNumber()));
        }

        if (null != filtering.getDesignationId()) {
            specification = specification.and(PartSpecification.filterByDesignation(filtering.getDesignationId()));
        }

        return specification;
    }

    /**
     * Add a part
     *
     * @param partCreationDto
     * @return Part
     * @throws FunctionalException
     */
    @Override
    public Part createPart(PartCreationDto partCreationDto) throws FunctionalException {
        partCreationDto.setStatus(EnumStatus.CREATED);

        Part part = new Part();
        partMapper.updatePart(partCreationDto, part, new CloningContext());

        //Creation d'une nouvelle Part Verification pas de partNumber identique
        if ((part.getPartNumber() != null) && (!part.getPartNumber().isEmpty()) && partRepository.findPartByPartNumberAndIsLatestVersionTrue(part.getPartNumber()).isPresent()) {
            throw new FunctionalException("retex.part.number.existing");
        }

        if (part.getPartNumber() != null && !part.getPartNumber().isEmpty() && part.getPartNumber().equals(part.getPartNumberRoot())) {
            throw new FunctionalException("retex.part.number.equals.part.number.root");
        }

        checkValidePart(part, partCreationDto.getMpnCodes());

        createVersion(part);
        return part;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deletePart(Long id) throws FunctionalException {
        Part part = findPartById(id, null);
        if (routingRepository.existsByPartTechnicalId(part.getTechnicalId())) {
            throw new FunctionalException("retex.part.delete.link.routing");
        }
        deleteVersion(part.getNaturalId());
    }

    /**
     * Update Part
     *
     * @param naturalId
     * @param partUpdatedDto:
     * @return ResponseEntity
     * @throws FunctionalException
     */
    @Override
    public ResponseEntity updatePart(Long naturalId, PartUpdateHeaderDto partUpdatedDto) throws FunctionalException {
        if (partUpdatedDto.getAtaCode().isEmpty()) {
            partUpdatedDto.setAtaCode(null);
        }

        partUpdatedDto.setStatus(EnumStatus.CREATED);
        Part part = findPartById(naturalId, null);

        checkValideMpn(partUpdatedDto.getMpnCodes(), part);

		updateVersion(part.getNaturalId(), part1 -> partMapper.updatePart(partUpdatedDto, part1, new CloningContext()));

        return ResponseEntity.noContent().build();
    }

    @Override
    public  List<FunctionalAreaCreateOrUpdateDto> createOrUpdateFunctionalityAreas(Long partNaturalId, boolean validate, PartCreateUpdateFunctionalAreaDto partCreateUpdateFunctionalAreaDto) throws FunctionalException {
        partCreateUpdateFunctionalAreaDto.setStatus(validate ? EnumStatus.VALIDATED : EnumStatus.CREATED);

        Part oldPart = findPartById(partNaturalId, null);

        if(null != oldPart.getAta()) {
            partCreateUpdateFunctionalAreaDto.setAtaCode(oldPart.getAta().getCode());
        }
        partCreateUpdateFunctionalAreaDto.setMpnCodes(oldPart.getMpnCodes() != null ? oldPart.getMpnCodes().stream().map(Mpn::getCode).collect(Collectors.toList()) : null);
        partCreateUpdateFunctionalAreaDto.setPartDesignationId(oldPart.getPartDesignation().getId());
        checkFunctionalArea(partCreateUpdateFunctionalAreaDto);


		Part part = updateVersion(partNaturalId,
				part1 -> partMapper.updatePart(partCreateUpdateFunctionalAreaDto, part1, new CloningContext()));
        return dtoConverter.toDtos(part.getFunctionalAreas(), FunctionalAreaCreateOrUpdateDto.class);
    }

    /**
     * Find Part by id
     *
     * @param id
     * @return
     */
    @Override
    public Optional<PartFullDto> findById(Long id, Long version) throws NotFoundException {
        Part part = findPartById(id, version);
        PartFullDto partFullDto = dtoConverter.toDto(part, PartFullDto.class);
        if (null != part.getMedia()) {
            partFullDto.setImage(new MediaDto(part.getMedia().getFilename(), part.getMedia().getUuid()));
        }
        return Optional.of(partFullDto);
    }

    /**
     * Find Part by id
     *
     * @param id
     * @return
     */
    @Override
    public Part findPartById(Long id, Long version) throws NotFoundException {
        Optional<Part> partOpt = partRepository.findByNaturalIdAndVersion(id, version);
        if (partOpt.isPresent()) {
            return partOpt.get();
        }
        throw new NotFoundException("retex.part.notExists");
    }

    @Override
    public Optional<PartDuplicateDto> getDuplicatePart(Long partId) throws NotFoundException {
        Part part = findPartById(partId, null);
        return Optional.of(dtoConverter.toDto(part, PartDuplicateDto.class));
    }

    @Override
    public PartFullDto duplicatePart(PartCreationDto creationDto, Long oldPartId) throws FunctionalException {
        Part newPart = this.createPart(creationDto);
        duplicateFunctionalAreaMapping(oldPartId, newPart);

        return dtoConverter.toDto(newPart, PartFullDto.class);
    }

    /**
     * Duplicate Part Mapping with oldPartId
     * @param oldPartId
     * @param newPart
     */
    private void duplicateFunctionalAreaMapping(Long oldPartId, Part newPart) throws FunctionalException {
        Part oldValidatedPart = partRepository.findValidatedVersionByNaturalId(oldPartId)
                .orElseThrow(() -> new NotFoundException("retex.part.notExists"));

        List<FunctionalArea> functionalAreas = functionalAreaRepository.findByPart(oldValidatedPart);

        updateVersion(newPart.getNaturalId(), partToUpdate -> {
            partToUpdate.setMedia(oldValidatedPart.getMedia());
            functionalAreas.stream().forEach(functionalArea -> {
                FunctionalArea fa = new FunctionalArea();
                fa.setPart(partToUpdate);
                fa.setClassification(functionalArea.getClassification());
                fa.setAreaNumber(functionalArea.getAreaNumber());
                fa.setFunctionality(functionalArea.getFunctionality());
                fa.setFunctionalAreaName(functionalArea.getFunctionalAreaName());
                fa.setMaterial(functionalArea.getMaterial());
                fa.setTreatment(functionalArea.getTreatment());
                fa.setDisabled(functionalArea.isDisabled());
                partToUpdate.addFunctionalAreas(fa);
            });
        });
    }

    @Override
    public List<PartNumberDto> getPartNumbersBySearchValue(String searchValue, boolean isNotLinkedToRouting, EnumStatus status) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("partNumber"));
        Page<Part> parts;
        if (isNotLinkedToRouting) {
            parts = partRepository.findByPartNumberStartingWithAndFunctionalAreasIsNotNullNotInRouting(searchValue, pageable, status);
        } else {
            parts = partRepository.findByPartNumberStartingWithAndFunctionalAreasIsNotNull(searchValue, pageable, status);
        }
        return dtoConverter.toDtos(parts.getContent(), PartNumberDto.class);
    }

    @Override
    public List<String> getPartNumbersRootBySearchValue(String searchValue, EnumStatus status) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("partNumberRoot"));
        Page<String> parts = partRepository.findByPartNumberRootStartingWith(searchValue, pageable, status);
        return parts.getContent();
    }

    /**
     * Add Part Image
     *
     * @param partID
     * @param file
     * @return
     * @throws Exception
     */
    @Override
	public MediaDto addPartImage(Long partID, MultipartFile file) throws FunctionalException {
        String formatName = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!Arrays.asList(retexConfig.getPartAcceptedMediaFormats()).contains(formatName)) {
            throw new FunctionalException(EXTENSION_FORMAT_NOT_ACCEPTED);
        }
        Part part = findPartById(partID, null);

        Media media = mediaService.writeStream(file.getOriginalFilename(), file);

        part = updateVersion(part.getNaturalId(), partToUpdate -> {
            partToUpdate.setStatus(EnumStatus.CREATED);
            partToUpdate.setMedia(media);
        });

        return new MediaDto(part.getMedia().getFilename(), part.getMedia().getUuid());

    }

    @Override
    protected void mapDtoToVersion(PartUpdateHeaderDto updateDto, Part version) throws FunctionalException {
        if (updateDto instanceof PartCreateUpdateFunctionalAreaDto){
            partMapper.updatePart((PartCreateUpdateFunctionalAreaDto) updateDto, version, new CloningContext());
        }
        partMapper.updatePart(updateDto, version, new CloningContext());
    }

    private void checkFunctionalArea(PartCreateUpdateFunctionalAreaDto partCreateUpdateFunctionalAreaDto) throws FunctionalException {
        if(null != partCreateUpdateFunctionalAreaDto.getFunctionalAreas()){
            partCreateUpdateFunctionalAreaDto.getFunctionalAreas()
                    .sort(Comparator.comparing(FunctionalAreaCreateOrUpdateDto::getAreaNumber));
            String lastAreaNumber = null;
            for (FunctionalAreaCreateOrUpdateDto functionalArea: partCreateUpdateFunctionalAreaDto.getFunctionalAreas()) {
                if(functionalArea.getAreaNumber().equals(lastAreaNumber)){
                    throw new FunctionalException("retex.functional.area.number.already.exists");
                }
                lastAreaNumber = functionalArea.getAreaNumber();
            }
        }
    }

    private void checkValidePart(Part part, List<String> mpnCodes) throws FunctionalException {
        if ((part.getPartNumber() == null) || (part.getPartNumber().isEmpty())) {
            if (((part.getPartNumberRoot() == null) || (part.getPartNumberRoot().isEmpty()))) {
                throw new FunctionalException("retex.part.numbers.notNull");
            } else if (partRepository.findPartByPartNumberAndPartNumberRootAndIsLatestVersionTrue(part.getPartNumber(), part.getPartNumberRoot()).isPresent()) {
                throw new FunctionalException("retex.part.number.existing");
            }
        } else if (mpnCodes.isEmpty()) {
            throw new FunctionalException("retex.part.mpn.mandatory");
        }
    }

    private void checkValideMpn(List<String> mpnCodes, Part version) throws FunctionalException {
        if (mpnCodes == null || mpnCodes.isEmpty()) {
            if (version.getPartNumberRoot().isEmpty() ||
                    (version.getPartNumberRoot() != null && version.getPartNumber() != null &&
                            !version.getPartNumberRoot().isEmpty() && !version.getPartNumber().isEmpty())) {
                throw new FunctionalException("retex.part.mpn.mandatory");
            }
        }
    }

    @Override
    protected Part cloneVersion(Part version) {
        return partCloner.clonePart(version, new CloningContext());
    }

    @Override
    public List<VersionDto> findAllVersionsByNaturalId(Long naturalId) {
        List<Part> partList = partRepository.findAllVersionsByNaturalId(naturalId);
        return  dtoConverter.toDtos(partList, VersionDto.class);
    }
}
