package ru.yandex.praktikum.clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.praktikum.models.Order;
import static io.restassured.RestAssured.given;

public class OrderClient {

    private static final String ORDER_PATH = "/api/v1/orders";

    @Step("Создание заказа")
    public Response create(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .post(ORDER_PATH);
    }

    @Step("Получение списка всех заказов")
    public Response getOrders() {
        return given()
                .get(ORDER_PATH);
    }

    @Step("Получение списка заказов по ID курьера: {courierId}")
    public Response getOrdersByCourierId(int courierId) {
        return given()
                .queryParam("courierId", courierId)
                .get(ORDER_PATH);
    }
    @Step("Принятие заказа: orderId={orderId}, courierId={courierId}")
    public Response acceptOrder(int orderId, int courierId) {
        return given()
                .queryParam("courierId", courierId)
                .put(ORDER_PATH + "/accept/" + orderId);
    }

    @Step("Принятие заказа без ID заказа")
    public Response acceptOrderWithoutId(int courierId) {
        return given()
                .queryParam("courierId", courierId)
                .put(ORDER_PATH + "/accept/");
    }

    @Step("Получение заказа по треку: {track}")
    public Response getOrderByTrack(int track) {
        return given()
                .queryParam("t", track)
                .get(ORDER_PATH + "/track");
    }
    @Step("Получение заказа без трека")
    public Response getOrderByTrackWithoutTrack() {
        return given()
                .get(ORDER_PATH + "/track");
    }
}