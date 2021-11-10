package com.airbus.retex.service.impl.ata;

import com.airbus.retex.persistence.ata.ATARepository;
import com.airbus.retex.service.ata.IATAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ATAServiceImpl implements IATAService {

    @Autowired
    private ATARepository ataRepository;

    public List<String> getAllATACode() {
        return ataRepository.getAllATACode();
    }
}