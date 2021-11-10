package com.airbus.retex.model.part;

import com.airbus.retex.model.basic.IIdentifiedModel;
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
@NoArgsConstructor
public class Mpn implements IIdentifiedModel<String> , Serializable {

    @Id
    @Column(name = "code")
    private String code;

    public Mpn(String mpnCode) {
        this.code = mpnCode;
    }

    @Override
    public String getId() {
        return code;
    }
}
