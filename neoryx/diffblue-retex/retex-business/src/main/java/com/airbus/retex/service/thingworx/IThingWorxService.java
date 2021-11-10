package com.airbus.retex.service.thingworx;


import com.airbus.retex.business.dto.step.StepThingworxDto;
import com.airbus.retex.business.dto.thingWorx.RedirectURLDto;
import com.airbus.retex.business.exception.FunctionalException;
import com.airbus.retex.model.user.User;

import java.io.IOException;
import java.util.Locale;

public interface IThingWorxService {

    /**
     * Get url of thingworx
     * Used from front end to access thingworx website
     * @return
     * @throws FunctionalException
     */
    RedirectURLDto getThingworxMainUrl(User user, Long routingComponentNaturalId, Long routingComponentVersionId, Long drtId, Long operationId, Long taskId, Locale locale) throws FunctionalException, IOException;


    /**
     * This endpoint will be used from thing worx to post data (measures) to Retex server
     * When data is received from thingworx it will be pushed to Frond end across websocket push
     *

     * @return
     */
    StepThingworxDto postMeasures(StepThingworxDto thingworxData) throws FunctionalException, IOException;
}
