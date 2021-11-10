package com.airbus.retex.dataset;

import com.airbus.retex.model.childrequest.ChildRequest;
import com.airbus.retex.model.childrequest.PhysicalPart;
import com.airbus.retex.model.request.Request;

public class RequestDataset {

    public RoutingDataset routingDataset;

    public Request parentRequest;
    public ChildRequest childRequest;
    public PhysicalPart physicalPart;

}
