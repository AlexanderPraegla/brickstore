package edu.hm.brickstore.client;

import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public abstract class ApiTestClient {
    protected final RequestSpecification spec;

    protected ApiTestClient(RequestSpecification spec) {
        this.spec = spec;
    }

    public String createResource(String path, Object bodyPayload) {
        return given()
                .spec(spec)
                .body(bodyPayload)
                .when()
                .put(path)
                .then()
                .statusCode(201)
                .extract().header("location");
    }

    public <T> T getResourceByLocationHeader(String locationHeader, Class<T> responseClass) {
        return given()
                .spec(spec)
                .when()
                .get(locationHeader)
                .then()
                .statusCode(200)
                .extract().as(responseClass);
    }

    public <T> T getResourceById(String path, long id, Class<T> responseClass) {
        return given()
                .spec(spec)
                .when()
                .get(path, id)
                .then()
                .statusCode(200)
                .extract().as(responseClass);
    }
}
