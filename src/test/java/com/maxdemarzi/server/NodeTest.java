package com.maxdemarzi.server;

import com.maxdemarzi.Disoriented;
import org.jooby.test.JoobyRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;

public class NodeTest {

    private Disoriented db;

    @ClassRule
    public static JoobyRule app = new JoobyRule(new Server());

    @Before
    public void setup() throws IOException {
        db = Disoriented.getInstance();
        db.clear();
        db.addNode("User", "emptyNode");
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("property", "Value");
        db.addNode("User", "singlePropertyNode", prop);
        HashMap<String, Object> props =  new HashMap<>();
        props.put("city", "Chicago");
        props.put("prop", prop);
        db.addNode("User", "complexPropertiesNode", props);
    }

    @Test
    public void integrationTestGetNodeNotThere() {
        when().
                get("/db/node/notThere").
        then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestGetEmptyNode() {

        HashMap<String, Object> props =  new HashMap<>();
        props.put("_id", "emptyNode");
        props.put("_type", "User");

        when().
                get("/db/node/emptyNode").
        then().
                assertThat()
                .body("$", equalTo(props))
                .statusCode(200)
                .contentType("application/json;charset=UTF-8");
    }

    @Test
    public void integrationTestGetSinglePropertyNode() {
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("property", "Value");

        HashMap<String, Object> props =  new HashMap<>();
        props.put("property", "Value");
        props.put("_id", "singlePropertyNode");
        props.put("_type", "User");
        when().
                get("/db/node/singlePropertyNode").
        then().
                assertThat()
                .body("$", equalTo(props))
                .statusCode(200)
                .contentType("application/json;charset=UTF-8");
    }

    @Test
    public void integrationTestGetComplexPropertyNode() {
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("property", "Value");

        HashMap<String, Object> props =  new HashMap<>();
        props.put("city", "Chicago");
        props.put("prop", prop);
        props.put("_id", "complexPropertiesNode");
        props.put("_type", "User");

        when().
                get("/db/node/complexPropertiesNode").
        then().
                assertThat()
                .body("$", equalTo(props))
                .statusCode(200)
                .contentType("application/json;charset=UTF-8");
    }

    @Test
    public void integrationTestCreateEmptyNode() {
        HashMap<String, Object> props =  new HashMap<>();
        props.put("_id", "empty");
        props.put("_type", "Empty");
        given().
                contentType("application/json").
                body("{}").
        when().
                post("/db/node/Empty/empty").
        then().
                assertThat().
                body("$", equalTo(props)).
                statusCode(201).
                contentType("application/json;charset=UTF-8");
    }

    @Test
    public void integrationTestCreateSinglePropertyNode() {
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("property", "Value");

        HashMap<String, Object> props =  new HashMap<>();
        props.put("property", "Value");
        props.put("_id", "singlePropertyNode");
        props.put("_type", "User");

        given().
                contentType("application/json").
                body(prop).
        when().
                post("/db/node/User/singlePropertyNode").
        then().
                assertThat().
                body("$", equalTo(props)).
                statusCode(201).
                contentType("application/json;charset=UTF-8");
    }

    @Test
    public void integrationTestCreateComplexPropertyNode() {
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("property", "Value");

        HashMap<String, Object> props =  new HashMap<>();
        props.put("city", "Chicago");
        props.put("prop", prop);
        props.put("_id", "complexPropertiesNode");
        props.put("_type", "User");

        given().
                contentType("application/json").
                body(props).
        when().
                post("/db/node/User/complexPropertiesNode").
        then().
                assertThat().
                body("$", equalTo(props)).
                statusCode(201);
    }

    @Test
    public void integrationTestPutNodeNotThere() {
        when().
                put("/db/node/notThere").
        then().
                assertThat().
                statusCode(304);
    }

    @Test
    public void integrationTestUpdateSinglePropertyNode() {
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("property", "Value2");

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("property", "Value2");
        properties.put("_id", "singlePropertyNode");
        properties.put("_type", "User");

        given().
                contentType("application/json").
                body(prop).
        when().
                put("/db/node/singlePropertyNode").
        then().
                assertThat().
                body("$", equalTo(properties)).
                statusCode(201).
                contentType("application/json;charset=UTF-8");
    }

    @Test
    public void integrationTestUpdateComplexPropertyNode() {
        HashMap<String, Object> prop =  new HashMap<>();
        prop.put("property", "Value");

        HashMap<String, Object> props =  new HashMap<>();
        props.put("city", "Miami");
        props.put("prop", prop);
        props.put("_id", "complexPropertiesNode");
        props.put("_type", "User");

        given().
                contentType("application/json").
                body(props).
                when().
                put("/db/node/complexPropertiesNode").
                then().
                assertThat().
                body("$", equalTo(props)).
                statusCode(201);
    }

    @Test
    public void integrationTestDeleteNodeNotThere() {
        when().
                delete("/db/node/notThere").
        then().
                assertThat().
                statusCode(404);
    }

    @Test
    public void integrationTestDeleteEmptyNode() {
        when().
                delete("/db/node/emptyNode").
        then().
                assertThat().
                statusCode(204);
    }
}
