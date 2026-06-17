package ru.yandex.praktikum.order;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;
import ru.yandex.praktikum.BaseTest;
import ru.yandex.praktikum.models.Courier;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersTest extends BaseTest {

    @Test
    @Step("Получение списка всех заказов")
    public void getOrdersList() {
        given()
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Test
    @Step("Получение списка заказов по ID курьера: {courierId}")
    public void getOrdersByCourierId() {
        Courier courier = createCourier();

        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body(new Courier(courier.getLogin(), courier.getPassword()))
                .post("/api/v1/courier/login");

        int courierId = loginResponse.then().extract().path("id");

        given()
                .queryParam("courierId", courierId)
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders", notNullValue());

        this.courierId = courierId;
    }
    @Test
    @Step("Получение списка заказов с неверным ID курьера: {wrongCourierId}")
    public void getOrdersByCourierIdWithWrongId() { //получение списка заказов курьера с неверным Id
        int wrongCourierId = 999999;
        given()
                .queryParam("courierId", wrongCourierId)
                .get("/api/v1/orders")
                .then()
                .statusCode(404)
                .body("message", equalTo("Курьер с идентификатором " + wrongCourierId + " не найден"));
    }
}