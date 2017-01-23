package com.maxdemarzi;

import com.googlecode.cqengine.attribute.Attribute;

import java.util.HashMap;
import java.util.Objects;

import static com.googlecode.cqengine.query.QueryFactory.attribute;

public class PropertyContainer {

    private final String id;
    private final HashMap<String, Object> properties;

    public PropertyContainer(String id, HashMap<String, Object> properties) {
        this.id = id;
        this.properties = properties;
    }

    public static final Attribute<PropertyContainer, String> ID = attribute("id", PropertyContainer::getId);

    public String getId() {
        return id;
    }

    public HashMap<String, Object> getProperties() {
        return properties;
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public Integer getIntegerProperty(String key) {
        return (Integer)properties.get(key);
    }


    @Override
    public boolean equals(Object o) {
        // self check
        if (this == o)
            return true;
        // null check
        if (o == null)
            return false;
        // type check and cast
        if (getClass() != o.getClass())
            return false;
        PropertyContainer propertyContainer = (PropertyContainer) o;
        // field comparison
        return Objects.equals(id, propertyContainer.id)
                && Objects.equals(properties, propertyContainer.properties);
    }

    @Override
    public int hashCode(){
        int result = 17;
        result = 31 * result + id.hashCode();
        return result;
    }
}