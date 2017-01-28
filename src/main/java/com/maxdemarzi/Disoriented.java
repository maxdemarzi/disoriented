package com.maxdemarzi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.ObjectLockingIndexedCollection;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.index.unique.UniqueIndex;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.resultset.ResultSet;

import java.io.IOException;
import java.util.*;

import static com.googlecode.cqengine.query.QueryFactory.equal;

public class Disoriented {

    private static IndexedCollection<PropertyContainer> nodes = new ObjectLockingIndexedCollection<>();
    private static IndexedCollection<PropertyContainer> relationships = new ObjectLockingIndexedCollection<>();
    private static HashMap<String, ReversibleMultiMap<String, String>> related;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static Disoriented instance;
    public static Disoriented init() {
        if (instance == null) {
            synchronized (Disoriented.class) {
                if (instance == null){
                    instance = new Disoriented();
                }
            }
        }
        return instance;
    }

    public static Disoriented getInstance() {
        return instance;
    }

    public boolean isAvailable() {
        return true;
    }

    public void clear() {
        nodes.clear();
        relationships.clear();
        related = new HashMap<>();
    }

    private Disoriented() {
        nodes.addIndex(UniqueIndex.onAttribute(PropertyContainer.ID));
        nodes.addIndex(HashIndex.onAttribute(PropertyContainer.TYPE));
        relationships.addIndex(UniqueIndex.onAttribute(PropertyContainer.ID));
        relationships.addIndex(HashIndex.onAttribute(PropertyContainer.TYPE));
        related = new HashMap<>();
    }

    public HashMap<String, Object> getRelationshipTypeAttributes(String type) {
        HashMap<String, Object> attributes = new HashMap<>();
        if (related.containsKey(type)) {
            attributes.put(type, related.get(type).size());
        }
        return attributes;
    }

    public List<String> getRelationshipTypes() {
        return new ArrayList<>(related.keySet());
    }

    public boolean addNode (String type, String id) {
        final Query<PropertyContainer> query = equal(PropertyContainer.ID, id);
        if (nodes.retrieve(query).isNotEmpty()) {
            return false;
        } else {
            nodes.add(new PropertyContainer(type, id, new HashMap<>()));
        }
        return true;

    }

    public boolean addNode (String type, String id, String properties)  {
        final Query<PropertyContainer> query = equal(PropertyContainer.ID, id);
         if (nodes.retrieve(query).isNotEmpty()) {
            return false;
        } else {
            try {
                nodes.add(new PropertyContainer(type, id, mapper.readValue(properties, HashMap.class)));
            } catch (IOException e) {
                HashMap<String, Object> value = new HashMap<>();
                value.put("value", properties);
                nodes.add(new PropertyContainer(type, id, value));
            }
            return true;
        }
    }

    public boolean addNode (String type, String id, HashMap properties)  {
        final Query<PropertyContainer> query = equal(PropertyContainer.ID, id);
        if (nodes.retrieve(query).isNotEmpty()) {
        return false;
        } else {
             nodes.add(new PropertyContainer(type, id, properties));
            return true;
        }
    }

    public boolean addNode (String type, String id, Object value)  {
        final Query<PropertyContainer> query = equal(PropertyContainer.ID, id);
        if (nodes.retrieve(query).isNotEmpty()) {
            return false;
        } else {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("value", value);
            nodes.add(new PropertyContainer(type, id, properties));
            return true;
        }
    }

    public boolean updateNode (String id, String value)  {
         final Query<PropertyContainer> query = equal(PropertyContainer.ID, id);
         ResultSet<PropertyContainer> results = nodes.retrieve(query);
         if (results.isNotEmpty()) {
             PropertyContainer node = results.uniqueResult();
             nodes.remove(node);
             try {
                nodes.add(new PropertyContainer(node.getType(), id, mapper.readValue(value, HashMap.class)));
            } catch (IOException e) {
                HashMap<String, Object> properties = new HashMap<>();
                properties.put("value", value);
                nodes.add(new PropertyContainer(node.getType(), id, properties));
            }
            return true;

        } else {
            return false;
        }
    }

    public boolean updateNode (String id, HashMap<String, Object> properties)  {
         final Query<PropertyContainer> query = equal(PropertyContainer.ID, id);
         ResultSet<PropertyContainer> results = nodes.retrieve(query);
         if (nodes.retrieve(query).isNotEmpty()) {
             PropertyContainer node = results.uniqueResult();
             nodes.remove(node);
             nodes.add(new PropertyContainer(node.getType(), id, properties));
            return true;
        } else {
            return false;
        }
    }

    public Iterator<PropertyContainer> getAllNodes(){
        return nodes.iterator();
    }

    public HashMap<String, Object> getNode(String id) {
        final Query<PropertyContainer> query = equal(PropertyContainer.ID, id);
        ResultSet<PropertyContainer> results = nodes.retrieve(query);
        if (results.isNotEmpty()) {
            return results.uniqueResult().getProperties();
        } else {
            return null;
        }
    }

    public boolean removeNode(String id) {
        Query<PropertyContainer> query = equal(PropertyContainer.ID, id);
        ResultSet<PropertyContainer> results = nodes.retrieve(query);
        if (results.isNotEmpty()) {
            nodes.remove(results.iterator().next());
            for (String type : related.keySet()) {
                ReversibleMultiMap<String, String> rels = related.get(type);
                for (String value : rels.get(id)) {
                    query = equal(PropertyContainer.ID, id + "-" + value + "-" + type);
                    results = relationships.retrieve(query);
                    relationships.remove(results.uniqueResult());
                }
                for (String key : rels.getKeysByValue(id)) {
                    query = equal(PropertyContainer.ID, key + "-" + id + "-" + type);
                    results = relationships.retrieve(query);
                    relationships.remove(results.uniqueResult());
                }
                rels.removeAll(id);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean addRelationship (String type, String from, String to) {
        related.putIfAbsent(type, new ReversibleMultiMap<>());
        return related.get(type).put(from, to);
    }

    public boolean addRelationship (String type, String from, String to, String properties)  {
        try {
            relationships.add(new PropertyContainer(type, from + "-" + to + "-" + type, mapper.readValue(properties, HashMap.class)));
        } catch (IOException e) {
            HashMap<String, Object> value = new HashMap<>();
            value.put("value", properties);
            relationships.add(new PropertyContainer(type, from + "-" + to + "-" + type, value));
        }
        related.putIfAbsent(type, new ReversibleMultiMap<>());
        return related.get(type).put(from, to);
    }

    public boolean addRelationship (String type, String from, String to, HashMap properties) {
        relationships.add(new PropertyContainer(type, from + "-" + to + "-" + type, properties));
        related.putIfAbsent(type, new ReversibleMultiMap<>());
        return related.get(type).put(from, to);
    }

    public boolean addRelationship (String type, String from, String to, Object properties) {
        HashMap<String, Object> value = new HashMap<>();
        value.put("value", properties);
        relationships.add(new PropertyContainer(type, from + "-" + to + "-" + type, value));
        related.putIfAbsent(type, new ReversibleMultiMap<>());
        return related.get(type).put(from, to); 
    }

    public HashMap<String, Object> getRelationship(String type, String from, String to) {
        final Query<PropertyContainer> query = equal(PropertyContainer.ID, from + "-" + to + "-" + type);
        ResultSet<PropertyContainer> results = relationships.retrieve(query);
        if (results.isEmpty()) {
            if (related.get(type).get(from).contains(to)) {
                return new HashMap<>();
            } else {
                return null;
            }
        }
        return results.uniqueResult().getProperties();
    }

    public boolean updateRelationship(String type, String from, String to, String properties) {
        final Query<PropertyContainer> query = equal(PropertyContainer.ID, from + "-" + to + "-" + type);
        ResultSet<PropertyContainer> results = relationships.retrieve(query);
        if (results.isEmpty()){
            if (!related.get(type).get(from).contains(to)) {
                return false;
            }
        }
        PropertyContainer rel = results.uniqueResult();
        relationships.remove(rel);
        try {
            relationships.add(new PropertyContainer(type, from + "-" + to + "-" + type, mapper.readValue(properties, HashMap.class)));
        } catch (IOException e) {
            HashMap<String, Object> value = new HashMap<>();
            value.put("value", properties);
            relationships.add(new PropertyContainer(type, from + "-" + to + "-" + type, value));
        }
        return true;
    }

    public boolean updateRelationship(String type, String from, String to, HashMap properties) {
        final Query<PropertyContainer> query = equal(PropertyContainer.ID, from + "-" + to + "-" + type);
        ResultSet<PropertyContainer> results = relationships.retrieve(query);
        if (results.isEmpty()){
            if (!related.get(type).get(from).contains(to)) {
                return false;
            }
        }
        PropertyContainer rel = results.uniqueResult();
        relationships.remove(rel);
        relationships.add(new PropertyContainer(type, from + "-" + to + "-" + type, properties));
        return true;
    }

    public boolean deleteRelationshipProperties(String type, String from, String to) {
        final Query<PropertyContainer> query = equal(PropertyContainer.ID, from + "-" + to + "-" + type);
        ResultSet<PropertyContainer> results = relationships.retrieve(query);
        if (results.isEmpty()){
            if (!related.get(type).get(from).contains(to)) {
                return false;
            }
        } else {
            relationships.remove(results.uniqueResult());
        }
        return true;
    }

    public boolean removeRelationship (String type, String from, String to) {
        if(!related.containsKey(type)) {
            return false;
        }
        related.get(type).remove(from, to);
        final Query<PropertyContainer> query = equal(PropertyContainer.ID, from + "-" + to + "-" + type);
        ResultSet<PropertyContainer> results = relationships.retrieve(query);
        if (results.isNotEmpty()) {
            relationships.remove(results.uniqueResult());
        }
        return true;
    }

    public HashSet<String> getOutgoingRelationshipNodeIds(String type, String from) {
        return new HashSet<>(related.get(type).get(from));
    }

    public HashSet<String> getIncomingRelationshipNodeIds(String type, String to) {
        return new HashSet<>(related.get(type).getKeysByValue(to));
    }

    public Set<Object> getOutgoingRelationshipNodes(String type, String from) {
        Set<Object> results = new HashSet<>();
        for (String id : related.get(type).get(from) ) {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("properties", getNode(id));
            results.add(properties);
        }
        return results;
    }

    public Set<Object> getIncomingRelationshipNodes(String type, String from) {
        Set<Object> results = new HashSet<>();
        for (String id : related.get(type).getKeysByValue(from) ) {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("properties", getNode(id));
            results.add(properties);
        }
        return results;
    }

    public Integer getNodeDegree(String id) {
        return getNodeDegree(id, "all", new ArrayList<>());
    }

    public Integer getNodeDegree(String id, String direction) {
        return getNodeDegree(id, direction, new ArrayList<>());
    }

    public Integer getNodeDegree(String id, String direction, List<String> types) {
        final Query<PropertyContainer> query = equal(PropertyContainer.ID, id);
        if (nodes.retrieve(query).isNotEmpty()) {
            Integer count = 0;
            List<String> relTypes;
            if (types.size() == 0) {
                relTypes = new ArrayList<>(related.keySet());
            } else {
                types.retainAll(related.keySet());
                relTypes = types;
            }

            for (String type : relTypes) {
                ReversibleMultiMap<String, String> rels = related.get(type);
                if (direction.equals("all") || direction.equals("out")) {
                    count += rels.get(id).size();
                }
                if (direction.equals("all") || direction.equals("in")) {
                    count += rels.getKeysByValue(id).size();
                }
            }
            return count;
        }

        return null;
    }
}
