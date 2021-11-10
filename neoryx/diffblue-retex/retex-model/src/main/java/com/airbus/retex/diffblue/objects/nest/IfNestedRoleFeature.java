package com.airbus.retex.diffblue.objects.nest;

import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.airbus.AirbusEntity;
import com.airbus.retex.model.admin.role.RoleCode;
import com.airbus.retex.model.admin.role.RoleFeature;
import com.airbus.retex.model.basic.AbstractBaseModelState;
import com.airbus.retex.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
public class IfNestedRoleFeature extends AbstractBaseModelState {

    public IfNestedRoleFeature(Long id) {
        this.setId(id);
    }

    public double ifNestedRoleFeature(RoleFeature roleFeature){
        if (roleFeature.getRole().getId().equals(2L)) {
            return 0;
        } else {
            return 1;
        }
    }

}
