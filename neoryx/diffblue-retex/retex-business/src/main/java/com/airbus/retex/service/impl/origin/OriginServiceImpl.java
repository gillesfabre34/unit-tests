package com.airbus.retex.service.impl.origin;

import com.airbus.retex.business.dto.origin.OriginDto;
import com.airbus.retex.model.origin.Origin;
import com.airbus.retex.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class OriginServiceImpl extends BaseService<OriginDto, Origin> {
    public OriginServiceImpl() {
        super(OriginDto::new);
    }
}
