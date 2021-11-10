package com.airbus.retex.business.authentication;

import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.model.common.EnumRightLevel;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.user.User;

import java.util.List;
import java.util.Map;

public interface CustomUserDetails  {

    public User getUser();
    public Map<FeatureCode, EnumRightLevel> getFeatureRights();
    public Language getLanguage();
    public Long getAirbusEntityId();
    public String getAirbusEntityName();
    public Long getUserId();
    public boolean isRole(List<RoleCode> roleCodeList);
}

