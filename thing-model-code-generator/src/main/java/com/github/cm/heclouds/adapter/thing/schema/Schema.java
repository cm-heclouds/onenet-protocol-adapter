package com.github.cm.heclouds.adapter.thing.schema;

import com.github.cm.heclouds.adapter.thing.schema.services.Service;

import java.util.List;

/**
 * 物模型Schema
 *
 */
public class Schema {

    private List<Property> properties;
    private List<Event> events;
    private List<Service> services;

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }
}
