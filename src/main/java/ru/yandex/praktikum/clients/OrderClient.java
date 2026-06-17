package ru.yandex.praktikum.clients;

import io.restassured.response.Response;
import ru.yandex.praktikum.models.Order;
import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String ORDER_PATH = "/api/v1/orders";

    public Response create(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .post(ORDER_PATH);
    }
}