package com.airbus.retex.model.basic;

public interface IIdentifiedVersionModel<T> extends IModel {
    T getTechnicalId();
    void setTechnicalId(T id);

    Long getNaturalId();
}
