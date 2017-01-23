package com.maxdemarzi;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.SimpleNullableAttribute;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.index.unique.UniqueIndex;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.resultset.ResultSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Random;

import static com.googlecode.cqengine.codegen.AttributeBytecodeGenerator.generateSimpleNullableAttributeForParameterizedGetter;
import static com.googlecode.cqengine.query.QueryFactory.between;
import static com.googlecode.cqengine.query.QueryFactory.equal;

public class CQEngineTest {

    Random rand = new Random();

    @Test
    public void shouldAddandFindEntries() {
        IndexedCollection<PropertyContainer> nodes = new ConcurrentIndexedCollection<>();
        nodes.addIndex(UniqueIndex.onAttribute(PropertyContainer.ID));
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "Max");
        String id = "max";
        PropertyContainer node =new PropertyContainer(id , properties);
        nodes.add(node);
        Query<PropertyContainer> query = equal(PropertyContainer.ID, id);
        ResultSet<PropertyContainer> results = nodes.retrieve(query);
        Assert.assertEquals(node, results.uniqueResult());
    }

    @Test
    public void shouldLetMeAddIndexesToAlreadyPopulatedCollection() {
        IndexedCollection<PropertyContainer> nodes = new ConcurrentIndexedCollection<>();
        HashMap<String, Object> properties = new HashMap<>();
        properties.put("name", "Max");
        String id = "max";
        PropertyContainer node =new PropertyContainer(id , properties);
        nodes.add(node);
        nodes.addIndex(UniqueIndex.onAttribute(PropertyContainer.ID));
        Query<PropertyContainer> query = equal(PropertyContainer.ID, id);
        ResultSet<PropertyContainer> results = nodes.retrieve(query);
        Assert.assertEquals(node, results.uniqueResult());
    }

    @Test
    public void shouldLetMeAddNewIndexes() throws IllegalAccessException, InstantiationException {
        IndexedCollection<PropertyContainer> nodes = new ConcurrentIndexedCollection<>();

        for (int person = 0; person < 1632803; person++) {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put("id" + person, "id" + person);
            properties.put("age", rand.nextInt(120));
            nodes.add(new PropertyContainer("id" + person, properties));
        }

        Class<? extends SimpleNullableAttribute<PropertyContainer, Integer>> attributeClass =
                generateSimpleNullableAttributeForParameterizedGetter(PropertyContainer.class, Integer.class,
                        "getIntegerProperty", "age", "age");
        SimpleNullableAttribute<PropertyContainer, Integer> ageAttribute = attributeClass.newInstance();

        nodes.addIndex( NavigableIndex.onAttribute(ageAttribute));


        HashMap<Integer, Integer> ages = new HashMap<>();
        Integer age;
        Query<PropertyContainer> query = between(ageAttribute, 35, 44);
        Long start = System.currentTimeMillis();
        ResultSet<PropertyContainer> results = nodes.retrieve(query);
        for (PropertyContainer nodeEntry : results) {
            age = (Integer) nodeEntry.getProperties().get("age");
            ages.merge(age, 1, Integer::sum);
        }
        Long end = System.currentTimeMillis();
        System.out.println(end-start);
    }

}
