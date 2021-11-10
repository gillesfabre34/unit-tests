package com.airbus.retex.service.part;

import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.functionalArea.FunctionalAreaCreateOrUpdateDto;
import com.airbus.retex.business.dto.media.MediaDto;
import com.airbus.retex.business.dto.part.*;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.model.common.EnumStatus;
import org.springframework.http.ResponseEntity;
import com.airbus.retex.model.part.Part;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


public interface IPartService {
    /**
     * get parts matching filters
     * @param filtering
     * @return PageDto<PartDto>
     */
    PageDto<PartDto> findParts(PartFilteringDto filtering, EnumStatus status);

    /**
     * create a new part
     *
     * @param creationDto - datas of part to register
     * @return PartFullDto
     * @throws FunctionalException
     */
    Part createPart(PartCreationDto creationDto) throws FunctionalException;


    /**
     * deleteVersion a part
     * @param id
     */
    void deletePart(Long id) throws FunctionalException;

    /**
     * Update a part
     *
     * @param id
     * @param partUpdateDto - data of part to update
     * @return ResponseEntity
     */
    ResponseEntity updatePart(Long id, PartUpdateHeaderDto partUpdateDto) throws FunctionalException;

    /**
     * Find Part in the repository
     *
     * @param id
     * @return Optional<PartFullDto>
     */
    Optional<PartFullDto> findById(Long id, Long version) throws NotFoundException;

    /**
     * Find Part in the repository
     *
     * @param id
     * @return Optional<PartFullDto>
     */
    Part findPartById(Long id, Long version) throws NotFoundException;


    /**
     * Add Part Image
     * @param partID
     * @param file
     * @return MediaDto
     * @throws FunctionalException
     */
    MediaDto addPartImage(Long partID, MultipartFile file) throws FunctionalException;

    /**
     * Find Part in the repository for duplicate it
     *
     * @param partId
     * @return Optional<PartDuplicateDto>
     */
    Optional<PartDuplicateDto> getDuplicatePart(Long partId) throws NotFoundException;

    /**
     * Duplicate a part and add corresponding part mapping
     * @param creationDto
     * @param oldPartId
     * @return PartFullDto
     */
    PartFullDto duplicatePart(PartCreationDto creationDto, Long oldPartId) throws FunctionalException;

    /**
     * Return list of PartNumber by a search value
     * @param searchValue
     * @return List<PartNumberDto>
     */
    List<PartNumberDto> getPartNumbersBySearchValue(String searchValue, boolean isNotLinkedToRouting, EnumStatus status);

    /**
     * Return list of PartNumberRoot by a search value
     * @param searchValue
     * @return List<String>
     */
    List<String> getPartNumbersRootBySearchValue(String searchValue, EnumStatus status);

    /**
     * Return update functional Area of a part
     * @param partNaturalId
     * @param validate
     * @param partCreateUpdateFunctionalAreaDto
     * @return List<FunctionalAreaCreateOrUpdateDto>
     */
    List<FunctionalAreaCreateOrUpdateDto> createOrUpdateFunctionalityAreas(Long partNaturalId, boolean validate, PartCreateUpdateFunctionalAreaDto partCreateUpdateFunctionalAreaDto) throws FunctionalException;

}
