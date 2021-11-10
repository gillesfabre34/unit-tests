package com.airbus.retex.controller;


import com.airbus.retex.service.language.ILanguageService;
import com.airbus.retex.utils.ConstantUrl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping()
@Api(value = "Language", tags = {"Languages"})
public class LanguageController {

    @Autowired
    private ILanguageService languageService;

    @ApiOperation("Get the list languages")
    @GetMapping(ConstantUrl.API_LANGUAGE)
    public ResponseEntity<List<String>> getAllLanguages(Authentication authentication) {
        return ResponseEntity.ok().headers(new HttpHeaders()).body(languageService.getAllLanguages());
    }

}
