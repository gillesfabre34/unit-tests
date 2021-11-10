package com.airbus.retex.service.impl.drt;

import com.airbus.retex.business.dto.inspection.InspectionQCheckValueDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.common.EnumStatus;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.service.drt.IDrtQCheckService;
import com.airbus.retex.service.drt.IDrtService;
import com.airbus.retex.service.impl.drt.mapper.DrtQCheckMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(rollbackFor = Exception.class)
public class DrtQCheckServiceImpl implements IDrtQCheckService {

    @Autowired
    private IDrtService drtService;
    @Autowired
    private DrtQCheckMapper drtQCheckMapper;

    @Override
    public void putInspectionQCheck(Long drtId, Long operationId, Long taskId, List<InspectionQCheckValueDto> inspectionQCheckValueDto, Long userId, boolean validate) throws FunctionalException {
        Drt drt = drtService.findDrtById(drtId);
        isDrtStatusInProgress(drt);
        drtQCheckMapper.updateQcheckDrt(drt, operationId, taskId, inspectionQCheckValueDto, validate);
    }

    private void isDrtStatusInProgress(Drt drt) throws FunctionalException {
        if(!drt.getStatus().equals(EnumStatus.IN_PROGRESS)) {
            throw new FunctionalException("retex.error.drt.wrong.status.update");
        }
    }

}



