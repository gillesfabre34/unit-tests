package com.airbus.retex.service.filtering;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.filtering.FilteringDto;
import com.airbus.retex.business.dto.filtering.FilteringUpdateDto;
import com.airbus.retex.business.dto.user.ReferenceDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.common.EnumFilteringPosition;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.persistence.filtering.FilteringRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class FilteringUpdateServiceIT extends AbstractServiceIT {

    @Autowired
    IFilteringService filteringService;

    @Autowired
    FilteringRepository filteringRepository;

    Filtering filtering;
    FilteringUpdateDto dto;

    @BeforeEach
    public void before() {
        dto = new FilteringUpdateDto();
        filtering = dataSetInitializer.createFiltering(f -> {
            f.setPhysicalPart(dataSetInitializer.createPhysicalPart());
            f.setPosition(EnumFilteringPosition.UP);
            f.setNotification("default notification");
        });
    }

    @Test
    public void updateFiltering_OK_all_fields() throws Exception {
        dto.setAircraftFamily(new ReferenceDto<Long>(dataset.aircraft_family_2.getId()));
        dto.setAircraftType(new ReferenceDto<Long>(dataset.aircraft_type_2.getId()));
        dto.setAircraftVersion(new ReferenceDto<Long>(dataset.aircraft_version_2.getId()));
        dto.setAircraftSerialNumber("64198495");
        dto.setNotification("test NOTIFICATION");
        dto.setPosition(EnumFilteringPosition.LEFT_HAND_SIDE);

        Filtering filtering = updateFiltering();
        basicAssert(filtering);
    }

    @Test
    public void updateFiltering_OK_no_fields() throws Exception {
        Filtering filtering = updateFiltering();
        basicAssert(filtering);
    }

    @Test
    public void updateFiltering_OK_with_Medias() throws Exception {
        dto.setMedias(List.of(dataset.media_1.getUuid()));
        Filtering filtering = updateFiltering();
        basicAssert(filtering);
    }

    @Test
    public void updateFiltering_OK_without_Medias() throws Exception {
        dto.setMedias(List.of());
        Filtering filtering = updateFiltering();
        basicAssert(filtering);
    }


    @Test
    public void updateFiltering_OK_zero_in_numeric_fields() throws Exception {
        dto.setAircraftSerialNumber("0");
        Filtering filtering = updateFiltering();
        assertNull(filtering.getAircraftSerialNumber());
    }

    @Test
    public void updateFiltering_OK_reset_aircraft() throws Exception {
        assertNotNull(filtering.getAircraftFamily());
        assertNotNull(filtering.getAircraftType());
        assertNotNull(filtering.getAircraftVersion());
        dto.setAircraftFamily(new ReferenceDto<Long>(null));
        dto.setAircraftType(new ReferenceDto<Long>(null));
        dto.setAircraftVersion(new ReferenceDto<Long>(null));
        Filtering filtering = updateFiltering();
        assertNull(filtering.getAircraftFamily());
        assertNull(filtering.getAircraftType());
        assertNull(filtering.getAircraftVersion());
    }

    @Test
    public void updateFiltering_OK_reset_aircraft2() throws Exception {
        assertNotNull(filtering.getAircraftFamily());
        assertNotNull(filtering.getAircraftType());
        assertNotNull(filtering.getAircraftVersion());
        dto.setAircraftFamily(new ReferenceDto<Long>(0L));
        dto.setAircraftType(new ReferenceDto<Long>(0L));
        dto.setAircraftVersion(new ReferenceDto<Long>(0L));
        Filtering filtering = updateFiltering();
        assertNull(filtering.getAircraftFamily());
        assertNull(filtering.getAircraftType());
        assertNull(filtering.getAircraftVersion());
    }

    @Test
    public void updateFiltering_KO_not_found() throws Exception {
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            filteringService.updateFiltering(1266511894L, dto);
        });
        assertEquals("retex.error.filtering.not.found", thrown.getMessage());
    }

    @Test
    public void updateFiltering_KO_invalid_aircraftFamily() throws Exception {
        dto.setAircraftFamily(new ReferenceDto<Long>(5645649L));
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            updateFiltering();
        });
        assertEquals("retex.error.aircraft.family.not.found", thrown.getMessage());
    }

    @Test
    public void updateFiltering_KO_invalid_aircraftType() throws Exception {
        dto.setAircraftType(new ReferenceDto<Long>(5645649L));
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            updateFiltering();
        });
        assertEquals("retex.error.aircraft.type.not.found", thrown.getMessage());
    }

    @Test
    public void updateFiltering_KO_invalid_aircraftVersion() throws Exception {
        dto.setAircraftVersion(new ReferenceDto<Long>(5645649L));
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            updateFiltering();
        });
        assertEquals("retex.error.aircraft.version.not.found", thrown.getMessage());
    }

    @Test
    public void updateFiltering_KO_closed() throws Exception {
        filtering = dataSetInitializer.createFiltering(f -> {
            f.setPhysicalPart(dataSetInitializer.createPhysicalPart());
            f.setStatus(EnumStatus.CLOSED);
        });
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            updateFiltering();
        });
        assertEquals("retex.error.filtering.update.closed", thrown.getMessage());
    }

    private Filtering updateFiltering() throws Exception {
        Long id = filtering.getId();
        FilteringDto filteringDto = filteringService.updateFiltering(filtering.getId(), dto);
        assertEquals(id, filteringDto.getId());
        return  filteringRepository.getOne(id);
    }

    private void basicAssert(Filtering filtering) throws Exception {
        if(dto.getMedias() == null) {
            assertNull(filtering.getMedias());
        }else {
            assertEquals(dto.getMedias().size(),filtering.getMedias().size());
        }
        if (dto.getAircraftFamily() == null) {
            assertNull(filtering.getAircraftFamily());
        } else {
            assertEquals(dto.getAircraftFamily().getId(), filtering.getAircraftFamily().getId());
        }
        if (dto.getAircraftType() == null) {
            assertNull(filtering.getAircraftType());
        } else {
            assertEquals(dto.getAircraftType().getId(), filtering.getAircraftType().getId());
        }
        if (dto.getAircraftVersion() == null) {
            assertNull(filtering.getAircraftVersion());
        } else {
            assertEquals(dto.getAircraftVersion().getId(), filtering.getAircraftVersion().getId());
        }
        assertEquals(dto.getAircraftSerialNumber(), filtering.getAircraftSerialNumber());
        assertEquals(dto.getNotification(), filtering.getNotification());
        assertEquals(dto.getPosition(), filtering.getPosition());

        assertEquals(LocalDate.now(), filtering.getLastModified());
        assertEquals(EnumStatus.CREATED, filtering.getStatus());
    }
}

