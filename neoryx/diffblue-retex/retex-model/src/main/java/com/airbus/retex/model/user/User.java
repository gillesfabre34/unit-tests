package com.airbus.retex.model.user;

import com.airbus.retex.model.admin.airbus.AirbusEntity;
import com.airbus.retex.model.admin.role.Role;
import com.airbus.retex.model.basic.AbstractBaseModelState;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.request.Request;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Audited
@Getter
@Setter
public class User extends AbstractBaseModelState {

    private static final long serialVersionUID = -289283071472810991L;

    @Column(unique = true)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "staff_number")
    private String staffNumber;

    @Column(name = "airbus_entity_id")
    private Long airbusEntityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airbus_entity_id", insertable = false, updatable = false)
    private AirbusEntity airbusEntity;

    @Column(name = "language")
    @Enumerated(EnumType.STRING)
    private Language language;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "userId")
    private List<UserFeature> userFeatures = new ArrayList<>();

    @ManyToMany(mappedBy = "technicalResponsibles", fetch = FetchType.LAZY)
    private List<Request> requests = new ArrayList<>();

    public void addRole(Role role) {
        this.roles.add(role);
        role.getUsers().add(this);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getUsers().remove(this);
    }
}
