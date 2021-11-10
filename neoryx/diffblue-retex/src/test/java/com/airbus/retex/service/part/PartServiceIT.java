package com.airbus.retex.service.part;

import com.airbus.retex.AbstractServiceIT;
import com.airbus.retex.business.dto.PageDto;
import com.airbus.retex.business.dto.part.PartCreationDto;
import com.airbus.retex.business.dto.part.PartDto;
import com.airbus.retex.business.dto.part.PartFilteringDto;
import com.airbus.retex.business.dto.part.PartUpdateHeaderDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.part.Mpn;
import com.airbus.retex.model.part.Part;
import com.airbus.retex.persistence.part.PartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
public class PartServiceIT extends AbstractServiceIT {

    private static final Integer SIZE_BY_DEFAULT = 20;
    private static final Integer FIRST_PAGE = 0;
    @Autowired
    private IPartService partService;
    @Autowired
    private PartRepository partRepository;

    private PartCreationDto partCreationDto;
    private PartCreationDto partCreationDtoPNRoot;
    private PartUpdateHeaderDto partUpdateDto;

    @BeforeEach
    public void before() {
        partCreationDto = new PartCreationDto();
        partCreationDto.setPartNumber("332132302101");
        partCreationDto.setPartNumberRoot("");
        partCreationDto.setPartDesignationId(dataset.partDesignation_planetGear.getId());
        partCreationDto.setAtaCode(dataset.ata_1.getCode());
        partCreationDto.setMpnCodes(List.of("JDKDB"));

        partCreationDtoPNRoot = new PartCreationDto();
        partCreationDtoPNRoot.setPartNumber(null);
        partCreationDtoPNRoot.setPartNumberRoot("100000001");
        partCreationDtoPNRoot.setPartDesignationId(dataset.partDesignation_planetGear.getId());
        partCreationDtoPNRoot.setMpnCodes(List.of("KDDNDN"));

        partUpdateDto = new PartUpdateHeaderDto();
        partUpdateDto.setPartDesignationId(dataset.partDesignation_planetGear.getId());
        partUpdateDto.setAtaCode(dataset.ata_1.getCode());
        partUpdateDto.setMpnCodes(List.of("KDDNDN", "OIJDI"));
    }

    @Test
    public void correctCreation() throws Exception {
        check();
    }

    @Test()
    public void createWithoutPN_PNRoot() {
        partCreationDto.setPartNumber("");
        partCreationDto.setPartNumberRoot("");
        FunctionalException thrown = assertThrows(FunctionalException.class, this::check);
        assertEquals("retex.part.numbers.notNull", thrown.getMessage());
    }

    @Test
    public void createWithExistingPN() {
        partCreationDto.setPartNumber(dataset.part_example.getPartNumber());
        FunctionalException thrown = assertThrows(FunctionalException.class, this::check);
        assertEquals("retex.part.number.existing", thrown.getMessage());
    }

    @Test()
    public void createWithPN_equals_PNRoot() {
        partCreationDto.setPartNumber("12");
        partCreationDto.setPartNumberRoot("12");
        FunctionalException thrown = assertThrows(FunctionalException.class, this::check);
        assertEquals("retex.part.number.equals.part.number.root", thrown.getMessage());
    }


    private void check() throws Exception {
        Part part = partService.createPart(partCreationDto);

        assertThat(part.getPartNumber(), equalTo(partCreationDto.getPartNumber()));
        assertThat(part.getPartNumberRoot(), equalTo(partCreationDto.getPartNumberRoot()));
        assertThat(part.getPartDesignation().getId(), equalTo(partCreationDto.getPartDesignationId()));
        assertThat(part.getAta().getCode(), equalTo(partCreationDto.getAtaCode()));
        assertThat(part.getMpnCodes().iterator().next().getCode(), equalTo(partCreationDto.getMpnCodes().get(0)));
        assertThat(part.getStatus(), equalTo(EnumStatus.CREATED));

    }

    @Test
    public void createPartWithoutPN() throws FunctionalException {
        Part part = partService.createPart(partCreationDtoPNRoot);

        assertThat(part.getPartNumber(), equalTo(partCreationDtoPNRoot.getPartNumber()));
        assertThat(part.getPartNumberRoot(), equalTo(partCreationDtoPNRoot.getPartNumberRoot()));
        assertThat(part.getPartDesignation().getId(), equalTo(partCreationDtoPNRoot.getPartDesignationId()));
        assertThat(null, equalTo(partCreationDtoPNRoot.getAtaCode()));
        assertThat(part.getMpnCodes().iterator().next().getCode(), equalTo(partCreationDtoPNRoot.getMpnCodes().get(0)));
        assertThat(part.getStatus(), equalTo(EnumStatus.CREATED));
    }

    @Test
    public void updatePartHeader_setMpns() throws FunctionalException {
        Part part = partService.createPart(partCreationDto);
        partService.updatePart(part.getNaturalId(), partUpdateDto);

        Part partUpdated = partService.findPartById(part.getNaturalId(), null);
        assertThat(partUpdated.getPartNumber(), equalTo(partCreationDto.getPartNumber()));
        assertThat(partUpdated.getPartNumberRoot(), equalTo(partCreationDto.getPartNumberRoot()));
        assertThat(partUpdated.getPartDesignation().getId(), equalTo(partUpdateDto.getPartDesignationId()));
        assertThat(partUpdated.getAta().getCode(), equalTo(partUpdateDto.getAtaCode()));
        List<String> partMpnCodes = partUpdated.getMpnCodes().stream().map(Mpn::getCode).collect(Collectors.toList());

        assertThat(partMpnCodes, containsInAnyOrder(partUpdateDto.getMpnCodes().toArray()));
    }

    @Test
    public void updatePartHeader_setEmptyMpn() {
        assertThrows(FunctionalException.class, this::updatePartHeader_WithoutMpn);
    }

    private void updatePartHeader_WithoutMpn() throws FunctionalException {
        partCreationDto.setMpnCodes(new ArrayList<>());
        Part part = partService.createPart(partCreationDto);
        partService.updatePart(part.getNaturalId(), partUpdateDto);
    }


    @Test
    public void updatePartHeader_PnRoot_WithoutMpn() throws FunctionalException {
        partCreationDtoPNRoot.setMpnCodes(null);
        Part part = partService.createPart(partCreationDtoPNRoot);
        partService.updatePart(part.getNaturalId(), partUpdateDto);

        Part newPart = partService.findPartById(part.getNaturalId(), null);
        assertThat(newPart.getPartNumber(), nullValue());
        assertThat(newPart.getPartNumberRoot(), equalTo(partCreationDtoPNRoot.getPartNumberRoot()));
        assertThat(newPart.getPartDesignation().getId(), equalTo(partUpdateDto.getPartDesignationId()));
        assertThat(newPart.getAta().getCode(), equalTo(partUpdateDto.getAtaCode()));
        List<String> partMpnCodes = newPart.getMpnCodes().stream().map(Mpn::getCode).collect(Collectors.toList());

        assertThat(partMpnCodes, containsInAnyOrder(partUpdateDto.getMpnCodes().toArray()));
    }

    @Test
    public void getAllPartWithDeletableTrue_ok() throws FunctionalException {
        partService.createPart(partCreationDto);
        PartFilteringDto filteringDto = new PartFilteringDto();
        filteringDto.setPage(FIRST_PAGE);
        filteringDto.setSize(SIZE_BY_DEFAULT);
        filteringDto.setPartNumber(partCreationDto.getPartNumber());
        PageDto<PartDto> parts = partService.findParts(filteringDto, null);

        assertTrue(parts.getResults().size() == 1);
        assertTrue(parts.getResults().get(0).isDeletable());
    }

    @Test
    public void getAllPartWithDeletableFalse_ok_Routing() throws FunctionalException {
        Part part = partService.createPart(partCreationDto);
        dataSetInitializer.createRouting(part);
        PartFilteringDto filteringDto = new PartFilteringDto();
        filteringDto.setPage(FIRST_PAGE);
        filteringDto.setSize(SIZE_BY_DEFAULT);
        filteringDto.setPartNumber(partCreationDto.getPartNumber());
        PageDto<PartDto> parts = partService.findParts(filteringDto, null);

        assertTrue(parts.getResults().size() == 1);
        assertFalse(parts.getResults().get(0).isDeletable());
    }

    @Test
    public void getAllPartWithDeletableFalse_ok_PhysicalPart() throws FunctionalException {
        Part part = partService.createPart(partCreationDto);
        dataSetInitializer.createPhysicalPart(physicalPart -> physicalPart.setPart(part));
        PartFilteringDto filteringDto = new PartFilteringDto();
        filteringDto.setPage(FIRST_PAGE);
        filteringDto.setSize(SIZE_BY_DEFAULT);
        filteringDto.setPartNumber(partCreationDto.getPartNumber());
        PageDto<PartDto> parts = partService.findParts(filteringDto, null);

        assertTrue(parts.getResults().size() == 1);
        assertFalse(parts.getResults().get(0).isDeletable());
    }

    @Test
    public void testOrderFunctionalArea() throws FunctionalException {
        Part part = partService.createPart(partCreationDto);
        List<String> areaNumberList = Arrays.asList("24", "1", "2", "11", "5", "22" , "3");
        List<String> orderedAreaNumberList = Arrays.asList("1", "11", "2", "22", "24", "3" , "5");

        runInTransaction(() -> {
            part.addFunctionalAreas(dataSetInitializer.createFunctionalArea(functionalArea -> functionalArea.setAreaNumber(areaNumberList.get(0))));
            part.addFunctionalAreas(dataSetInitializer.createFunctionalArea(functionalArea -> functionalArea.setAreaNumber(areaNumberList.get(1))));
            part.addFunctionalAreas(dataSetInitializer.createFunctionalArea(functionalArea -> functionalArea.setAreaNumber(areaNumberList.get(2))));
            part.addFunctionalAreas(dataSetInitializer.createFunctionalArea(functionalArea -> functionalArea.setAreaNumber(areaNumberList.get(3))));
            part.addFunctionalAreas(dataSetInitializer.createFunctionalArea(functionalArea -> functionalArea.setAreaNumber(areaNumberList.get(4))));
            part.addFunctionalAreas(dataSetInitializer.createFunctionalArea(functionalArea -> functionalArea.setAreaNumber(areaNumberList.get(5))));
            part.addFunctionalAreas(dataSetInitializer.createFunctionalArea(functionalArea -> functionalArea.setAreaNumber(areaNumberList.get(6))));

            AtomicInteger cpt = new AtomicInteger();
            part.getFunctionalAreas().forEach(functionalArea -> {
                assertThat(functionalArea.getAreaNumber(), equalTo(orderedAreaNumberList.get(cpt.get())));
                cpt.getAndIncrement();
            });
        });



    }




}
