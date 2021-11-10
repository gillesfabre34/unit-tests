package com.airbus.retex.exception;

import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.NotFoundException;
import com.airbus.retex.model.ata.ATA;
import com.airbus.retex.persistence.ata.ATARepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Transactional(rollbackFor = Exception.class)
public class ExceptionTestController {

    public static final String TEST_ATA_CODE = "99999";

    @Autowired
    private ATARepository ataRepository;

    @GetMapping("/test/throw-not-found")
    public void throwNotFound() throws NotFoundException {
        throw new NotFoundException("retex.user.not.found.label");
    }

    @GetMapping("/test/throw-functional")
    public void throwFunctional() throws FunctionalException {
        throw new FunctionalException("retex.role.error.label");
    }

    @GetMapping("/test/throw-error")
    public void throwError() {
        throw new Error();
    }

    @GetMapping("/test/save-and-throw-error")
    public void throwErrorWithSaveAta() {
        saveAta();

        throw new Error();
    }

    @GetMapping("/test/save-and-throw-functional")
    public void throwFunctionalWithSaveAta() throws FunctionalException {
        saveAta();

        throw new FunctionalException();
    }

    @GetMapping("/test/save")
    public void save() throws NotFoundException {
        saveAta();
    }

    private void saveAta() {
        ATA newAta = new ATA();
        newAta.setCode(TEST_ATA_CODE);
        ataRepository.save(newAta);

        ataRepository.getAllATACode();//Force flush
    }
}
