package com.itrustcambodia.pluggable.validation.controller;

import java.io.Serializable;

public class LinkController implements Comparable<LinkController>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 6813010136998034798L;

    private String method;

    private String label;

    private double order;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getOrder() {
        return order;
    }

    public void setOrder(double order) {
        this.order = order;
    }

    @Override
    public int compareTo(LinkController o) {
        return Double.valueOf(this.order).compareTo(o.order);
    }

}
