package com.airbus.retex.service.damage;

import java.util.List;

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
import com.airbus.retex.model.common.EnumActiveState;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.damage.Damage;

public interface IDamageService {

    // ---------------------------------------------------------------------------------------
    // ----------------------------------- DAMAGE --------------------------------------------
    // ---------------------------------------------------------------------------------------

    /**
     * find a damages
     *
     * @param naturalId
     * @param version
     * @return Damage
     */
    Damage findByNaturalIdAndVersion(Long naturalId, Long version) throws NotFoundException;

    /**
     * Return a created damage
     * @param creationDto
     * @param language
     * @return DamageFullDto
     * @throws Exception
     */
    DamageFullDto createDamage(DamageCreationDto creationDto, Language language) throws Exception;

    /**
     * Add damage image
     * @param damageID
     * @param file
     * @return MediaDto
     * @throws FunctionalException
     */
    MediaDto addDamageImage(Long damageID, MultipartFile file) throws FunctionalException;

    /**
     * Update damage by id
     * @param creationDto
     * @param id
     * @return DamageFullDto
     * @throws FunctionalException
     */
    DamageFullDto updateDamage(DamageCreationDto creationDto, Long id) throws FunctionalException;

    /**
     * Get damage by ID and fields translated according to given locale language
     * @param id
     * @return DamageFullDto
     */
    DamageFullDto getDamage(Long id, Long version) throws NotFoundException;

    /**
     * Get all damages according to given locale language
     * @param locale
     * @param state
     * @return List<DamageLightDto>
     */
    List<DamageLightDto> getAllDamages(Language locale, EnumActiveState state);

    /**
     * delete a given damage (by id)
     * @param id
     */
    void deleteDamage(Long id) throws FunctionalException;

    // ---------------------------------------------------------------------------------------
    // ----------------------------------- FUNCTIONALITY DAMAGE ------------------------------
    // ---------------------------------------------------------------------------------------

    /**
     * Delete damaged (affected) functionality of damage
     * @param damageID
     * @param functionalityID
     * @return int
     */
    int deleteDamagedFunctionalityOfDamage(Long damageID, Long functionalityID) throws FunctionalException;

    /**
     * Get list of functionalit_damages according to given damageID and functionalityID
     * @param damageID
     * @param functionalityID
     * @return List<FunctionalityDamageDto>
     */
    List<FunctionalityDamageDto> getFunctionalityDamages(Long damageID, Long functionalityID,  Long version) throws NotFoundException;

    /**
     * Revoke (deleteVersion) a given FunctionalityDamage of damage
     * @param functionalityDamageID
     */
    void deleteFunctionalityDamageOfDamage(Long damageID, Long functionalityDamageID) throws FunctionalException;

    /**
     * Add a functionality Damage to damage
     * @param damageID naturalID
     * @param functionalityID
     * @param functionalityDamageDto
     */
    void addFunctionalityDamageToDamage(Long damageID,  Long functionalityID, FunctionalityDamageCreationDto functionalityDamageDto) throws FunctionalException;

    /**
     * Add image to the FunctionalityDamage
     * @param functionalityDamageID
     * @param file
     * @return MediaDto
     * @throws FunctionalException
     */
    MediaDto addFunctionalityDamageImage(Long damageID, Long functionalityDamageID, MultipartFile file) throws FunctionalException;

    /**
     * Update functionalityDamage of a damage
     * @param functionalityDamageID functionalityDamageID naturalId
     * @param functionalityDamageDto translatedField to Update
     */
    DamageFullDto updateFunctionalityDamageOfDamage(Long damageID, Long functionalityDamageID, FunctionalityDamageUpdateDto functionalityDamageDto) throws FunctionalException;

    /**
     * Delete image of damage
     * @param damageNaturalId damage naturalId
     * @param uuidValue id image
     */
    boolean deleteImage(Long damageNaturalId, String uuidValue) throws FunctionalException;


    DamageFullDto updateStatus(Long damageID, boolean validate) throws FunctionalException;
}
