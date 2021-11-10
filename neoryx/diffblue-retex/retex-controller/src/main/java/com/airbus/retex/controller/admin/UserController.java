package com.airbus.retex.controller.admin;

import com.airbus.retex.business.authentication.CustomUserDetails;
import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.feature.FeatureRightDto;
import com.airbus.retex.business.dto.role.RoleDto;
import com.airbus.retex.business.dto.user.*;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.service.admin.IUserService;
import com.airbus.retex.utils.ConstantUrl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.airbus.retex.utils.UserAccessManager.manageAccessAirbusEntity;

@Api(value = "User", tags = {"Users"})
@RestController
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private DtoConverter dtoConverter;

    @ApiOperation("Get list of users filtered by airbusEntity and language, with pagination")
    @GetMapping(ConstantUrl.API_USERS)
    @Secured("ROLE_ADMIN:READ")
    public ResponseEntity<PageDto<UserDto>> getAllUsersByCountry(Authentication authentication,
                                                                 @Valid UserFilteringDto userFilteringDto) throws FunctionalException {
        return ResponseEntity.ok().body(userService.findUsersByAirbusEntity(userFilteringDto));
    }
    @ApiOperation("Get list of users filtered by role with pagination")
    @GetMapping(ConstantUrl.API_USERS_ROLE)
    @Secured("ROLE_REQUEST:READ")
    public ResponseEntity<PageDto<UserLightDto>> getAllUsersByRole(@Valid UserFilteringDto userFilteringDto) throws FunctionalException {
        return ResponseEntity.ok().body(userService.findUsersByRole(userFilteringDto));
    }
    @ApiOperation("Retrieve information from the authenticated user")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Ok"),
                    @ApiResponse(code = 401, message = "Unauthorized")
            })
    @GetMapping(ConstantUrl.API_USER_ME)
    public ResponseEntity<UserInfosDto> getAuthenticatedUserInformation(Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserInfosDto userInfosDto = dtoConverter.toDto(userDetails.getUser(), UserInfosDto.class);
        List<FeatureRightDto> features = userDetails.getFeatureRights().entrySet().stream()
                .map(item -> new FeatureRightDto(item.getKey(), item.getValue()))
                .collect(Collectors.toList());
        userInfosDto.setFeatures(features);

        return ResponseEntity.ok().headers(new HttpHeaders()).body(userInfosDto);
    }
    @ApiOperation("Create a user")
    @PostMapping(ConstantUrl.API_USERS)
    @Secured("ROLE_ADMIN:WRITE")
    public ResponseEntity<UserFullDto> createUser(Authentication authentication,
                                              @RequestBody @Valid UserCreationDto creationDto) throws Exception {
        CustomUserDetails customUserDetails = ((CustomUserDetails) authentication.getPrincipal());
        if (manageAccessAirbusEntity(customUserDetails, creationDto.getAirbusEntityId()).equals(creationDto.getAirbusEntityId())) {
            return ResponseEntity.ok().body(userService.createUser(creationDto));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    @ApiOperation("Get a user")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Ok"),
            @ApiResponse(code = 401, message = "Unauthorized")})
    @GetMapping(ConstantUrl.API_USER)
    @Secured("ROLE_ADMIN:READ")
    public ResponseEntity<UserFullDto> findById(Authentication authentication, @PathVariable(value = "id") Long id) throws FunctionalException {
        return ResponseEntity.ok().body(userService.getUserById(id));
    }
    @ApiOperation("Update a user")
    @PutMapping(ConstantUrl.API_USER)
    @Secured("ROLE_ADMIN:WRITE")
    public ResponseEntity<UserFullDto> updateUser(Authentication authentication,
                                              @PathVariable(value = "id") Long id,
                                              @RequestBody @Valid UserUpdateDto userUpdateDto) throws Exception {
        return ResponseEntity.ok().body(userService.updateUserRolesAndFeatures(id, userUpdateDto));
    }
    @ApiOperation("Revoke user ")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Ok"),
                    @ApiResponse(code = 404, message = "Not Found")
            })
    @PutMapping(ConstantUrl.API_USERS_REVOKE)
    @Secured("ROLE_ADMIN:WRITE")
    public ResponseEntity<UserDto> revokeUser(Authentication authentication, @PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok().body(userService.revokeUser(id));
        } catch (Exception e) {
            return new ResponseEntity<>(null, new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }
    @ApiOperation("Get user's roles")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Ok"),
                    @ApiResponse(code = 404, message = "Not Found")
            })
    @GetMapping(ConstantUrl.API_USER_ROLES)
    @Secured("ROLE_ADMIN:READ")
    public ResponseEntity<List<RoleDto>> getRolesOfUser(Authentication authentication, @PathVariable("userId") Long userId) throws FunctionalException{
        return ResponseEntity.ok().body(userService.getRolesOfUser(userId));
    }
    /**
     * check if user can access to a feature with the given right
     * @param userId
     * @param feature
     * @throws FunctionalException
     */
    //FIXME : duplication with UserService.getUserFeatureRights (where the code is correct)
//    @ApiOperation("User can have access to feature ?")
//    @ApiResponses(
//            value = {
//                    @ApiResponse(code = 200, message = "Can access"),
//                    @ApiResponse(code = 404, message = "Can't access")
//            })
//    @GetMapping(ConstantUrl.API_USER_CAN_ACCESS_FEATURE)
//    @Secured("ROLE_ADMIN:READ")
//    public ResponseEntity<Boolean> userCanAccessFeature(Authentication authentication,
//                                                        @PathVariable("userId") Long userId,
//                                                        @RequestParam("feature") FeatureCode feature,
//                                                        @RequestParam("right") EnumRightLevel right) throws FunctionalException {
//        return ResponseEntity.ok().body(userService.userCanAccessFeature(userId, feature, right));
//    }


}
