package com.airbus.retex.model.functional;

import com.airbus.retex.model.basic.AbstractVersionableChildModel;
import com.airbus.retex.model.classification.EnumClassification;
import com.airbus.retex.model.functionality.Functionality;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.model.treatment.Treatment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Audited
@NoArgsConstructor
public class FunctionalArea extends AbstractVersionableChildModel<Long> implements Comparable <FunctionalArea> {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functionality_id", nullable = false)
    private Functionality functionality;

    @Enumerated(EnumType.STRING)
    private EnumClassification classification;

    @Column(name = "material_code")
    private String material;

    @Column(name = "areaNumber")
    private String areaNumber;

    @Column(name = "is_disabled")
    private boolean disabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treatment_id", nullable = false)
    private Treatment treatment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "functional_area_name_id", nullable = false)
    private FunctionalAreaName functionalAreaName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", referencedColumnName = "id")
    private Part part;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "functionalArea")
    private Set<OperationFunctionalArea> operationFunctionalAreas;

    @Override
    public int compareTo(FunctionalArea o) {
        return areaNumber.compareTo(o.getAreaNumber());
    }
}
