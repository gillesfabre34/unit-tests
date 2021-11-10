package com.airbus.retex.model.admin.role;

import com.airbus.retex.model.admin.FeatureCode;
import com.airbus.retex.model.common.EnumRightLevel;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(RoleFeature.RoleFeatureId.class)
@Table(name = "role_feature")
public class RoleFeature implements Serializable {
    private static final long serialVersionUID = 8967765798466015851L;

    @Id
    @Column(name = "role_id")
    private Long roleId;

    @Id
    @Enumerated(value = EnumType.STRING)
    private FeatureCode code;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "right_level")
    private EnumRightLevel rightLevel;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "role_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Role role;

    public RoleFeature(Long roleId, FeatureCode code, EnumRightLevel rightLevel) {
        this.roleId = roleId;
        this.code = code;
        this.rightLevel = rightLevel;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class RoleFeatureId implements Serializable {

        @Column(name = "role_id")
        private Long roleId;

        @Enumerated(EnumType.STRING)
        private FeatureCode code;
    }
}
