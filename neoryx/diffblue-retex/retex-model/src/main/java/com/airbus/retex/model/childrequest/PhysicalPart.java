package com.airbus.retex.model.childrequest;

import com.airbus.retex.model.basic.AbstractBaseModel;
import com.airbus.retex.model.part.Part;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Table(name = "physical_part")
@Audited
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhysicalPart extends AbstractBaseModel {
    @Column(name = "serial_number")
    private String serialNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_request_id")
    private ChildRequest childRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private Part part;

    @Column(name = "equipment_number")
    private String equipmentNumber;
}
