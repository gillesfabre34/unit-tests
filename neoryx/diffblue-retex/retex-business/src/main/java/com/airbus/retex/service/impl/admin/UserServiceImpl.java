package com.airbus.retex.service.impl.admin;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.feature.FeatureRightDto;
import com.airbus.retex.business.dto.role.RoleDto;
import com.airbus.retex.business.dto.user.UserCreationDto;
import com.airbus.retex.business.dto.user.UserDto;
import com.airbus.retex.business.dto.user.UserFilteringDto;
import com.airbus.retex.business.dto.user.UserFullDto;
import com.airbus.retex.business.dto.user.UserLightDto;
import com.airbus.retex.business.dto.user.UserUpdateDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.role.Role;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.model.admin.specifications.UserSpecification;
import com.airbus.retex.model.common.EnumActiveState;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.user.User;
import com.airbus.retex.model.user.UserFeature;
import com.airbus.retex.model.user.UserRole;
import com.airbus.retex.persistence.admin.RoleRepository;
import com.airbus.retex.persistence.admin.UserFeatureRepository;
import com.airbus.retex.persistence.admin.UserRepository;
import com.airbus.retex.persistence.admin.UserRoleRepository;
import com.airbus.retex.service.admin.IUserService;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements IUserService {
    private static final String ERROR_USER_NOT_FOUND = "retex.user.not.found.label";


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserFeatureRepository userFeatureRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;


    @Autowired
    private DtoConverter dtoConverter;


    @Override
    public Map<FeatureCode, EnumRightLevel> getUserFeatureRights(User user) {
		EnumMap<FeatureCode, EnumRightLevel> featureRights = new EnumMap<>(FeatureCode.class);

		if (CollectionUtils.isNotEmpty(user.getRoles())) {
            user.getRoles().forEach(role ->
                role.getFeatures().forEach(roleFeature -> {
                    FeatureCode code = roleFeature.getCode();
                    EnumRightLevel level = roleFeature.getRightLevel();
                    EnumRightLevel previousLevel = featureRights.getOrDefault(code, EnumRightLevel.NONE);
                    // Update the level only if it is greater than the existing one
                    if (previousLevel.ordinal() < level.ordinal()) {
                        featureRights.put(code, level);
                    }
                })
            );
        }

        // TODO is this necessary ??????????????????
        // we are facing to this error : Caused by: java.lang.IllegalArgumentException:
        // No enum constant com.airbus.retex.model.admin.FeatureCode.1
        // main cause : given user has no roles and userFeatures (both arrays are empty)
        try {
            List<UserFeature> userFeatures = userFeatureRepository.findAllByUserId(user.getId());
            if (CollectionUtils.isNotEmpty(userFeatures)) {
				userFeatures.stream().forEach(userFeature ->
                    // override existing featureRights
				featureRights.put(userFeature.getCode(), userFeature.getRightLevel()));
            }
        } catch (Exception e) {//FIXME remove this catch
            // exception
        }
        return featureRights;
    }

    @Override
    public PageDto<UserLightDto> findUsersByRole(UserFilteringDto userFilteringdto) throws FunctionalException {
        Pageable pageable = PageRequest.of(userFilteringdto.getPage(), userFilteringdto.getSize(), Sort.by("lastName", "firstName"));
        Specification specification = null;

        if (userFilteringdto.getRoleCode() != null ) {
            specification = Specification.where(UserSpecification.filterByRole(userFilteringdto.getRoleCode()));

        }

        Page<User> users = userRepository.findAll(specification, pageable);
        return new PageDto<>(dtoConverter.toDtos(users.getContent(), UserLightDto.class), users.getTotalElements(), users.getTotalPages());
    }

    @Override
    public PageDto<UserDto> findUsersByAirbusEntity(final UserFilteringDto userFilteringdto) throws FunctionalException{
        Pageable pageable = PageRequest.of(userFilteringdto.getPage(), userFilteringdto.getSize(), Sort.by("lastName", "firstName"));
        Specification specification = null;

        if (userFilteringdto.getAirbusEntityId() != null) {
            specification = Specification.where(UserSpecification.filterByAirbusEntity(userFilteringdto.getAirbusEntityId()));
        }else {
            specification = Specification.where(null);
        }

        if (userFilteringdto.getRoleCode() != null ) {
            // TODO : why we need to check this ?
//            if (RoleCode.OPERATOR != userFilteringdto.getRoleCode()) {
//                throw new FunctionalException("retex.user.false.param.notsupported");
//            }
            specification = specification.and(UserSpecification.filterByRole(userFilteringdto.getRoleCode()));

        }

        // TODO :  why we need to filter this way on role type ?

//        if (userFilteringdto.getIsTechnicalResponsible() != null) {
//            if (!userFilteringdto.getIsTechnicalResponsible()){
//                throw new FunctionalException("retex.user.false.param.notsupported");
//            }
//            specification = specification.and(UserSpecification.filterByTechnicalResponsible(userFilteringdto.getIsTechnicalResponsible()));
//        }

        if (userFilteringdto.getOnlyActive()) {
            specification = specification.and(UserSpecification.filterByActive());
        }

        if (userFilteringdto.getSearch().length() > 0) {
            specification = specification.and(UserSpecification.filterbySearch(userFilteringdto.getSearch()));
        }

        Page<User> users = userRepository.findAll(specification, pageable);
        return new PageDto<>(dtoConverter.toDtos(users.getContent(), UserDto.class), users.getTotalElements(), users.getTotalPages());
    }

    @Override
    public UserFullDto createUser(UserCreationDto userCreationDto) throws FunctionalException {
        User user = userRepository.save(dtoConverter.toEntity(userCreationDto, User::new));
        if (userCreationDto.getRoleIds() != null) {
            for (var roleId : userCreationDto.getRoleIds()) {
                userRoleRepository.save(new UserRole(user, roleId));
            }
        }
        if (userCreationDto.getFeatures() != null) {
            for (var feature : userCreationDto.getFeatures()) {
                userFeatureRepository.save(new UserFeature(user.getId(), feature.getCode(), feature.getRightLevel()));
            }
        }
        userRepository.detach(user);
        return dtoConverter.toDto(userRepository.getById(user.getId()), UserFullDto.class);
    }

    @Override
    public UserFullDto getUserById(Long id) throws FunctionalException {
        User user = userRepository.findById(id).orElseThrow(() -> new FunctionalException(ERROR_USER_NOT_FOUND));
        return dtoConverter.toDto(user, UserFullDto.class);
    }

    @Override
    public UserFullDto updateUserRolesAndFeatures(Long id, UserUpdateDto userUpdateDto) throws FunctionalException {
        User user = userRepository.getOne(id);
        user.setLanguage(userUpdateDto.getLanguage());
        if (userUpdateDto.getFeatures() != null) {
            user.setUserFeatures(addUserFeature(user, userUpdateDto.getFeatures()));
            user.setRoles(addRoles(user, userUpdateDto.getRoleIds()));
        }

        userRepository.save(user);
        return dtoConverter.toDto(userRepository.getOne(id), UserFullDto.class);
    }

    private Set<Role> addRoles(User user, List<Long> roleIds) throws FunctionalException{
        Set<Role> roles = new HashSet<>();
        List<Long> alreadySavedRoles = user.getRoles().stream().map(Role::getId).collect(Collectors.toList());

        for(Long roleId: roleIds){
            if(!alreadySavedRoles.contains(roleId)){
                Role role = roleRepository.findById(roleId).orElseThrow(() -> new FunctionalException("retex.error.role.not.found"));
                roles.add(role);
            }
        }
        roles.addAll(user.getRoles());
        return roles;
    }

    private List<UserFeature> addUserFeature(User user, List<FeatureRightDto> featureRightDtos) {

        List<FeatureCode> featureCodes = featureRightDtos.stream().map(FeatureRightDto::getCode).collect(Collectors.toList());
        user.getUserFeatures().stream().forEach(userFeature -> {
            if (!featureCodes.contains(userFeature.getCode())) {
                userFeatureRepository.delete(user.getId(), userFeature.getCode());
            }
        });

        List<UserFeature> userFeatures = new ArrayList<>();

        featureRightDtos.forEach(featureRightDto -> {
            Optional<UserFeature> userFeatureOpt = userFeatureRepository.findUserFeature(user.getId(), featureRightDto.getCode());
            if (userFeatureOpt.isPresent()) {
                UserFeature userFeature = userFeatureOpt.get();
                userFeature.setRightLevel(featureRightDto.getRightLevel());
                userFeatures.add(userFeature);
            } else {
                UserFeature uf = new UserFeature(user.getId(), featureRightDto.getCode(), featureRightDto.getRightLevel());
                userFeatures.add(uf);
            }
        });
        return userFeatures;
    }



    /**
     * Disable user
     * @param id
     * @return
     * @throws FunctionalException
     */
    @Override
    public UserDto revokeUser(Long id) throws FunctionalException {
        User user = userRepository.findById(id).orElseThrow(() -> new FunctionalException(ERROR_USER_NOT_FOUND));
        user.setState(EnumActiveState.REVOKED);
        return dtoConverter.toDto(userRepository.save(user), UserDto.class);
    }

    @Override
    public List<RoleDto> getRolesOfUser(Long userID) throws FunctionalException {
        User user = userRepository.findById(userID).orElseThrow(() -> new FunctionalException(ERROR_USER_NOT_FOUND));
        return dtoConverter.toDtos(user.getRoles(), RoleDto.class);
    }

    private boolean findUserHasRoleCode(final Long userId, final RoleCode roleCode) {
        User user = userRepository.getById(userId);
        return user.getRoles().stream().anyMatch(role -> role.getRoleCode().equals(roleCode));
    }
    @Override
    public boolean isAuthentifiedUserHasOperatorRole(final Long userId) {
        return findUserHasRoleCode(userId, RoleCode.INTERNAL_OPERATOR);
    }
    @Override
    public boolean isAuthentifiedUserHasTechnicalResponsibleRole(final Long userId) {
        return findUserHasRoleCode(userId, RoleCode.TECHNICAL_RESPONSIBLE);
    }

    @Override
    public boolean isAuthentifiedUserHasAdminRole(final Long userId) {
        return findUserHasRoleCode(userId, RoleCode.ADMIN);
    }


    //FIXME : duplication with UserService.getUserFeatureRights (where the code is correct)
//    @Override
//    public boolean userCanAccessFeature(Long userId, FeatureCode featureCode, EnumRightLevel featureRight) throws FunctionalException {
//        User user = userRepository.findById(userId).orElseThrow(() -> new FunctionalException(ERROR_USER_NOT_FOUND));
//        for(Role role :  user.getRoles()){
//            for(RoleFeature roleFeature: role.getFeatures()){
//                if(roleFeature.getCode().equals(featureCode)) {
//                    EnumRightLevel level = roleFeature.getRightLevel();
//                    if(level.equals(EnumRightLevel.WRITE)) return true;
//                    if(level.equals(EnumRightLevel.NONE)) return false;
//                    if(featureRight.equals(EnumRightLevel.READ) && level.equals(EnumRightLevel.READ)) return true;
//                    if(featureRight.equals(EnumRightLevel.WRITE) && level.equals(EnumRightLevel.READ)) return false;
//                }
//            }
//        }
//        return false;
//    }
}