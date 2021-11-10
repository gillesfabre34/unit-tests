package com.airbus.retex.model.part;

import com.airbus.retex.model.ata.ATA;
import com.airbus.retex.model.basic.AbstractVersionableModel;
import com.airbus.retex.model.functional.FunctionalArea;
import com.airbus.retex.model.media.Media;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.*;

@Entity
@Audited
@Getter
@Setter
public class Part extends AbstractVersionableModel<Long> {

    @Column(name = "part_number")
    private String partNumber;

    @Column(name = "part_number_root")
    private String partNumberRoot;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Media media;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_designation_id")
    private PartDesignation partDesignation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ata_code")
    private ATA ata;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "part_mpn",
            joinColumns = { @JoinColumn(name = "part_id") },
            inverseJoinColumns = { @JoinColumn(name = "code") }
    )
    @OrderColumn(name = "id")
    private Set<Mpn> mpnCodes = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "part")
    @OrderBy("areaNumber")
    private SortedSet<FunctionalArea> functionalAreas;

    public void addAllMpn(Set<Mpn> mpns ) {
        this.mpnCodes.addAll(mpns);
    }

    public void addMpn(Mpn mpns) {
        this.mpnCodes.add(mpns);
    }

    public void addFunctionalAreas(FunctionalArea functionalArea){
        if(null == this.functionalAreas){
            this.functionalAreas = new TreeSet<>();
        }
        functionalArea.setPart(this);
        this.functionalAreas.add(functionalArea);
    }
}
