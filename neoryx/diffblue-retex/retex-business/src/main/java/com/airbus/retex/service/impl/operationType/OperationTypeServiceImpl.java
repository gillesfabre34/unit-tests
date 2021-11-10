package com.airbus.retex.service.impl.operationType;

import com.airbus.retex.business.converter.DtoConverter;
import com.airbus.retex.business.dto.operationType.OperationTypeDto;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.persistence.operationType.OperationTypeRepository;
import com.airbus.retex.service.operationType.IOperationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class OperationTypeServiceImpl implements IOperationTypeService {


    @Autowired
    private OperationTypeRepository operationTypeRepository;
    @Autowired
    private DtoConverter dtoConverter;

    @Override
    public List<OperationTypeDto> findOperationTypes() {

        List<OperationType> operationTypeList = operationTypeRepository.findAll();
        return dtoConverter.convert(operationTypeList, OperationTypeDto::new);

    }


}
