package com.airbus.retex.service.impl.language;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.model.common.Language;
import com.airbus.retex.service.language.ILanguageService;

@Service
@Transactional(rollbackFor = Exception.class)
public class LanguageServiceImpl implements ILanguageService {

    @Override
    public List<String> getAllLanguages() {
		return EnumSet.allOf(Language.class).stream().map(Language::name).collect(Collectors.toList());
    }
}
