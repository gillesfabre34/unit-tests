package com.airbus.retex.controller.part;

import java.util.List;

import com.airbus.retex.utils.ValidList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.controller.VersionsController;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.service.impl.part.PartServiceImpl;
import com.airbus.retex.service.part.IPartService;
import com.airbus.retex.utils.ConstantUrl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Api(value = "Part", tags = {"Parts"})
@RequestMapping(ConstantUrl.API_PARTS)
@RestController
public class PartController extends VersionsController<PartServiceImpl> {

    @Autowired
    private IPartService partService;
    @Autowired
    private DtoConverter dtoConverter;

    @ApiOperation("Get list of parts")
    @GetMapping("")
    @Secured("ROLE_PART_MAPPING:READ")
    public ResponseEntity<PageDto<PartDto>> getAllParts(
            Authentication authentication,
            @Valid PartFilteringDto partFiltering,
            @RequestParam(value = "status", required = false, defaultValue = "") EnumStatus status) {
        return ResponseEntity.ok().body(partService.findParts(partFiltering, status));
    }
    @ApiOperation("Create a part")
    @PostMapping("")
    @Secured("ROLE_PART_MAPPING:WRITE")
    public ResponseEntity<PartFullDto> createPart(Authentication authentication,
                                                  @RequestBody @Valid PartCreationDto creationDto) throws FunctionalException {
        PartFullDto part = dtoConverter.toDto(partService.createPart(creationDto), PartFullDto.class);
        return ResponseEntity.ok(part);
    }
    @ApiOperation("Duplicate a part")
    @PostMapping(ConstantUrl.API_DUPLICATE_PART)
    @Secured("ROLE_PART_MAPPING:WRITE")
    public ResponseEntity<PartFullDto> createDuplicatePart(@PathVariable(value = "id") Long oldPartId,
                                                           @RequestBody @Valid PartCreationDto creationDto) throws FunctionalException {
        return ResponseEntity.ok(partService.duplicatePart(creationDto, oldPartId));
    }
    @ApiOperation("Delete a part")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Part deleted successfully"),
            @ApiResponse(code = 400, message = "The request body does not match the expected one")
    })
    @DeleteMapping(ConstantUrl.API_DELETE_PART)
    @Secured("ROLE_PART_MAPPING:WRITE")
    public ResponseEntity<Void> deletePart(Authentication authentication, @PathVariable Long id) throws FunctionalException {
        partService.deletePart(id);
        return ResponseEntity.noContent().build();
    }
    @ApiOperation("Update a part")
    @PutMapping(ConstantUrl.API_PARTS_UPDATE)
    @Secured("ROLE_PART_MAPPING:WRITE")
    public ResponseEntity updatePart(Authentication authentication,
                                     @PathVariable Long id,
                                     @RequestBody @Valid PartUpdateHeaderDto creationDto) throws FunctionalException {
        return partService.updatePart(id, creationDto);
    }
    @ApiOperation("Get a part")
    @GetMapping(ConstantUrl.API_PART)
    @Secured({"ROLE_PART_MAPPING:READ"})
    public ResponseEntity<PartFullDto> getPart(Authentication authentication,
                                               @PathVariable Long id,
                                               @RequestParam(value = "version", required = false) Long version) throws NotFoundException {

        return partService.findById(id, version).map(
                ResponseEntity::ok
        ).orElse(ResponseEntity.notFound().build());
    }
    @ApiOperation("Get a part that will duplicate")
    @GetMapping(ConstantUrl.API_DUPLICATE_PART)
    @Secured({"ROLE_PART_MAPPING:READ"})
    public ResponseEntity<PartDuplicateDto> getDuplicatePart(Authentication authentication,
                                                             @PathVariable(value = "id") Long partId) throws NotFoundException {

        return partService.getDuplicatePart(partId).map(
                ResponseEntity::ok
        ).orElse(ResponseEntity.notFound().build());

    }

    @ApiOperation("Get part numbers by search value where status is VALIDATED")
    @GetMapping(ConstantUrl.API_PART_NUMBERS)
    @Secured({"ROLE_PART_MAPPING:READ"})
    public ResponseEntity<List<PartNumberDto>> getPartNumbersBySearchValue(
            @RequestParam(value = "search", defaultValue = "") String search,
            @RequestParam(value = "isNotLinkedToRouting", defaultValue = "false") boolean isNotLinkedToRouting,
            @RequestParam(value = "status", defaultValue = "VALIDATED") EnumStatus status) {
        return ResponseEntity.ok(partService.getPartNumbersBySearchValue(search, isNotLinkedToRouting, status));
    }

    @ApiOperation("Get part numbers root by search value where status is VALIDATED")
    @GetMapping(ConstantUrl.API_PART_NUMBERS_ROOT)
    @Secured({"ROLE_PART_MAPPING:READ"})
    public ResponseEntity<List<String>> getPartNumbersRootBySearchValue(
            @RequestParam(value = "search", defaultValue = "") String searchValue,
            @RequestParam(value = "status", defaultValue = "VALIDATED") EnumStatus status) {
        return ResponseEntity.ok(partService.getPartNumbersRootBySearchValue(searchValue, status));
    }

    /**
     * Add image to part
     *
     * @param id
     * @param file
     * @return
     */
    @ApiOperation("Add image to a part")
    @PostMapping(ConstantUrl.API_ADD_PART_MAPPING_IMAGE)
    @Secured({"ROLE_PART_MAPPING:WRITE"})
    public ResponseEntity<MediaDto> addPartImage(@PathVariable("id") Long id, @RequestParam("file") MultipartFile file) throws FunctionalException {
        MediaDto result = partService.addPartImage(id, file);
        return ResponseEntity.ok().body(result);
    }


    @ApiOperation("Create or Update functional area")
    @PostMapping(ConstantUrl.API_PART_ADD_FUNCTIONAL_AREAS)
    @Secured("ROLE_PART_MAPPING:WRITE")
    public ResponseEntity<List<FunctionalAreaCreateOrUpdateDto>> createFunctionalArea(Authentication authentication,
                                                                                      @PathVariable("id") @NotNull Long partNaturalId,
                                                                                      @RequestParam(name = "validate", required = false, defaultValue = "false") boolean validate,
                                                                                      @RequestBody @Valid ValidList<FunctionalAreaCreateOrUpdateDto> creationDtoList) throws FunctionalException {
        PartCreateUpdateFunctionalAreaDto partCreateUpdateFunctionalAreaDto = new PartCreateUpdateFunctionalAreaDto();
        partCreateUpdateFunctionalAreaDto.setFunctionalAreas(creationDtoList);

        if (!partService.createOrUpdateFunctionalityAreas(partNaturalId, validate, partCreateUpdateFunctionalAreaDto).isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
