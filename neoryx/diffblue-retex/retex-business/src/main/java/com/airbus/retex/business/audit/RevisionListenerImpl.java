package com.airbus.retex.business.audit;

import com.airbus.retex.business.authentication.CustomUserDetails;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Listener for hibernate envers Revision class , assign userId to revision
 * Table
 *
 * @author mduretti
 */
public class RevisionListenerImpl implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (null == auth) {
            return;
        }

        if (auth.isAuthenticated() ) {
            CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
            if(userDetails != null){
                ((Revision) revisionEntity).setUserId(userDetails.getUserId());
            }
        }
    }
}
