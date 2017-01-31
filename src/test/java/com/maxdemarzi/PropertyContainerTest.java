package com.maxdemarzi;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PropertyContainerTest {

    @Test
    public void shouldBeEquals() throws Exception {
        PropertyContainer propertyContainer = new PropertyContainer("myType", "max", new HashMap<>());
        PropertyContainer propertyContainer2 = new PropertyContainer("myType", "max", new HashMap<>());
        assertTrue(propertyContainer.equals(propertyContainer2));
    }

    @Test
    public void shouldReturnLowerCase() throws Exception {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("full_name", "Max De Marzi");
        PropertyContainer propertyContainer = new PropertyContainer("myType", "max", properties);
        assertEquals("max de marzi",propertyContainer.getLowerCaseStringProperty("full_name"));
    }

    @Test
    public void shouldHaveSameHashCode() throws Exception {
        PropertyContainer propertyContainer = new PropertyContainer("myType", "max", new HashMap<>());
        PropertyContainer propertyContainer2 = new PropertyContainer("myType", "max", new HashMap<>());
        assertEquals(propertyContainer.hashCode(), propertyContainer2.hashCode());
    }
}
