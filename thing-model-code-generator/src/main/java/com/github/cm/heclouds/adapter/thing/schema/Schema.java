package com.github.cm.heclouds.adapter.thing.schema;

import lombok.Data;

import java.util.List;

/**
 * 物模型Schema
 *
 */
@Data
public class Schema {

    private List<Property> properties;
    private List<Event> events;
//    private List<Service> services;
}
