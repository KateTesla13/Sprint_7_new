package ru.yandex.praktikum.clients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import ru.yandex.praktikum.models.Courier;

import static io.restassured.RestAssured.given;

public class CourierClient {

    private static final String COURIER_PATH = "/api/v1/courier";
    private static final String LOGIN_PATH = "/api/v1/courier/login";

    @Step("Создание курьера с логином: {courier.login}")
    public Response create(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .post(COURIER_PATH);
    }
    @Step("Авторизация курьера с логином: {login}")
    public Response login(String login, String password) {
        return given()
                .header("Content-type", "application/json")
                .body(new Courier(login, password))
                .post(LOGIN_PATH);
    }
    @Step("Удаление курьера по ID: {courierId}")
    public Response delete(int courierId) {
        return given()
                .header("Content-type", "application/json")
                .delete(COURIER_PATH + "/" + courierId);
    }
    @Step("Удаление курьера без ID")
    public Response deleteWithoutId() {
        return given()
                .delete(COURIER_PATH + "/");
    }
}