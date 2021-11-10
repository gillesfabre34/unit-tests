package com.airbus.retex.service.impl.airbusEntity;

import com.airbus.retex.business.dto.airbusentity.AirbusEntityLightDto;
import com.airbus.retex.model.admin.airbus.AirbusEntity;
import com.airbus.retex.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class AirbusEntityServiceImpl extends BaseService<AirbusEntityLightDto, AirbusEntity> {
    public AirbusEntityServiceImpl() {
        super(AirbusEntityLightDto::new);
    }
}
