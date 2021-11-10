package com.airbus.retex.controller.admin;

import com.airbus.retex.business.authentication.CustomUserDetails;
import com.airbus.retex.business.dto.role.RoleCreationUpdateDto;
import com.airbus.retex.business.dto.role.RoleDto;
import com.airbus.retex.business.dto.role.RoleFullDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.configuration.CustomLocalResolver;
import com.airbus.retex.service.admin.IRoleService;
import com.airbus.retex.utils.ConstantUrl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.airbus.retex.utils.UserAccessManager.hasAccessAllAirbusEntity;
import static com.airbus.retex.utils.UserAccessManager.manageAccessAirbusEntity;


@Api(value = "Role", tags = {"Manage Roles Endpoints"})
@RestController
public class RoleController {

    @Autowired
    public IRoleService roleService;

    @Autowired
    public CustomLocalResolver customLocalResolver;

    @Autowired
    public HttpServletRequest httpServletRequest;


    @ApiOperation("Get list of roles and their segregation filtered or not")
    @GetMapping(ConstantUrl.API_ROLE_WITH_SEGREGATION_ADMIN)
    public ResponseEntity<List<RoleDto>> getAllRoles(Authentication authentication,
                                                     @RequestParam(required = false) Long airbusEntityId,
                                                     @RequestParam boolean allLabels) {
        CustomUserDetails customUserDetails = ((CustomUserDetails) authentication.getPrincipal());

        Locale locale = customLocalResolver.resolveLocale(httpServletRequest);

        List<RoleDto> roles = new ArrayList<>();
        if (airbusEntityId != null && airbusEntityId.equals(manageAccessAirbusEntity(customUserDetails, airbusEntityId))) {
            if (allLabels) {
                roles = roleService.getAllRolesByAirbusEntityAndLanguage(airbusEntityId, null);
            } else {
                roles = roleService.getAllRolesByAirbusEntityAndLanguage(customUserDetails.getAirbusEntityId(), locale);
            }
        } else if (airbusEntityId == null && hasAccessAllAirbusEntity(customUserDetails)) {
            if (allLabels) {
                roles = roleService.getAllRolesWithAllAirbusEntities(null);
            } else {
                roles = roleService.getAllRolesWithAllAirbusEntities(locale);
            }
        }
        return ResponseEntity.ok().headers(new HttpHeaders()).body(roles);
    }
    @PostMapping(ConstantUrl.API_ROLES)
    public ResponseEntity<RoleFullDto> createRole(Authentication authentication,
                                                  @RequestBody @Valid RoleCreationUpdateDto creationDto) throws FunctionalException {
        CustomUserDetails customUserDetails = ((CustomUserDetails) authentication.getPrincipal());
        if (manageAccessAirbusEntity(customUserDetails, creationDto.getAirbusEntity().getId()).equals(creationDto.getAirbusEntity().getId())) {
            RoleFullDto role = roleService.createRole(creationDto);
            return ResponseEntity.ok().headers(new HttpHeaders()).body(role);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).headers(new HttpHeaders()).build();
    }
    @PutMapping(ConstantUrl.API_ROLE_UPDATE)
    public ResponseEntity<RoleFullDto> updateRole(Authentication authentication,
                                                  @PathVariable Long id,
                                                  @RequestBody RoleCreationUpdateDto updateDto) {
        CustomUserDetails customUserDetails = ((CustomUserDetails) authentication.getPrincipal());
        if (manageAccessAirbusEntity(customUserDetails, updateDto.getAirbusEntity().getId()).equals(updateDto.getAirbusEntity().getId())) {
            try {
                RoleFullDto role = roleService.updateRole(id, updateDto);
                return ResponseEntity.ok().headers(new HttpHeaders()).body(role);
            } catch (Exception e) {
                throw new RuntimeException(e);
                // TODO : exception in there
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).headers(new HttpHeaders()).build();
    }
    @GetMapping(ConstantUrl.API_ROLE)
    public ResponseEntity<RoleFullDto> getRole(Authentication authentication,
                                               @PathVariable Long id) {
        CustomUserDetails customUserDetails = ((CustomUserDetails) authentication.getPrincipal());
        RoleFullDto roleFullDto = roleService.getRole(id);
        if (manageAccessAirbusEntity(customUserDetails, roleFullDto.getAirbusEntity().getId()).equals(roleFullDto.getAirbusEntity().getId())) {
            return ResponseEntity.ok().headers(new HttpHeaders()).body(roleFullDto);
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).headers(new HttpHeaders()).build();
    }
    @ApiOperation("Get list of roles filtered by airbusEntity and language")
    @GetMapping(ConstantUrl.API_ROLES)
    public ResponseEntity<List<RoleDto>> getAllRoles(Authentication authentication,
                                                     @RequestParam(required = false) Long airbusEntityId) {
        CustomUserDetails customUserDetails = ((CustomUserDetails) authentication.getPrincipal());
        Long airbusId = manageAccessAirbusEntity(customUserDetails, airbusEntityId);
        List<RoleDto> listDto = roleService.getAllRolesByAirbusEntity(airbusId);
        return ResponseEntity.ok().headers(new HttpHeaders()).body(listDto);
    }
    @PutMapping(ConstantUrl.API_ROLE_REVOKE)
    public ResponseEntity<String> revokeRole(Authentication authentication,
                                             @RequestParam Long roleId) {
        boolean updated;
        CustomUserDetails customUserDetails = ((CustomUserDetails) authentication.getPrincipal());

        if (hasAccessAllAirbusEntity(customUserDetails)) {
            updated = roleService.revokeRole(roleId);
        } else {
            updated = roleService.revokeRole(roleId, customUserDetails.getAirbusEntityId());
        }
        return updated ? ResponseEntity.ok().headers(new HttpHeaders()).build()
                : ResponseEntity.status(HttpStatus.FORBIDDEN).headers(new HttpHeaders()).build();
    }

}
