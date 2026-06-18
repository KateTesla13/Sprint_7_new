package ru.yandex.praktikum.order;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import ru.yandex.praktikum.BaseTest;
import ru.yandex.praktikum.models.Order;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrderByTrackTest extends BaseTest {

    @Test
    @DisplayName("Успешное получение заказа по track")
    public void getOrderByTrackSuccess() {
        Order order = new Order(
                "Naruto", "Uzumaki", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", null
        );
        int track = createOrderAndGetTrack(order);

        orderClient.getOrderByTrack(track)
                .then()
                .statusCode(SC_OK)
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Получение заказа без указания track")
    public void getOrderByTrackWithoutTrack() {
        orderClient.getOrderByTrackWithoutTrack()
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Получение заказа с неверным track")
    public void getOrderByTrackWithWrongTrack() {
        orderClient.getOrderByTrack(999999)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Заказ не найден"));
    }
}