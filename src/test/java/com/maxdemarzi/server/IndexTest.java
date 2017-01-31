package com.maxdemarzi.server;

import com.maxdemarzi.Disoriented;
import io.restassured.http.ContentType;
import org.jooby.test.JoobyRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import static io.restassured.RestAssured.when;

public class IndexTest {
    private Disoriented db;

    @ClassRule
    public static JoobyRule app = new JoobyRule(new Server());

    @Before
    public void setup() throws IOException {
        db = Disoriented.getInstance();
        db.clear();

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("stars", 5);

        HashMap<String, Object> properties2 = new HashMap<>();
        properties2.put("stars", 4);

        db.addNode("User", "node1");
        db.addNode("User", "node2");
        db.addNode("User", "node3");
        db.addRelationship("FOLLOWS", "node1", "node2", properties);
        db.addRelationship("FOLLOWS", "node1", "node3", properties2);

    }

    @Test
    public void integrationTestIndexList() {
        HashMap<String, Set<String>> found = db.getIndexes();
        when().
                get("/db/index").
        then().
                contentType(ContentType.JSON).
                assertThat().extract().response().equals(found);
    }

    @Test
    public void integrationTestGetIndex() {
        when().
                get("/db/index/_type").
                then().
                assertThat().
                statusCode(200);
    }

    @Test
    public void integrationTestPostNodeIntegerIndex() {
        when().
                post("/db/index/node/navigable/integer/birth_year").
        then().
                assertThat().
                statusCode(201);
    }

    @Test
    public void integrationTestPostNodeFloatIndex() {
        when().
                post("/db/index/node/navigable/float/radius").
                then().
                assertThat().
                statusCode(201);
    }

    @Test
    public void integrationTestPostNodeDoubleIndex() {
        when().
                post("/db/index/node/navigable/double/amount").
                then().
                assertThat().
                statusCode(201);
    }

    @Test
    public void integrationTestPostNodeStringIndex() {
        when().
                post("/db/index/node/navigable/double/first_name").
                then().
                assertThat().
                statusCode(201);
    }

    @Test
    public void integrationTestPostFullTextNodeIndex() {
        when().
                post("/db/index/node/full_text/string/last_name").
                then().
                assertThat().
                statusCode(201);
    }

    @Test
    public void integrationTestPostRelationshipIntegerIndex() {
        when().
                post("/db/index/relationship/hash/integer/rating").
        then().
                assertThat().
                statusCode(201);
    }

    @Test
    public void integrationTestPostRelationshipFloatIndex() {
        when().
                post("/db/index/relationship/hash/float/weight").
                then().
                assertThat().
                statusCode(201);
    }

    @Test
    public void integrationTestPostRelationshipDoubleIndex() {
        when().
                post("/db/index/relationship/hash/double/cost").
                then().
                assertThat().
                statusCode(201);
    }
    @Test
    public void integrationTestPostRelationshipStringIndex() {
        when().
                post("/db/index/relationship/hash/string/reason").
                then().
                assertThat().
                statusCode(201);
    }

    @Test
    public void integrationTestPostRelationshipFullTextIndex() {
        when().
                post("/db/index/relationship/full_text/string/description").
                then().
                assertThat().
                statusCode(201);
    }


}
