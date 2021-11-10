package com.airbus.retex.helper;

import com.airbus.retex.business.dto.request.RequestCreationDto;
import com.airbus.retex.business.dto.request.RequestDetailsDto;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.request.RequestFieldsEnum;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class DtoHelper {

    /**
     * @return a valid RequestCreationDto
     */
    public static RequestCreationDto generateValidCreationDto(){
        RequestCreationDto item = new RequestCreationDto();

        HashMap<RequestFieldsEnum, String> en = new HashMap<>();
        en.put(RequestFieldsEnum.name, "Request XY");
        en.put(RequestFieldsEnum.description, "Test description");

        HashMap<RequestFieldsEnum, String> fr = new HashMap<>();
        fr.put(RequestFieldsEnum.name, "Requete XY");
        fr.put(RequestFieldsEnum.description, "Description test");

        Map<Language, Map<RequestFieldsEnum, String>> translations = new HashMap<>();
        translations.put(Language.EN, en);
        translations.put(Language.FR, fr);
        item.setDueDate(LocalDate.now().plusDays(1));
        item.setTranslatedFields(translations);
        return item;
    }

    /**
     * @return a valid RequestDetailDto
     */
    public static RequestDetailsDto generateValidDetailDto(){
        RequestDetailsDto request = new RequestDetailsDto();
        return request;
    }
}
