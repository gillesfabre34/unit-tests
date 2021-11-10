package com.airbus.retex.model.material;

import com.airbus.retex.model.basic.IIdentifiedModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Audited
@NoArgsConstructor
@AllArgsConstructor
public class Material implements IIdentifiedModel<String>, Serializable {

    @Id
    @Column(name = "code")
    private String code;


    @Override
    public String getId() {
        return code;
    }
}