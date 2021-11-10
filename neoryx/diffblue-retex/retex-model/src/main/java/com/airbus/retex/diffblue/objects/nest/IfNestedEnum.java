package com.airbus.retex.diffblue.objects.nest;

import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.role.RoleFeature;
import com.airbus.retex.model.basic.AbstractBaseModelState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
public class IfNestedEnum extends AbstractBaseModelState {

    public IfNestedEnum(Long id) {
        this.setId(id);
    }

    public double ifNestedEnum(RoleFeature roleFeature){
        if (roleFeature.getCode().equals(FeatureCode.ADMIN)) {
            return 0;
        } else {
            return 1;
        }
    }

}
