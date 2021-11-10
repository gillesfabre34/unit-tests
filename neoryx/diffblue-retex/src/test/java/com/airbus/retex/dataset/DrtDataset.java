package com.airbus.retex.dataset;

import com.airbus.retex.model.control.ControlRoutingComponent;
import com.airbus.retex.model.control.ControlTodoList;
import com.airbus.retex.model.control.ControlVisual;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.drt.DrtOperationStatusFunctionalArea;
import com.airbus.retex.model.drt.DrtOperationStatusTodoList;
import com.airbus.retex.model.drt.DrtPictures;
import com.airbus.retex.model.filtering.Filtering;
import com.airbus.retex.model.qcheck.QcheckRoutingComponent;

public class DrtDataset {


    public RequestDataset requestDataset;

    public Drt drt;
    public Filtering filtering;

    //CONTROL
    public ControlRoutingComponent controlDimensional;
    public ControlRoutingComponent controlLaboratory;
    public ControlRoutingComponent controlTriDimensional;

    public ControlVisual controlVisual;

    public ControlTodoList controlPremiminary;
    public ControlTodoList controlClosure;

    //CONTROL
    public DrtPictures drtPicturesDimensional;
    public DrtPictures drtPicturesLaboratory;
    public DrtPictures drtPicturesTriDimensional;

    //OPERATION STATUS
    public DrtOperationStatusFunctionalArea drtOperationStatusDimensional;
    public DrtOperationStatusFunctionalArea drtOperationStatusLaboratory;
    public DrtOperationStatusFunctionalArea drtOperationStatusTriDimensional;
    public DrtOperationStatusFunctionalArea drtOperationStatusVisual;

    public DrtOperationStatusTodoList drtOperationStatusPreliminary;
    public DrtOperationStatusTodoList drtOperationStatusClosure;

    public QcheckRoutingComponent qcheckRoutingDimensional;
    public QcheckRoutingComponent qcheckRoutingLaboratory;
    public QcheckRoutingComponent qcheckRoutingTriDimensional;


}
