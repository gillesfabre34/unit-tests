package com.airbus.retex.model.user;

import com.airbus.retex.model.admin.role.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(UserRole.UserRoleId.class)
public class UserRole implements Serializable{
    private static final long serialVersionUID = 1344377706277625447L;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "role_id")
    private Long roleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Role role;

    public UserRole(User user, Role role) {
        this.userId = user.getId();
        this.roleId = role.getId();
    }

    public UserRole(User user, Long roleId) {
        this.userId = user.getId();
        this.roleId = roleId;
    }

    @Getter
    @Setter
    static class UserRoleId implements Serializable {
        @Column(name = "user_id")
        private Long userId;

        @Column(name = "role_id")
        private Long roleId;
    }
}
