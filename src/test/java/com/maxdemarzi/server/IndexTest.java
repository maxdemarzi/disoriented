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
}
