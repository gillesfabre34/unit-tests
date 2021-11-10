package com.airbus.retex.service.admin;

import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.role.RoleDto;
import com.airbus.retex.business.dto.user.*;
import com.airbus.retex.business.exception.FunctionalError;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.user.User;

import java.util.List;
import java.util.Map;

public interface IUserService {

    Map<FeatureCode, EnumRightLevel> getUserFeatureRights(User user);

    PageDto<UserLightDto> findUsersByRole(final UserFilteringDto userFilteringdto) throws FunctionalException;

    /**
     * Get all users with airbus entity id or having the operator/technicalManager role, page, state and search text.
     *
     * @param userFilteringdto      - com.airbus.retex.filtering datas of users to find
     * @return PageUserDto - Containing usersDto paginated, total users returned and total pages.
     */
    PageDto<UserDto> findUsersByAirbusEntity(final UserFilteringDto userFilteringdto) throws FunctionalException;

    /**
     * create a new user
     *
     * @param creationDto - datas of user to register
     * @return UserFullDto
     * @throws FunctionalError
     */
    UserFullDto createUser(UserCreationDto creationDto) throws FunctionalError, Exception;

    /**
     * get user informations by id
     *
     * @param id
     * @return
     * @throws Exception
     */
    UserFullDto getUserById(Long id) throws FunctionalException;

    /**
     * Update user informations
     * @return
     * @throws Exception
     */
    UserFullDto updateUserRolesAndFeatures(Long id, UserUpdateDto userUpdateDto) throws Exception;

    /**
     * revoke a user => disabled user
     * @param id
     * @return
     * @throws Exception
     */
    UserDto revokeUser(Long id) throws Exception;

    /**
     * Get user's role list
     * @return
     * @throws FunctionalException
     */
    List<RoleDto> getRolesOfUser(Long userID) throws FunctionalException;

    /**
     * check if the authenticated user can access given feature with that Feature Right
     * Example : user X can access to DRT with WRITE right?
     * @param featureRightDto
     * @return
     * @throws FunctionalException
     */
    // TODO :  FIXME : UserService.getUserFeatureRights
    //boolean userCanAccessFeature(Long userId, FeatureCode featureCode, EnumRightLevel featureRightDto) throws FunctionalException;

    boolean isAuthentifiedUserHasOperatorRole(final Long userId);

    boolean isAuthentifiedUserHasTechnicalResponsibleRole(final Long userId);

    boolean isAuthentifiedUserHasAdminRole(final Long userId);

    }