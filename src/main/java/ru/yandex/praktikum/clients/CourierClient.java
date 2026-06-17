package ru.yandex.praktikum.clients;

import io.restassured.response.Response;
import ru.yandex.praktikum.models.Courier;

import static io.restassured.RestAssured.given;

public class CourierClient {

    private static final String COURIER_PATH = "/api/v1/courier";

    public Response create(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .post(COURIER_PATH);
    }
}