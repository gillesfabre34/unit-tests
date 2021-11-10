package com.airbus.retex.utils;

import com.airbus.retex.business.authentication.CustomUserDetails;
import com.airbus.retex.exception.ForbiddenException;
import org.springframework.stereotype.Component;

@Component
public class UserAccessManager {

    private UserAccessManager() {
    }

    /**
     * Manage all rights from parameter and user airbusEntityId
     *
     * @param userDetails    Principal
     * @param airbusEntityId Long
     * @return Long
     */
    public static Long manageAccessAirbusEntity(CustomUserDetails userDetails, Long airbusEntityId) {

        // if user as all rights , apply Param String airbusEntityId
        if (hasAccessAllAirbusEntity(userDetails)) {
            return airbusEntityId;
        }
        // if user as restriction,apply this one
        else {

            if (airbusEntityId == null) {
                return userDetails.getAirbusEntityId();
            } else if (airbusEntityId.equals(userDetails.getAirbusEntityId())) {
                return airbusEntityId;
            } else {
                throw new ForbiddenException();
            }
        }
    }

    /**
     * Return true if user has access to all airbusEntity
     *
     * @param userDetails Principal
     * @return boolean
     */
    public static boolean hasAccessAllAirbusEntity(CustomUserDetails userDetails) {
        return userDetails.getAirbusEntityName().equalsIgnoreCase("france");//FIXME Hardcoded value and test unsafe, at least, move into configuration
    }

}
