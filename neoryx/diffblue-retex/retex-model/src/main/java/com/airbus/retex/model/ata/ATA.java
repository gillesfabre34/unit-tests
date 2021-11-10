package com.airbus.retex.model.ata;

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

@Entity
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ATA implements IIdentifiedModel<String>, Serializable {

    @Id
    @Column(name = "code")
    private String code;


    @Override
    public String getId() {
        return code;
    }
}
