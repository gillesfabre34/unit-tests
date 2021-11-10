package com.airbus.retex.service.filtering;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.filtering.FilteringCreationDto;
import com.airbus.retex.business.dto.filtering.FilteringDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.persistence.childRequest.PhysicalPartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;


public class FilteringCreateServiceIT extends AbstractServiceIT {

    @Autowired
    IFilteringService filteringService;

    @Autowired
    PhysicalPartRepository physicalPartRepository;

    private class CreateFiltering {
        public String pn;
        public String sn;
        public String en;
        Long physicalPartId;
        Long partId;
    }

    CreateFiltering cfWithPhyPart;
    CreateFiltering cfWithPhyPartNoEn;
    CreateFiltering cfWithPhyPartEnNotEqual;
    CreateFiltering cfWithOnlyPart;
    CreateFiltering cfWithNoPart;

    @BeforeEach
    public void before() {
        cfWithPhyPart = initCreateFilteringWithPhyPart( pp -> {
            pp.setSerialNumber("SN8WITHPART1");
            pp.setEquipmentNumber("EN8WITHPART1");
        });
        cfWithPhyPartNoEn = initCreateFilteringWithPhyPart( pp -> {
            pp.setSerialNumber("SN8WITHPART2");
        }, (pp,cf) -> {
            cf.en = "EN8WITHPART2";
        });
        cfWithPhyPartEnNotEqual = initCreateFilteringWithPhyPart( pp -> {
            pp.setSerialNumber("SN8WITHPART3");
            pp.setEquipmentNumber("EN8WITHPART3");
        }, (pp,cf) -> {
            cf.en = "EN8WITHPART3BAD";
        });
        cfWithOnlyPart = initCreateFilteringWithOnlyPart("SN8ONLYPART1", "EN8ONLYPART1");
        cfWithNoPart = initCreateFilteringWithNoPart("PN8NOPART1", "SN8NOPART1", "EN8NOPART1");
    }

    CreateFiltering initCreateFilteringWithPhyPart(Consumer<PhysicalPart> ppModify, BiConsumer<PhysicalPart, CreateFiltering> cfModify) {
        PhysicalPart physicalPart = dataSetInitializer.createPhysicalPart(ppModify);
        CreateFiltering cf = new CreateFiltering();
        cf.pn = physicalPart.getPart().getPartNumber();
        cf.sn = physicalPart.getSerialNumber();
        cf.en = physicalPart.getEquipmentNumber();
        cf.physicalPartId = physicalPart.getId();
        cf.partId = physicalPart.getPart().getTechnicalId();
        cfModify.accept(physicalPart, cf);
        return cf;
    }

    CreateFiltering initCreateFilteringWithPhyPart(Consumer<PhysicalPart> ppModify) {
        return initCreateFilteringWithPhyPart(ppModify, (pp,cf)->{});
    }

    CreateFiltering initCreateFilteringWithOnlyPart(String sn, String en) {
        CreateFiltering cf = new CreateFiltering();
        cf.pn = dataset.part_example.getPartNumber();
        cf.sn = sn;
        cf.en = en;
        cf.partId = dataset.part_example.getTechnicalId();
        cf.physicalPartId = null;
        return cf;
    }

    CreateFiltering initCreateFilteringWithNoPart(String pn, String sn, String en) {
        CreateFiltering cf = new CreateFiltering();
        cf.pn = pn;
        cf.sn = sn;
        cf.en = en;
        cf.partId = null;
        cf.physicalPartId = null;
        return cf;
    }

    @Test
    public void createFiltering_OK_existing_physicalPart() throws Exception {
        CreateFiltering cf = cfWithPhyPart;
        FilteringDto dto = createFiltering(cf);
        basicAssert(cf, dto);
    }

    @Test
    public void createFiltering_OK_create_physicalPart() throws Exception {
        CreateFiltering cf = cfWithOnlyPart;
        assertFalse(physicalPartRepository.findByPartPartNumberAndSerialNumber(cf.pn, cf.sn).isPresent());
        FilteringDto dto = createFiltering(cf);
        basicAssert(cf, dto);
        assertTrue(physicalPartRepository.findByPartPartNumberAndSerialNumber(cf.pn, cf.sn).isPresent());
    }

    @Test
    public void createFiltering_OK_set_equipmentNumber() throws Exception {
        CreateFiltering cf = cfWithPhyPartNoEn;
        assertTrue(physicalPartRepository.findByPartPartNumberAndSerialNumber(cf.pn, cf.sn).isPresent());
        assertFalse(physicalPartRepository.findByEquipmentNumber(cf.en).isPresent());
        FilteringDto dto = createFiltering(cf);
        basicAssert(cf, dto);
        assertTrue(physicalPartRepository.findByEquipmentNumber(cf.en).isPresent());
    }

    @Test
    public void createFiltering_KO_equipmentNumber_different() throws Exception {
        CreateFiltering cf = cfWithPhyPartEnNotEqual;
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            createFiltering(cf);
        });
        String en = runInTransactionAndReturn(() -> {
            return physicalPartRepository.getOne(cf.physicalPartId).getEquipmentNumber();
        });
        assertException(thrown, "retex.error.filtering.creation.en.different.for.pn.sn", en);
    }

    @Test
    public void createFiltering_KO_partNumber_not_found() throws Exception {
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            createFiltering(cfWithNoPart);
        });
        assertException(thrown, "retex.error.filtering.creation.pn.not.found");
    }

    @Test
    public void createFiltering_KO_partNumber_and_serialNumber_found() throws Exception {
        CreateFiltering cf1 = cfWithPhyPart;
        FilteringDto dto = createFiltering(cf1);
        basicAssert(cf1, dto);

        CreateFiltering cf2 = initCreateFilteringWithNoPart(cf1.pn, cf1.sn, "EN8ONLYPART99");
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            createFiltering(cf2);
        });
        assertException(thrown, "retex.error.filtering.creation.pn.sn.found");
    }

    @Test
    public void createFiltering_KO_equipmentNumber_found() throws Exception {
        CreateFiltering cf1 = cfWithPhyPart;
        FilteringDto dto = createFiltering(cf1);
        basicAssert(cf1, dto);

        CreateFiltering cf2 = initCreateFilteringWithNoPart("PN8ONLYPART99", "SN8ONLYPART99", cf1.en);
        FunctionalException thrown = assertThrows(FunctionalException.class, () -> {
            createFiltering(cf2);
        });
        String[] args = runInTransactionAndReturn(() -> {
            PhysicalPart pp = physicalPartRepository.getOne(cf1.physicalPartId);
            return new String[]{pp.getPart().getPartNumber(), pp.getSerialNumber()};
        });
        assertException(thrown, "retex.error.filtering.creation.en.found", args[0], args[1]);
    }

    private FilteringDto createFiltering(CreateFiltering cf) throws Exception {
        FilteringCreationDto createDto = new FilteringCreationDto();
        createDto.setPartNumber(cf.pn);
        createDto.setSerialNumber(cf.sn);
        createDto.setEquipmentNumber(cf.en);
        return filteringService.createFiltering(createDto);
    }

    private void basicAssert(CreateFiltering cf, FilteringDto dto) throws Exception {
        assertNotNull(dto);
        assertThat(dto.getPartNumber(), equalTo(cf.pn));
        assertThat(dto.getSerialNumber(), equalTo(cf.sn));
        assertThat(dto.getEquipmentNumber(), equalTo(cf.en));
        assertThat(dto.getStatus(), equalTo(EnumStatus.CREATED));
    }

    private void assertException(FunctionalException thrown, String code, Object... args) throws Exception {
        assertEquals(code, thrown.getMessage());
        Object[] thrownArgs = thrown.getCodeArgs().get(code);
        if (thrownArgs == null) {
            assertEquals(0, args.length);
        } else {
            assertArrayEquals(args, thrownArgs);
        }
    }
}

