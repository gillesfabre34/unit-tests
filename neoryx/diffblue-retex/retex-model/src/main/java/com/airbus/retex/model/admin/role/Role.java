package com.airbus.retex.model.admin.role;

import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.admin.airbus.AirbusEntity;
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
public class Role extends AbstractBaseModelState {
    public static final String FIELD_LABEL = "label";

    @ManyToOne
    @JoinColumn(name = "airbus_entity_id", nullable = false)
    private AirbusEntity airbusEntity;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "roles")
    private Set<User> users=new HashSet<>();

    @Column(name = "role_code")
    @Enumerated(value = EnumType.STRING)
    private RoleCode roleCode;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "role")
    private Set<RoleFeature> features = new HashSet<>();

    public Role(Long id) {
        this.setId(id);
    }

    public void addRoleFeature(RoleFeature roleFeature){
        this.features.add(roleFeature);
        roleFeature.setRole(this);
    }

    public void removeRoleFeature(RoleFeature roleFeature){
        this.features.remove(roleFeature);
        roleFeature.setRole(null);
    }

    public double setFeatureCode(RoleFeature roleFeature){
        if (roleFeature.getCode().equals(FeatureCode.ADMIN)) {
            return 0;
        } else {
            return 1;
        }
    }

}
