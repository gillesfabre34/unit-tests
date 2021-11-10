package com.airbus.retex.model.client;

import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.childrequest.ChildRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
public class Client extends AbstractBaseModel {

    @Column(name="name")
    private String name;

    @ManyToMany(mappedBy = "clients", fetch = FetchType.LAZY)
    private Set<ChildRequest> childRequests = new HashSet<>();

}
