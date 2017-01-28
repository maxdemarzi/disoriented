package com.maxdemarzi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class DisorientedTest {
    private Disoriented db;

    @Before
    public void setup() throws IOException {
        Disoriented.init();
        db = Disoriented.getInstance();
        db.clear();
    }

    @Test
    public void shouldAddRelationship() {
        boolean created = db.addRelationship("FRIENDS", "one", "two");
        Assert.assertTrue(created);
    }

    @Test
    public void shouldAddRelationshipWithProperty() {
        db.addRelationship("FRIENDS", "one", "two", 3);
        Assert.assertEquals(1, db.getRelationshipTypeAttributes("FRIENDS").get("FRIENDS"));
    }

    @Test
    public void shouldAddRelationshipWithProperties() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("stars", 5);
        db.addRelationship("RATED", "one", "two", properties);
        HashMap<String, Object> actual = db.getRelationship("RATED", "one", "two");
        Assert.assertEquals(properties.get("stars"), actual.get("stars"));
        Assert.assertEquals(1, db.getRelationshipTypeAttributes("RATED").get("RATED"));
    }

    @Test
    public void shouldRemoveRelationship() {
        db.addRelationship("HATES", "one", "two");
        Assert.assertEquals(1, db.getRelationshipTypeAttributes("HATES").get("HATES"));
        db.removeRelationship("HATES", "one", "two");
        Assert.assertEquals(0, db.getRelationshipTypeAttributes("HATES").get("HATES"));
    }

    @Test
    public void shouldGetNodeNotThere() {
        Assert.assertEquals(null, db.getNode("NotThere"));
    }


    @Test
    public void shouldAddNode() {
        boolean created = db.addNode("User", "key");
        Assert.assertTrue(created);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("_id", "key");
        properties.put("_type", "User");
        Assert.assertEquals(properties, db.getNode("key"));
    }

    @Test
    public void shouldNotAddNodeAlreadyThere() {
        boolean created = db.addNode("User", "key");
        Assert.assertTrue(created);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("_id", "key");
        properties.put("_type", "User");
        Assert.assertEquals(properties, db.getNode("key"));
        created = db.addNode("User", "key");
        Assert.assertFalse(created);
    }

    @Test
    public void shouldAddNodeWithProperties() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "max");
        properties.put("email", "maxdemarzi@hotmail.com");
        boolean created = db.addNode("User", "max", properties);
        properties.put("_id", "max");
        properties.put("_type", "User");
        Assert.assertTrue(created);
        Assert.assertEquals(properties, db.getNode("max"));
    }

    @Test
    public void shouldAddNodeWithSimpleProperty() {
        boolean created = db.addNode("User", "simple", 5);
        Assert.assertTrue(created);
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("_id", "simple");
        properties.put("_type", "User");
        properties.put("value", 5);

        Assert.assertEquals(properties, db.getNode("simple"));
    }

    @Test
    public void shouldNotRemoveNodeNotThere() {
        boolean result = db.removeNode("not_there");
        Assert.assertFalse(result);
    }


    @Test
    public void shouldRemoveNode() {
        boolean result = db.addNode("User", "simple", 5);
        Assert.assertTrue(result);
        result = db.removeNode("simple");
        Assert.assertTrue(result);
    }

    @Test
    public void shouldRemoveNodeRelationships() {
        db.addNode("User", "one");
        db.addNode("User", "two");
        db.addNode("User", "three");
        db.addRelationship("FRIENDS", "one", "two", 9);
        db.addRelationship("FRIENDS", "three", "one", 10);

        boolean result = db.removeNode("one");
        Assert.assertTrue(result);
        Assert.assertEquals(0, db.getRelationshipTypeAttributes("FRIENDS").get("FRIENDS"));

        Assert.assertEquals(null, db.getRelationship("FRIENDS", "one", "two"));
        Assert.assertEquals(null, db.getRelationship("FRIENDS", "three", "one"));
    }

    @Test
    public void shouldAddNodeWithObjectProperties() {
        HashMap<String, Object> address = new HashMap<>();
        address.put("Country", "USA");
        address.put("Zip", "60601");
        address.put("State", "TX");
        address.put("City", "Chicago");
        address.put("Line1 ", "175 N. Harbor Dr.");
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "max");
        properties.put("email", "maxdemarzi@hotmail.com");
        properties.put("address", address);
        boolean created = db.addNode("User", "complex", properties);
        Assert.assertTrue(created);
        properties.put("_id", "complex");
        properties.put("_type", "User");
        Assert.assertEquals(properties, db.getNode("complex"));
    }

    @Test
    public void shouldGetRelationshipTypes() {
        db.addRelationship("FOLLOWS", "one", "two");
        List<String> types = db.getRelationshipTypes();
        Assert.assertEquals(new ArrayList<String>(){{add("FOLLOWS");}}, types);
    }

    @Test
    public void shouldGetNodeOutgoingRelationshipNodeIds() {
        db.addNode("User", "one");
        db.addNode("User", "two");
        db.addNode("User", "three");
        db.addRelationship("FRIENDS", "one", "two");
        db.addRelationship("FRIENDS", "one", "three");
        HashSet<String> actual = db.getOutgoingRelationshipNodeIds("FRIENDS", "one");
        Assert.assertEquals(new HashSet<String>() {{ add("two"); add("three");}}, actual);
    }

    @Test
    public void shouldGetNodeIncomingRelationshipNodeIds() {
        db.addNode("User", "one");
        db.addNode("User", "two");
        db.addNode("User", "three");
        db.addRelationship("FRIENDS", "one", "two");
        db.addRelationship("FRIENDS", "one", "three");
        HashSet<String> actual = db.getIncomingRelationshipNodeIds("FRIENDS", "two");
        Assert.assertEquals(new HashSet<String>() {{ add("one"); }}, actual);
    }

    @Test
    public void shouldGetNodeOutgoingRelationshipNodes() {
        db.addNode("User", "one", 1);
        db.addNode("User", "two", "node two");

        HashMap<String, Object> node3props = new HashMap<> ();
        node3props.put("property1", 3);
        db.addNode("User", "three", node3props);

        db.addRelationship("FRIENDS", "one", "two");
        db.addRelationship("FRIENDS", "one", "three");
        Set<Object> actual = db.getOutgoingRelationshipNodes("FRIENDS", "one");
        node3props.put("_id", "three");
        node3props.put("_type", "User");

        Set<Object> expected = new HashSet<Object>() {{
            add( new HashMap<String, Object>() {{
                put("properties", new HashMap<String, Object>(){{
                    put("_id", "two");
                    put("_type", "User");
                    put("value", "node two");
                }});
            }});
            add( new HashMap<String, Object>() {{
                put("properties", node3props);
            }});
        }};
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeIncomingRelationshipNodes() {
        db.addNode("User", "one", 1);
        db.addNode("User", "two", "node two");

        HashMap<String, Object> node3props = new HashMap<> ();
        node3props.put("property1", 3);
        db.addNode("User", "three", node3props);

        db.addRelationship("FRIENDS", "two", "one");
        db.addRelationship("FRIENDS", "three", "one");
        Set<Object> actual = db.getIncomingRelationshipNodes("FRIENDS", "one");
        node3props.put("_id", "three");
        node3props.put("_type", "User");
        Set<Object> expected = new HashSet<Object>() {{
            add( new HashMap<String, Object>() {{

                put("properties", new HashMap<String, Object>(){{
                    put("_id", "two");
                    put("_type", "User");
                    put("value", "node two");}});
            }});
            add( new HashMap<String, Object>() {{
                put("properties", node3props);
            }});
        }};
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldGetNodeDegree() {
        db.addNode("User", "four");
        db.addNode("User", "five");
        db.addNode("User", "six");
        db.addRelationship("FRIENDS", "four", "five");
        db.addRelationship("ENEMIES", "four", "six");
        Integer actual = db.getNodeDegree("four");
        Assert.assertEquals(Integer.valueOf(2), actual);
    }

    @Test
    public void shouldGetNodeIncomingDegree() {
        db.addNode("User", "four");
        db.addNode("User", "five");
        db.addNode("User", "six");
        db.addRelationship("FRIENDS", "four", "five");
        db.addRelationship("ENEMIES", "six", "four");
        Integer actual = db.getNodeDegree("four", "out");
        Assert.assertEquals(Integer.valueOf(1), actual);
    }

    @Test
    public void shouldGetNodeOutgoingDegree() {
        db.addNode("User", "four");
        db.addNode("User", "five");
        db.addNode("User", "six");
        db.addRelationship("FRIENDS", "four", "five");
        db.addRelationship("ENEMIES", "six", "four");
        Integer actual = db.getNodeDegree("four", "out");
        Assert.assertEquals(Integer.valueOf(1), actual);
    }

    @Test
    public void shouldGetNodeIncomingTypedDegree() {
        db.addNode("User", "four");
        db.addNode("User", "five");
        db.addNode("User", "six");
        db.addRelationship("FRIENDS", "five", "four");
        db.addRelationship("ENEMIES", "six", "four");
        Integer actual = db.getNodeDegree("four", "in", new ArrayList<String>(){{add("ENEMIES");}});
        Assert.assertEquals(Integer.valueOf(1), actual);
    }

    @Test
    public void shouldGetNodeOutgoingTypedDegree() {
        db.addNode("User", "four");
        db.addNode("User", "five");
        db.addNode("User", "six");
        db.addRelationship("FRIENDS", "four", "five");
        db.addRelationship("ENEMIES", "four", "six");
        Integer actual = db.getNodeDegree("four", "out", new ArrayList<String>(){{add("ENEMIES");}});
        Assert.assertEquals(Integer.valueOf(1), actual);
    }
}
