package edu.hm.praegla;

import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public abstract class ApiClient {
    protected final RequestSpecification spec;

    protected ApiClient(RequestSpecification spec) {
        this.spec = spec;
    }

    protected String createResource(String path, Object bodyPayload) {
        return given()
                .spec(spec)
                .body(bodyPayload)
                .when()
                .put(path)
                .then()
                .statusCode(201)
                .extract().header("location");
    }

    protected <T> T getResourceByLocationHeader(String locationHeader, Class<T> responseClass) {
        return given()
                .spec(spec)
                .when()
                .get(locationHeader)
                .then()
                .statusCode(200)
                .extract().as(responseClass);
    }

    protected <T> T getResourceById(String path, long id, Class<T> responseClass) {
        return given()
                .spec(spec)
                .when()
                .get(path, id)
                .then()
                .statusCode(200)
                .extract().as(responseClass);
    }
}
