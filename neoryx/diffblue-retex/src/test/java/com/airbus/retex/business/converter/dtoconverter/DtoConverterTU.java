package com.airbus.retex.business.converter.dtoconverter;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.Dto;
import com.airbus.retex.model.basic.IIdentifiedModel;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
public class DtoConverterTU {

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private DtoConverter dtoConverter;

    @Test
    public void updateEntity() {dtoConverter.postContruct();
        UpdateTestDto dto = new UpdateTestDto();
        UpdateTestEntity entity = new UpdateTestEntity();

        dtoConverter.updateEntity(dto, entity);

        assertFieldCopied(entity.getLongField(), dto.getLongField());
        assertFieldCopied(entity.getStringField(), dto.getStringField());
        assertFieldCopied(entity.getIntegerField(), dto.getIntegerField());
        assertFieldCopied(entity.getLocalDateField(), dto.getLocalDateField());
        assertFieldCopied(entity.getLocalTimeDateField(), dto.getLocalTimeDateField());

        assertThat(entity.getListField(), nullValue());
        assertThat(entity.getSetField(), nullValue());
    }

    @Test
    public void updatePartialEntity() {dtoConverter.postContruct();
        UpdateTestDto dto = new UpdateTestDto();
        UpdateTestEntity entity = new UpdateTestEntity();

        dtoConverter.updateEntity(dto, entity);
        assertThat(entity.getLongField(), notNullValue());
        dto.setLongField(null);
        dtoConverter.updateEntity(dto, entity);
        assertThat(entity.getLongField(), nullValue());
    }

    public <T> void assertFieldCopied(T entityField, T dtoField) {
        assertThat(entityField, notNullValue());
        assertThat(entityField, equalTo(dtoField));
    }

    @Getter
    @Setter
    public static class UpdateTestDto implements Dto {

        private Long longField = 1L;

        private Integer integerField = 2;

        private String stringField = "string";

        private LocalDate localDateField = LocalDate.now();

        private LocalDateTime localTimeDateField = LocalDateTime.now();

        private List<Integer> listField = new ArrayList<>();

        private Set<Integer> setField = new HashSet<>();
    }

    @Getter
    @Setter
    public static class UpdateTestEntity implements IIdentifiedModel<Long> {

        private Long longField;

        private Integer integerField;

        private String stringField;

        private LocalDate localDateField;

        private LocalDateTime localTimeDateField;

        private List<Integer> listField;

        private Set<Integer> setField;

        @Override
        public Long getId() {
            return longField;
        }
    }
}
