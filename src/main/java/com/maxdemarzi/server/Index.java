package com.maxdemarzi.server;

import com.googlecode.cqengine.attribute.SimpleNullableAttribute;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.index.radix.RadixTreeIndex;
import com.googlecode.cqengine.index.radixinverted.InvertedRadixTreeIndex;
import com.googlecode.cqengine.index.radixreversed.ReversedRadixTreeIndex;
import com.googlecode.cqengine.index.suffix.SuffixTreeIndex;
import com.googlecode.cqengine.index.unique.UniqueIndex;
import com.maxdemarzi.PropertyContainer;
import org.jooby.Err;
import org.jooby.Jooby;
import org.jooby.Status;

import static com.googlecode.cqengine.codegen.AttributeBytecodeGenerator.generateSimpleNullableAttributeForParameterizedGetter;

public class Index  extends Jooby {
    public Index() {
        super("index");
    }

    {
        use("/db")
            /*
             * List Indexes
             * @return Returns <code>200</code> with a list of Indexes or <code>404</code>
             */
            .get("/index", req -> {
                return Server.db.getIndexes();
            })
            /*
             * Find index by property.
             * @param property Property.
             * @return Returns <code>200</code> with a single Index or <code>404</code>
             */
            .get("/index/:property", req -> {
                String property = req.param("property").value();
                if (Server.db.getIndexes().containsKey(property)) {
                    return Server.db.getIndexes().get(property);
                } else {
                    throw new Err(Status.NOT_FOUND);
                }

            })
            /*
             * Create an Index on a Node Property
             * @param index Index.
             * @param type Type.
             * @param property Property.
             * @return Returns <code>201</code>
             */
            .post("/index/node/:index/:type/:property", (req, rsp) -> {
                String index = req.param("index").value();
                String type = req.param("type").value();
                String property = req.param("property").value();

                switch(type) {
                    case "integer":
                        createNodeIntegerIndex(index, property);
                        break;
                    case "long":
                        createNodeLongIndex(index, property);
                        break;
                    case "float":
                        createNodeFloatIndex(index, property);
                        break;
                    case "double":
                        createNodeDoubleIndex(index, property);
                        break;
                    case "string":
                        createNodeStringIndex(index, property);
                        break;
                    default:
                }

                rsp.status(201);
                rsp.send(Server.db.getIndexes().get(property));
            })
            /*
             * Create an Index on a Property
             * @param index Index.
             * @param type Type.
             * @param property Property.
             * @return Returns <code>201</code>
             */
                .post("/index/relationship/:index/:type/:property", (req, rsp) -> {
                    String index = req.param("index").value();
                    String type = req.param("type").value();
                    String property = req.param("property").value();

                    switch(type) {
                        case "integer":
                            createRelationshipIntegerIndex(index, property);
                            break;
                        case "long":
                            createRelationshipLongIndex(index, property);
                            break;
                        case "float":
                            createRelationshipFloatIndex(index, property);
                            break;
                        case "double":
                            createRelationshipDoubleIndex(index, property);
                            break;
                        case "string":
                            createRelationshipStringIndex(index, property);
                            break;
                        default:
                    }

                    rsp.status(201);
                    rsp.send(Server.db.getIndexes().get(property));
                })
            .produces("json");
    }

    private void createNodeIntegerIndex(String index, String property) throws InstantiationException, IllegalAccessException {
        Class<? extends SimpleNullableAttribute<PropertyContainer, Integer>> attributeClass =
                generateSimpleNullableAttributeForParameterizedGetter(
                        PropertyContainer.class, Integer.class,"getIntegerProperty", property, property);
        SimpleNullableAttribute<PropertyContainer, Integer> attribute = attributeClass.newInstance();

        switch (index) {
            case "navigable":
                Server.db.addNodeIndex(NavigableIndex.onAttribute(attribute), property);
                break;
            case "hash":
                Server.db.addNodeIndex(HashIndex.onAttribute(attribute), property);
                break;
            case "unique":
                Server.db.addNodeIndex(UniqueIndex.onAttribute(attribute), property);
                break;
            default:
                throw new Err(Status.NOT_MODIFIED);
        }
    }

    private void createNodeLongIndex(String index, String property) throws InstantiationException, IllegalAccessException {
        Class<? extends SimpleNullableAttribute<PropertyContainer, Long>> attributeClass =
                generateSimpleNullableAttributeForParameterizedGetter(
                        PropertyContainer.class, Long.class,"getLongProperty", property, property);
        SimpleNullableAttribute<PropertyContainer, Long> attribute = attributeClass.newInstance();

        switch (index) {
            case "hash":
                Server.db.addNodeIndex(HashIndex.onAttribute(attribute), property);
                break;
            case "unique":
                Server.db.addNodeIndex(UniqueIndex.onAttribute(attribute), property);
                break;
            case "navigable":
                Server.db.addNodeIndex(NavigableIndex.onAttribute(attribute), property);
                break;
            default:
                throw new Err(Status.NOT_MODIFIED);
        }
    }

    private void createNodeFloatIndex(String index, String property) throws InstantiationException, IllegalAccessException {
        Class<? extends SimpleNullableAttribute<PropertyContainer, Float>> attributeClass =
                generateSimpleNullableAttributeForParameterizedGetter(
                        PropertyContainer.class, Float.class,"getFloatProperty", property, property);
        SimpleNullableAttribute<PropertyContainer, Float> attribute = attributeClass.newInstance();

        switch (index) {
            case "navigable":
                Server.db.addNodeIndex(NavigableIndex.onAttribute(attribute), property);
                break;
            case "hash":
                Server.db.addNodeIndex(HashIndex.onAttribute(attribute), property);
                break;
            case "unique":
                Server.db.addNodeIndex(UniqueIndex.onAttribute(attribute), property);
                break;
            default:
                throw new Err(Status.NOT_MODIFIED);
        }
    }

    private void createNodeDoubleIndex(String index, String property) throws InstantiationException, IllegalAccessException {
        Class<? extends SimpleNullableAttribute<PropertyContainer, Double>> attributeClass =
                generateSimpleNullableAttributeForParameterizedGetter(
                        PropertyContainer.class, Double.class,"getDoubleProperty", property, property);
        SimpleNullableAttribute<PropertyContainer, Double> attribute = attributeClass.newInstance();

        switch (index) {
            case "navigable":
                Server.db.addNodeIndex(NavigableIndex.onAttribute(attribute), property);
                break;
            case "hash":
                Server.db.addNodeIndex(HashIndex.onAttribute(attribute), property);
                break;
            case "unique":
                Server.db.addNodeIndex(UniqueIndex.onAttribute(attribute), property);
                break;
            default:
                throw new Err(Status.NOT_MODIFIED);
        }
    }
    private void createNodeStringIndex(String index, String property) throws InstantiationException, IllegalAccessException {
        Class<? extends SimpleNullableAttribute<PropertyContainer, String>> attributeClass;
        if (index.equals("full_text")){
            attributeClass =
                    generateSimpleNullableAttributeForParameterizedGetter(
                            PropertyContainer.class, String.class, "getLowerCaseStringProperty", property, property);
        }else {
            attributeClass =
                    generateSimpleNullableAttributeForParameterizedGetter(
                            PropertyContainer.class, String.class, "getStringProperty", property, property);
        }
        SimpleNullableAttribute<PropertyContainer, String> attribute = attributeClass.newInstance();

        switch (index) {
            case "hash":
                Server.db.addNodeIndex(HashIndex.onAttribute(attribute), property);
                break;
            case "unique":
                Server.db.addNodeIndex(UniqueIndex.onAttribute(attribute), property);
                break;
            case "navigable":
                Server.db.addNodeIndex(NavigableIndex.onAttribute(attribute), property);
                break;
            case "radix":
                Server.db.addNodeIndex(RadixTreeIndex.onAttribute(attribute), property);
                break;
            case "reversed_radix":
                Server.db.addNodeIndex(ReversedRadixTreeIndex.onAttribute(attribute), property);
                break;
            case "inverted_radix":
                Server.db.addNodeIndex(InvertedRadixTreeIndex.onAttribute(attribute), property);
                break;
            case "suffix":
                Server.db.addNodeIndex(SuffixTreeIndex.onAttribute(attribute), property);
                break;
            case "full_text":
                Server.db.addNodeIndex(RadixTreeIndex.onAttribute(attribute), property);
                Server.db.addNodeIndex(SuffixTreeIndex.onAttribute(attribute), property);
                break;
            default:
                throw new Err(Status.NOT_MODIFIED);
        }
    }

    private void createRelationshipIntegerIndex(String index, String property) throws InstantiationException, IllegalAccessException {
        Class<? extends SimpleNullableAttribute<PropertyContainer, Integer>> attributeClass =
                generateSimpleNullableAttributeForParameterizedGetter(
                        PropertyContainer.class, Integer.class,"getIntegerProperty", property, property);
        SimpleNullableAttribute<PropertyContainer, Integer> attribute = attributeClass.newInstance();

        switch (index) {
            case "navigable":
                Server.db.addRelationshipIndex(NavigableIndex.onAttribute(attribute), property);
                break;
            case "hash":
                Server.db.addRelationshipIndex(HashIndex.onAttribute(attribute), property);
                break;
            case "unique":
                Server.db.addRelationshipIndex(UniqueIndex.onAttribute(attribute), property);
                break;
            default:
                throw new Err(Status.NOT_MODIFIED);
        }
    }

    private void createRelationshipLongIndex(String index, String property) throws InstantiationException, IllegalAccessException {
        Class<? extends SimpleNullableAttribute<PropertyContainer, Long>> attributeClass =
                generateSimpleNullableAttributeForParameterizedGetter(
                        PropertyContainer.class, Long.class,"getLongProperty", property, property);
        SimpleNullableAttribute<PropertyContainer, Long> attribute = attributeClass.newInstance();

        switch (index) {
            case "hash":
                Server.db.addRelationshipIndex(HashIndex.onAttribute(attribute), property);
                break;
            case "unique":
                Server.db.addRelationshipIndex(UniqueIndex.onAttribute(attribute), property);
                break;
            case "navigable":
                Server.db.addRelationshipIndex(NavigableIndex.onAttribute(attribute), property);
                break;
            default:
                throw new Err(Status.NOT_MODIFIED);
        }
    }

    private void createRelationshipFloatIndex(String index, String property) throws InstantiationException, IllegalAccessException {
        Class<? extends SimpleNullableAttribute<PropertyContainer, Float>> attributeClass =
                generateSimpleNullableAttributeForParameterizedGetter(
                        PropertyContainer.class, Float.class,"getFloatProperty", property, property);
        SimpleNullableAttribute<PropertyContainer, Float> attribute = attributeClass.newInstance();

        switch (index) {
            case "navigable":
                Server.db.addRelationshipIndex(NavigableIndex.onAttribute(attribute), property);
                break;
            case "hash":
                Server.db.addRelationshipIndex(HashIndex.onAttribute(attribute), property);
                break;
            case "unique":
                Server.db.addRelationshipIndex(UniqueIndex.onAttribute(attribute), property);
                break;
            default:
                throw new Err(Status.NOT_MODIFIED);
        }
    }

    private void createRelationshipDoubleIndex(String index, String property) throws InstantiationException, IllegalAccessException {
        Class<? extends SimpleNullableAttribute<PropertyContainer, Double>> attributeClass =
                generateSimpleNullableAttributeForParameterizedGetter(
                        PropertyContainer.class, Double.class,"getDoubleProperty", property, property);
        SimpleNullableAttribute<PropertyContainer, Double> attribute = attributeClass.newInstance();

        switch (index) {
            case "navigable":
                Server.db.addRelationshipIndex(NavigableIndex.onAttribute(attribute), property);
                break;
            case "hash":
                Server.db.addRelationshipIndex(HashIndex.onAttribute(attribute), property);
                break;
            case "unique":
                Server.db.addRelationshipIndex(UniqueIndex.onAttribute(attribute), property);
                break;
            default:
                throw new Err(Status.NOT_MODIFIED);
        }
    }
    private void createRelationshipStringIndex(String index, String property) throws InstantiationException, IllegalAccessException {
        Class<? extends SimpleNullableAttribute<PropertyContainer, String>> attributeClass;
        if (index.equals("full_text")){
            attributeClass =
                    generateSimpleNullableAttributeForParameterizedGetter(
                            PropertyContainer.class, String.class, "getLowerCaseStringProperty", property, property);
        }else {
            attributeClass =
                    generateSimpleNullableAttributeForParameterizedGetter(
                            PropertyContainer.class, String.class, "getStringProperty", property, property);
        }
        SimpleNullableAttribute<PropertyContainer, String> attribute = attributeClass.newInstance();

        switch (index) {
            case "hash":
                Server.db.addRelationshipIndex(HashIndex.onAttribute(attribute), property);
                break;
            case "unique":
                Server.db.addRelationshipIndex(UniqueIndex.onAttribute(attribute), property);
                break;
            case "navigable":
                Server.db.addRelationshipIndex(NavigableIndex.onAttribute(attribute), property);
                break;
            case "radix":
                Server.db.addRelationshipIndex(RadixTreeIndex.onAttribute(attribute), property);
                break;
            case "reversed_radix":
                Server.db.addRelationshipIndex(ReversedRadixTreeIndex.onAttribute(attribute), property);
                break;
            case "inverted_radix":
                Server.db.addRelationshipIndex(InvertedRadixTreeIndex.onAttribute(attribute), property);
                break;
            case "suffix":
                Server.db.addRelationshipIndex(SuffixTreeIndex.onAttribute(attribute), property);
                break;
            case "full_text":
                Server.db.addRelationshipIndex(RadixTreeIndex.onAttribute(attribute), property);
                Server.db.addRelationshipIndex(SuffixTreeIndex.onAttribute(attribute), property);
                break;
            default:
                throw new Err(Status.NOT_MODIFIED);
        }
    }
}
