package com.airbus.retex.service.language;

import java.util.List;

public interface ILanguageService {
    /**
     * get languages list
     *
     * @return
     */
   // @PreAuthorize("hasRole('ADMIN_FEATURE:WRITE')")
    List<String> getAllLanguages();
}
