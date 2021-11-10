package com.airbus.retex.service.impl.routingComponent;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airbus.retex.business.dto.functionalAreaName.FunctionalAreaNameFieldsEnum;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.damage.DamageFieldsEnum;
import com.airbus.retex.model.damage.DamageTranslation;
import com.airbus.retex.model.inspection.Inspection;
import com.airbus.retex.model.operation.OperationType;
import com.airbus.retex.model.operation.OperationTypeBehaviorEnum;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.task.TodoListName;
import com.airbus.retex.service.impl.translate.TranslateServiceImpl;
import com.airbus.retex.service.routingComponent.NameComputeService;

@Service
@Transactional(rollbackFor = Exception.class)
public class NameComputeServiceImpl implements NameComputeService {

    private static final String RETEX_TRANSLATE_GENERIC = "retex.translate.generic";

    @Autowired
    private TranslateServiceImpl translateService;


    @Autowired
    private MessageSource messageSource;

    @Override
    public void computeNames(Page<RoutingComponentIndex> pageItemRoutingComponentsIndex, Locale locale) {

        for (RoutingComponentIndex routingComponentIndex : pageItemRoutingComponentsIndex.getContent()) {
            StringBuilder tempName = new StringBuilder();

            if (null != routingComponentIndex.getRoutingComponent() && null != routingComponentIndex.getRoutingComponent().getNaturalId()){
                if(routingComponentIndex.getRoutingComponent().getOperationType().isBehavior(OperationTypeBehaviorEnum.GENERIC)) {
                    tempName.append(messageSource.getMessage(RETEX_TRANSLATE_GENERIC, null, locale));
                    tempName.append("/");
                    tempName.append(messageSource.getMessage(RETEX_TRANSLATE_GENERIC, null, locale));
                    tempName.append("/");
                    tempName.append(messageSource.getMessage("retex.translate.undefined", null, locale));
                    tempName.append("/");
                    tempName.append(messageSource.getMessage(RETEX_TRANSLATE_GENERIC, null, locale));

                } else {
                    tempName.append(translateService.getFieldValue(routingComponentIndex.getOperationType(), OperationType.FIELD_NAME, Language.languageFor(locale)));
                    tempName.append("/");
                    tempName.append(translateService.getFieldValue(routingComponentIndex.getRoutingComponent().getFunctionality(),
                            FunctionalAreaNameFieldsEnum.name.name(),
                            Language.languageFor(locale)));
                    tempName.append("/");
                    tempName.append(routingComponentIndex.getRoutingComponent().getDamage().getTranslations().stream()
                            .filter(damageTranslation -> damageTranslation.getField().equals(DamageFieldsEnum.name))
                            .filter(damageTranslation -> damageTranslation.getLanguage().equals(Language.languageFor(locale)))
                            .findFirst().orElse(new DamageTranslation()).getValue());
                    tempName.append("/");
                    tempName.append(translateService.getFieldValue(routingComponentIndex.getRoutingComponent().getInspection(),
                            Inspection.FIELD_NAME, Language.languageFor(locale)));
                }

            } else if (null != routingComponentIndex.getTodoList() && null != routingComponentIndex.getTodoList().getNaturalId()){
                tempName.append(translateService.getFieldValue(routingComponentIndex.getOperationType(), OperationType.FIELD_NAME, Language.languageFor(locale)));
                tempName.append("/");
                tempName.append(translateService.getFieldValue(routingComponentIndex.getTodoList().getTodoListName(), TodoListName.FIELD_NAME, Language.languageFor(locale)));
                tempName.append("/");
                tempName.append(messageSource.getMessage(RETEX_TRANSLATE_GENERIC, null, locale));
                tempName.append("/");
                tempName.append(translateService.getFieldValue(routingComponentIndex.getTodoList().getInspection(), Inspection.FIELD_NAME, Language.languageFor(locale)));
            } else {
                throw new IllegalStateException();
            }
            routingComponentIndex.setName(tempName.toString());

        }
    }
}
