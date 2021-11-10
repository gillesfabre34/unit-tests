package com.airbus.retex.service.admin;

import com.airbus.retex.business.dto.role.RoleCreationUpdateDto;
import com.airbus.retex.business.dto.role.RoleDto;
import com.airbus.retex.business.dto.role.RoleFullDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.TechnicalError;
import com.airbus.retex.model.common.Language;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface IRoleService {

    List<RoleDto> getAllRolesByAirbusEntity(Long airbusEntityId);

    /**
     * @param airbusEntityId
     * @param locale
     * @return list of roles that match the language language also the airbusEntity
     */
    List<RoleDto> getAllRolesByAirbusEntityAndLanguage(Long airbusEntityId, Locale locale);

    /**
     * @param locale
     * @return list of roles for all airbus entities
     */
    List<RoleDto> getAllRolesWithAllAirbusEntities(Locale locale);

    /**
     * @param roleId
     * @return boolean if the role has been updated
     */
    boolean revokeRole(Long roleId);

    /**
     * @param roleId
     * @param airbusEntityId
     * @return boolean indicates if the role has been updated
     */
    boolean revokeRole(Long roleId, Long airbusEntityId);

    /**
     * @param roleCreationDto
     * @return Light Role Dto with the important informations of the role being created
     * @throws TechnicalError
     */
    RoleFullDto createRole(RoleCreationUpdateDto roleCreationDto) throws TechnicalError, FunctionalException;

    /**
     * Update role
     *
     * @param id
     * @param updatedDto
     * @return
     */
    RoleFullDto updateRole(Long id, RoleCreationUpdateDto updatedDto) throws Exception;

    /**
     * Update role labels
     *
     * @param id
     * @param updateLabels
     * @return
     */
    RoleFullDto updateRole(Long id, Map<Language, String> updateLabels);

    /**
     * Get role full informations
     *
     * @param id
     * @return
     */
    RoleFullDto getRole(Long id);
}
