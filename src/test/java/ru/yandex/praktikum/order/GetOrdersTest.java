package ru.yandex.praktikum.order;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import ru.yandex.praktikum.BaseTest;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersTest extends BaseTest {

    @Test
    @DisplayName("Получение списка всех заказов")
    public void getOrdersList() {
        orderClient.getOrders()
                .then()
                .statusCode(SC_OK)
                .body("orders", notNullValue());
    }

    @Test
    @DisplayName("Получение списка заказов по ID курьера")
    public void getOrdersByCourierId() {
        orderClient.getOrdersByCourierId(courierId)
                .then()
                .statusCode(SC_OK)
                .body("orders", notNullValue());
    }
    @Test
    @DisplayName("Получение списка заказов с неверным ID курьера")
    public void getOrdersByCourierIdWithWrongId() {
        int wrongCourierId = 999999;
        orderClient.getOrdersByCourierId(wrongCourierId)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Курьер с идентификатором " + wrongCourierId + " не найден"));
    }
}