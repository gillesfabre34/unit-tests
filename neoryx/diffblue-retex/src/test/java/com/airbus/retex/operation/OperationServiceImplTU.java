package com.airbus.retex.operation;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.operation.ManageOperationDto;
import com.airbus.retex.business.dto.operation.OperationCreationDto;
import com.airbus.retex.business.dto.operation.OperationDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.persistence.operation.OperationRepository;
import com.airbus.retex.service.impl.operation.OperationServiceImpl;
import com.airbus.retex.service.impl.operation.mapper.OperationMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class OperationServiceImplTU {

    @Mock
    private OperationRepository operationRepository;

    @InjectMocks
    private OperationServiceImpl operationService;

    @Spy
    private OperationMapperImpl operationMapper;

    @Spy
    private DtoConverter dtoConverter;

    private Set<Operation> operationList;

    @BeforeEach
    public void before() {
        operationList = new HashSet<>();
    }

    @Test
    public void checkOrderUpdateOrderOne() throws FunctionalException {
        initOperationList(Arrays.asList(2,4,10));

        operationService.cleanOrderNumber(operationList);

        List<Operation> mainList = new ArrayList<>();
        mainList.addAll(operationList);
        mainList.sort(Comparator.comparing(Operation::getOrderNumber));
        assertThat(mainList.get(0).getOrderNumber(), equalTo(1));
        assertThat(mainList.get(1).getOrderNumber(), equalTo(2));
        assertThat(mainList.get(2).getOrderNumber(), equalTo(3));
    }

    @Test
    public void checkOrderUpdateOrderWithSort() throws FunctionalException {
        initOperationList(Arrays.asList(5,3,1));

        operationService.cleanOrderNumber(operationList);

        List<Operation> mainList = new ArrayList<>();
        mainList.addAll(operationList);
        assertThat(mainList.get(0).getOrderNumber(), equalTo(1));
        assertThat(mainList.get(1).getOrderNumber(), equalTo(2));
        assertThat(mainList.get(2).getOrderNumber(), equalTo(3));
    }

    @Test
    public void checkOrderUpdateOrderOnCreation() throws FunctionalException {
        ManageOperationDto manageOperationDto = new ManageOperationDto();
        manageOperationDto.setAddedOperations(initOperationCreationList(Arrays.asList(1,3,3)));

        assertThrows(FunctionalException.class, () -> {
            operationService.checkOrderNumber(manageOperationDto);
        });

    }

    @Test
    public void checkOrderUpdateOrderErrorOnUpdate() throws FunctionalException {
        ManageOperationDto manageOperationDto = new ManageOperationDto();
        manageOperationDto.setOperations(initOperationDtoList(Arrays.asList(1,3,3)));

        assertThrows(FunctionalException.class, () -> {
            operationService.checkOrderNumber(manageOperationDto);
        });

    }

    @Test
    public void checkOrderUpdateOrderErrorOnBoth() throws FunctionalException {
        ManageOperationDto manageOperationDto = new ManageOperationDto();
        manageOperationDto.setAddedOperations(initOperationCreationList(Arrays.asList(1,2,3)));
        manageOperationDto.setOperations(initOperationDtoList(Arrays.asList(4,3,7)));

        assertThrows(FunctionalException.class, () -> {
            operationService.checkOrderNumber(manageOperationDto);
        });

    }

    private void initOperationList(List<Integer> orderNumberList) {
        for (Integer orderNumber : orderNumberList) {
            Operation operation = new Operation();
            operation.setOrderNumber(orderNumber);
            operationList.add(operation);
        }
    }


    private List<OperationDto> initOperationDtoList(List<Integer> orderNumberList) {
        List<OperationDto> operationDtos = new ArrayList<>();
        for (Integer orderNumber : orderNumberList) {
            OperationDto operation = new OperationDto();
            operation.setOrderNumber(orderNumber);
            operationDtos.add(operation);
        }
        return operationDtos;
    }


    private List<OperationCreationDto> initOperationCreationList(List<Integer> orderNumberList) {
        List<OperationCreationDto> operationCreationDtos = new ArrayList<>();
        for (Integer orderNumber : orderNumberList) {
            OperationCreationDto operation = new OperationCreationDto();
            operation.setOrderNumber(orderNumber);
            operation.setOperationTypeId(1L);

            operationCreationDtos.add(operation);
        }
        return operationCreationDtos;
    }

}
