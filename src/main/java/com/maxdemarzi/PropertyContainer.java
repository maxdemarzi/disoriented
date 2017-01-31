package com.maxdemarzi;

import com.googlecode.cqengine.attribute.Attribute;

import java.util.HashMap;
import java.util.Objects;

import static com.googlecode.cqengine.query.QueryFactory.attribute;

public class PropertyContainer {

    private final String type;
    private final String id;
    private final HashMap<String, Object> properties;

    public PropertyContainer(String type, String id, HashMap<String, Object> properties) {
        this.id = id;
        this.type = type;
        this.properties = properties;
    }

    public static final Attribute<PropertyContainer, String> TYPE = attribute("_type", PropertyContainer::getType);
    public static final Attribute<PropertyContainer, String> ID = attribute("_id", PropertyContainer::getId);

    public String getId() {
        return id;
    }
    public String getType() {
        return type;
    }

    public HashMap<String, Object> getProperties() {
        HashMap<String, Object> node = new HashMap<>();
        node.putAll(properties);
        node.put("_id", id);
        node.put("_type", type);
        return node;
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public Integer getIntegerProperty(String key) {
        return (Integer)properties.get(key);
    }

    public Long getLongProperty(String key) {
        return (Long) properties.get(key);
    }

    public String getStringProperty(String key) {
        return (String) properties.get(key);
    }

    public Float getFloatProperty(String key) {
        return (Float)properties.get(key);
    }

    public Double getDoubleProperty(String key) {
        return (Double) properties.get(key);
    }

    public String getLowerCaseStringProperty(String key) {
        String property = (String)properties.get(key);
        if (property == null) {
            return null;
        } else {
            return property.toLowerCase();
        }
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