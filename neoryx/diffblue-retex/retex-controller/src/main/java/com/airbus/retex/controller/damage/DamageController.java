package com.airbus.retex.controller.damage;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
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

import com.airbus.retex.business.dto.damage.DamageCreationDto;
import com.airbus.retex.business.dto.damage.DamageFullDto;
import com.airbus.retex.business.dto.damage.DamageLightDto;
import com.airbus.retex.business.dto.damage.functionality.FunctionalityDamageCreationDto;
import com.airbus.retex.business.dto.damage.functionality.FunctionalityDamageDto;
import com.airbus.retex.business.dto.damage.functionality.FunctionalityDamageUpdateDto;
import com.airbus.retex.business.dto.media.MediaDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.configuration.CustomLocalResolver;
import com.airbus.retex.controller.VersionsController;
import com.airbus.retex.model.common.EnumActiveState;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.service.damage.IDamageService;
import com.airbus.retex.service.impl.damage.DamageServiceImpl;
import com.airbus.retex.utils.ConstantUrl;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Damage api", tags = {"Damage api"})
@RestController
@RequestMapping(ConstantUrl.API_DAMAGE_LIBRARY)
public class DamageController extends VersionsController<DamageServiceImpl> {
    @Autowired
    private IDamageService damageService;
    @Autowired
    private CustomLocalResolver customLocalResolver;
    @Autowired
    private HttpServletRequest httpServletRequest;


    @ApiOperation("Create new Damage")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Damage was created successfully"),
                    @ApiResponse(code = 400, message = "the request body does not match the expected one")
            })
    @PostMapping("")
    @Secured("ROLE_DAMAGE:WRITE")
    public ResponseEntity<DamageFullDto> createDamage(@RequestBody @Valid DamageCreationDto damageCreationDto) throws Exception {
        Locale locale = customLocalResolver.resolveLocale(httpServletRequest);
        return ResponseEntity.ok().headers(new HttpHeaders()).body(damageService.createDamage(damageCreationDto, Language.languageFor(locale)));
    }
    @ApiOperation("Add image to a damage")
    @PostMapping(ConstantUrl.API_ADD_DAMAGE_IMAGE)
    @Secured("ROLE_DAMAGE:WRITE")
    public ResponseEntity<MediaDto> addDamageImage(@PathVariable Long id,
                                                   @RequestParam("file") MultipartFile file) throws FunctionalException {
        MediaDto result = damageService.addDamageImage(id, file);

        return ResponseEntity.ok().headers(new HttpHeaders()).body(result);
    }
    @ApiOperation("Update a Damage")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Damage was updated successfully"),
                    @ApiResponse(code = 400, message = "the request body does not match the expected one")
            })
    @PutMapping(ConstantUrl.API_ADD_UPDATE_DAMAGED_DAMAGE)
    @Secured("ROLE_DAMAGE:WRITE")
    public ResponseEntity<DamageFullDto> updateDamage(@RequestBody @Valid DamageCreationDto damageCreationDto,
                                                      @PathVariable Long id) throws Exception {
        return ResponseEntity.ok().headers(new HttpHeaders()).body(damageService.updateDamage(damageCreationDto, id));
    }
    @ApiOperation("Get one damage by id")
    @GetMapping("/{id}")
    @Secured("ROLE_DAMAGE:READ")
    public ResponseEntity<DamageFullDto> getDamage(Authentication authentication,
                                                   @PathVariable Long id,
                                                   @RequestParam(value = "version", required = false) Long version) throws NotFoundException {
        DamageFullDto damage = damageService.getDamage(id, version);
        return ResponseEntity.ok().headers(new HttpHeaders()).body(damage);
    }
    @ApiOperation("Ge all light damages")
    @GetMapping("")
    @Secured("ROLE_DAMAGE:READ")
    public ResponseEntity<List<DamageLightDto>> getAllDamages(Authentication authentication, @RequestParam(name = "statusFilter") EnumActiveState state) {
        Locale locale = customLocalResolver.resolveLocale(httpServletRequest);
        return ResponseEntity.ok().headers(new HttpHeaders()).body(damageService.getAllDamages(Language.languageFor(locale), state));
    }
    @ApiOperation("Revoke a damage")
    @DeleteMapping("/{id}")
    @Secured("ROLE_DAMAGE:WRITE")
    public ResponseEntity<Void> revokeDamage(Authentication authentication,
                                                @ApiParam(value = "id of damage", required = true) @PathVariable Long id) throws FunctionalException {
        damageService.deleteDamage(id);
        return ResponseEntity.noContent().build();

    }
    @ApiOperation("Delete a damaged functionality of damage")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Functionality damage deleted successfully"),
            @ApiResponse(code = 400, message = "The request body does not match the expected one")
    })
    @DeleteMapping(ConstantUrl.API_FUNCTIONALITY_DAMAGES)
    @Secured("ROLE_DAMAGE:WRITE")
    public ResponseEntity<Boolean> deleteDamagedFunctionalityOfDamage(@PathVariable Long id,
                                                                      @PathVariable Long functionalityID) throws FunctionalException {
        int deleted = damageService.deleteDamagedFunctionalityOfDamage(id, functionalityID);
        return ResponseEntity.ok().headers(new HttpHeaders()).body(deleted > 0);
    }
    @Deprecated(since = "4.2", forRemoval = true)
    @ApiOperation("Get list of functionalityDamage associated to given damage and functionality")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of FunctionalityDamage"),
            @ApiResponse(code = 400, message = "The request body does not match the expected one")
    })
    @GetMapping(ConstantUrl.API_FUNCTIONALITY_DAMAGES)
    @Secured("ROLE_DAMAGE:READ")
    public ResponseEntity<List<FunctionalityDamageDto>> getFunctionalityDamages(@PathVariable Long id,
                                                                                @PathVariable Long functionalityID,
                                                                                @RequestParam(value = "version", required = false) Long version) throws NotFoundException {
        return ResponseEntity.ok().headers(new HttpHeaders()).body(damageService.getFunctionalityDamages(id, functionalityID,  version));
    }
    @ApiOperation("Revoke (deleteVersion) a given FunctionalityDamage of damage")
    @DeleteMapping(ConstantUrl.API_REVOKE_FUNCTIONALITYDAMGE_OF_DAMAGE)
    @Secured("ROLE_DAMAGE:WRITE")
    public ResponseEntity<Void> revokeFunctionalityDamageOfDamage(@PathVariable Long id,
                                                                     @PathVariable Long functionalityDamageID) throws FunctionalException {
        damageService.deleteFunctionalityDamageOfDamage(id, functionalityDamageID);
        return ResponseEntity.noContent().build();
    }
    @ApiOperation("Add functionality damage to damage")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "FunctionalityDamage added successfully"),
            @ApiResponse(code = 400, message = "The request body does not match the expected one")
    })
    @PostMapping(ConstantUrl.API_FUNCTIONALITY_DAMAGES)
    @Secured("ROLE_DAMAGE:WRITE")
    public ResponseEntity<Void> addFunctionalityDamageToDamage(@PathVariable Long id,
                                                               @PathVariable Long functionalityID,
                                                               @RequestBody FunctionalityDamageCreationDto functionalityDamageDto) throws FunctionalException {
        damageService.addFunctionalityDamageToDamage(id, functionalityID, functionalityDamageDto);
        return ResponseEntity.noContent().build();
    }
    @ApiOperation("Add Image to FunctionalityDamage")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Image added successfully to FunctionalityDamage"),
                    @ApiResponse(code = 400, message = "the request body does not match the expected one")
            })
    @PostMapping(ConstantUrl.API_ADD_FUNCTIONALITY_DAMAGE_IMAGE)
    @Secured("ROLE_DAMAGE:WRITE")
    public ResponseEntity<MediaDto> addFunctionalityDamageImage(@PathVariable Long id,
                                                                @PathVariable("functionalityDamageID") Long functionalityDamageID,
                                                                @RequestParam("file") MultipartFile file) throws FunctionalException {
        return ResponseEntity.ok().headers(new HttpHeaders()).body(damageService.addFunctionalityDamageImage(id, functionalityDamageID, file));
    }
    @ApiOperation("Update functionality damage of damage")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The FunctionalityDamage updated successfully"),
            @ApiResponse(code = 400, message = "The request body does not match the expected one")
    })
    @PutMapping(ConstantUrl.API_UPDATE_FUNCTIONALITY_DAMAGE)
    @Secured("ROLE_DAMAGE:WRITE")
    public ResponseEntity<DamageFullDto> updateFunctionalityDamageOfDamage(@PathVariable Long id,
                                                                  @PathVariable Long functionalityDamageID,
                                                                  @RequestBody FunctionalityDamageUpdateDto functionalityDamageDto) throws FunctionalException {
        return ResponseEntity.ok(damageService.updateFunctionalityDamageOfDamage(id, functionalityDamageID, functionalityDamageDto));

    }
    @ApiOperation("Delete image of entity")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "File deleted"),
                    @ApiResponse(code = 400, message = "The request body does not match the expected one")
            })
    @DeleteMapping(ConstantUrl.API_DELETE_DAMAGE_MEDIA)
    @Secured("ROLE_DAMAGE:WRITE")
    public ResponseEntity<Boolean> deleteImage(@PathVariable Long id,
                                      @RequestParam("uuid") String uuid) throws FunctionalException {
        return ResponseEntity.ok().body(damageService.deleteImage(id, uuid));
    }
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "The processing status of damage updated successfully"),
            @ApiResponse(code = 400, message = "The request body does not match the expected one")
    })
    @ApiOperation("Update damage status")
    @PutMapping(ConstantUrl.API_UPDATE_DAMAGE_STATUS)
    @Secured("ROLE_DAMAGE:WRITE")
    public ResponseEntity<DamageFullDto> updateDamageStatus(Authentication authentication,
                                                            @PathVariable Long id,
                                                            @RequestParam(name = "validate", defaultValue = "false") boolean validate) throws FunctionalException {

        return ResponseEntity.ok().body(damageService.updateStatus(id, validate));
}

}
