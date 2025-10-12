package com.sleekydz86.service.healthcare.service;

import org.springframework.stereotype.Component;

@Component
public class ChatgptProperties {

    private String model;
    private double temperature;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
