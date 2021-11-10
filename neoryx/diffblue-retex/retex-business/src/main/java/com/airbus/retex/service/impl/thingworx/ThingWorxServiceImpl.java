package com.airbus.retex.service.impl.thingworx;

import com.airbus.retex.business.dto.step.StepCustomThingworxDto;
import com.airbus.retex.business.dto.step.StepThingworxDto;
import com.airbus.retex.business.dto.thingWorx.RedirectURLDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.business.exception.TechnicalError;
import com.airbus.retex.config.RetexConfig;
import com.airbus.retex.model.common.Language;
import com.airbus.retex.model.common.StepType;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.messaging.WebsocketIdentifier;
import com.airbus.retex.model.mesureUnit.MeasureUnit;
import com.airbus.retex.model.operation.Operation;
import com.airbus.retex.model.operation.OperationFunctionalArea;
import com.airbus.retex.model.post.PostFieldsEnum;
import com.airbus.retex.model.routingComponent.RoutingComponentIndex;
import com.airbus.retex.model.step.StepActivation;
import com.airbus.retex.model.user.User;
import com.airbus.retex.persistence.messaging.WebsocketIdentifierRepository;
import com.airbus.retex.persistence.routingComponent.RoutingComponentIndexRepository;
import com.airbus.retex.service.drt.IDrtService;
import com.airbus.retex.service.impl.messaging.WebsocketUrl;
import com.airbus.retex.service.impl.messaging.service.PushService;
import com.airbus.retex.service.impl.thingworx.internal.PostUrlDto;
import com.airbus.retex.service.impl.thingworx.internal.StepUrlDto;
import com.airbus.retex.service.impl.translate.TranslateServiceImpl;
import com.airbus.retex.service.thingworx.IThingWorxService;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ThingWorxServiceImpl implements IThingWorxService {
    private static final String MSG = "-----------------------------------------------------------";
    private static final String THING_WORX_ERROR_API_KEY = "Error : Thingworx Api key is empty";
    private static final String THINGWORX_USER_NOT_SUBSCRIBED_PUSH_SERVICE = "retex.error.thingworx.user.not.connected.push.service";


    @Autowired
    private WebsocketIdentifierRepository repository;
    @Autowired
    private PushService pushService;

    @Autowired
    private RoutingComponentIndexRepository routingComponentIndexRepository;

    @Autowired
    private RetexConfig retexConfig;
    @Autowired
    private TranslateServiceImpl translateService;
    @Autowired
    private IDrtService drtService;

    @PostConstruct
    public void postConstruct() {
        if (StringUtils.isEmpty(retexConfig.getThingworxApikey())) {
            throw new ConfigurationErrorException(THING_WORX_ERROR_API_KEY);
        }
    }

    /**
     * @param user
     * @param routingComponentNaturalId
     * @param routingComponentVersionId
     * @param locale
     * @return
     * @throws FunctionalException
     */
    @Override
    public RedirectURLDto getThingworxMainUrl(User user, Long routingComponentNaturalId, Long routingComponentVersionId, Long drtId, Long operationId, Long taskId, Locale locale) throws FunctionalException {
        RoutingComponentIndex routingComponentIndex = routingComponentIndexRepository
                .findByNaturalIdAndVersion(routingComponentNaturalId, routingComponentVersionId)
                .orElseThrow(() -> new FunctionalException("retex.error.routing.component.index.not.found"));

        RedirectURLDto redirectURLDto = new RedirectURLDto();
        redirectURLDto.setUrl(buildThingworxUrl(user, routingComponentIndex, drtId, operationId, taskId, locale));
        return redirectURLDto;
    }

    String buildThingworxUrl(User user, RoutingComponentIndex routingComponentIndex, Long drtId, Long operationId, Long taskId, Locale locale) throws FunctionalException {
        StringBuilder sBuilder = new StringBuilder();
        // set thingworx main url
        sBuilder.append(retexConfig.getThingworxUrl());

        // set the userId of the front requester (airbus logged user)
        sBuilder.append("&user=" + user.getStaffNumber());

        // context is the user id, context allows us to determine which user should receive notification
        sBuilder.append("&context=" + user.getId());

        Drt drt = drtService.findDrtById(drtId);
        Operation operation = drtService.getOperationByDrtAndOperation(drtId, operationId);

        OperationFunctionalArea operationFunctionalArea = drtService.getOperationFunctionalArea(operation, taskId);

        List<StepUrlDto> stepUrlDtos = new ArrayList<>();
        operationFunctionalArea.getStepActivations().stream()
                .filter(stepActivation -> stepActivation.getActivated() &&
                        stepActivation.getStep().getType().equals(StepType.AUTO) &&
                        stepActivation.getStep().getRoutingComponent().getNaturalId().equals(routingComponentIndex.getRoutingComponent().getNaturalId()))
                .map(StepActivation::getStep) // map to stream only on Steps
                .forEach(step -> {
                    StepUrlDto stepUrlDto = new StepUrlDto();
                    stepUrlDto.setId(step.getNaturalId());
                    stepUrlDto.setName(step.getType() + " " + step.getStepNumber());

                    List<PostUrlDto> stepPosts = new ArrayList<>();

                    step.getPosts().forEach(post -> {
                        PostUrlDto postUrlDto = new PostUrlDto();
                        postUrlDto.setId(post.getNaturalId());
                        postUrlDto.setDesignation(post.getTranslation(Language.languageFor(locale), PostFieldsEnum.designation));
                        postUrlDto.setUnitName(translateService.getFieldValue(MeasureUnit.class.getSimpleName(),
                                post.getMeasureUnitId(),
                                MeasureUnit.FIELD_NAME,
                                Language.languageFor(locale)));
                        stepPosts.add(postUrlDto);
                    });
                    stepUrlDto.setPosts(stepPosts);
                    stepUrlDtos.add(stepUrlDto);
                });

        String urlEncoded = null;
        try {
            urlEncoded = new ObjectMapper().writeValueAsString(stepUrlDtos);
            urlEncoded = URLEncoder.encode(urlEncoded, "UTF-8");
        } catch (IOException e) {
            throw new TechnicalError(e.getMessage(), e);
        }
        sBuilder.append("&steps=" + urlEncoded);

        return sBuilder.toString();
    }


    /**
     * Thingworx call this url to post data
     * We accept only request who contains context parameter
     *
     * @param thingworxData
     * @return
     * @throws FunctionalException
     * @throws IOException
     */
    @Override
    public StepThingworxDto postMeasures(StepThingworxDto thingworxData) throws FunctionalException, IOException {
        log.info("---------------- Thingworx : received data ---------------");
        log.info("Data -> " + new ObjectMapper().writeValueAsString(thingworxData));
        log.info(MSG);

        if (StringUtils.isEmpty(thingworxData.getContext())) {
            throw new FunctionalException("retex.error.thingworx.context.not.found");
        }
        // thingworx send us context which is ldap user identifier<
        Long ssoUser = Long.valueOf(thingworxData.getContext());
        log.info("ssoUser id -> " + ssoUser);

        // check if is connected to websocket
        WebsocketIdentifier sender = repository.findByUserId(ssoUser)
                .orElseThrow(() -> new FunctionalException("retex.error.user.not.subscribed.websocket"));
        log.info(MSG);
        log.info("ssoUser is connected to websocket ? " + sender.isConnected());
        log.info(MSG);

        if (sender.isConnected()) {
            // save or update data
            // TODO : c'est pas clair, faut que le front connaisse l'id de la step !!!???
            List<StepCustomThingworxDto> data = thingworxData.getSteps();
            // push data to frontend
            pushService.notify(data, WebsocketUrl.THINGWORX_QUEUE, sender.getUserId().toString());
        } else {
            log.info(MSG);
            log.info("Error : user not subscribed to websocket push");
            log.info(MSG);
            throw new FunctionalException(THINGWORX_USER_NOT_SUBSCRIBED_PUSH_SERVICE);
        }
        return thingworxData;
    }

    public static class ConfigurationErrorException extends RuntimeException {
        ConfigurationErrorException(String messages) {
            super(messages);
        }
    }
}
