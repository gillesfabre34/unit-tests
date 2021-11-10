package com.airbus.retex.model.translation;

import com.airbus.retex.model.common.Language;
import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@Audited
@IdClass(Translate.TranslateId.class)
public class Translate  implements Serializable {

    public static final String TRANSLATE_CACHE = "translateCacheable";

    @Id
    @Column(name = "class_name")
    private String className;

    //TODO RENAME TO TECH_ID
    @Id
    @Column(name = "entity_id")
    private Long entityId;

    @Id
    private String field;

    @Id
    @Enumerated(value = EnumType.STRING)
    private Language language;

    private String value;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TranslateId implements Serializable {

        @Column(name = "class_name")
        private String className;

        @Column(name = "entity_id")
        private Long entityId;

        private String field;

        @Enumerated(value = EnumType.STRING)
        private Language language;
    }
}
