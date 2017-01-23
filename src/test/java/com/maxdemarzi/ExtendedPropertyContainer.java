package com.maxdemarzi;

import com.googlecode.cqengine.attribute.Attribute;

import java.util.HashMap;

import static com.googlecode.cqengine.query.QueryFactory.attribute;

public class ExtendedPropertyContainer extends PropertyContainer {
    public ExtendedPropertyContainer(String id, HashMap<String, Object> properties) {
        super(id, properties);
    }

    public static final Attribute<PropertyContainer, String> NAME = attribute("name", pc -> (String)pc.getProperty("name") );

}
