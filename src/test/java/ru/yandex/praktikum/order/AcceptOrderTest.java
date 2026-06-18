package ru.yandex.praktikum.order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import ru.yandex.praktikum.BaseTest;
import ru.yandex.praktikum.models.Order;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class AcceptOrderTest extends BaseTest {

    @Test
    @DisplayName("Успешное принятие заказа - Наруто принимает заказ Саске")
    public void acceptOrderSuccess() {
        long timestamp = System.currentTimeMillis();

        Order order = new Order(
                "Саске" + timestamp, "Учиха" + timestamp, "Коноха, 112 квартира", 4, "+7 113 355 35 35",
                3, "2020-06-10", "Я восстановлю свой клан", null
        );
        int orderId = createOrderAndGetId(order);

        orderClient.acceptOrder(orderId, courierId)
                .then()
                .statusCode(SC_OK)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Повторное принятие заказа - Саске уже взял заказ")
    public void acceptOrderAlreadyInWork() {
        Order order = new Order(
                "Наруто", "Узумаки", "Коноха, 142 квартира", 4, "+7 800 355 35 35",
                5, "2020-06-06", "Саске, вернись в Коноху", null
        );
        int orderId = createOrderAndGetId(order);

        orderClient.acceptOrder(orderId, courierId)
                .then()
                .statusCode(SC_OK)
                .body("ok", equalTo(true));

        orderClient.acceptOrder(orderId, courierId)
                .then()
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот заказ уже в работе"));
    }

    @Test
    @DisplayName("Принятие заказа без courierId - как без Хокаге")
    @Description("Баг API: ОР: status code 400, ФР: status code 404")
    public void acceptOrderWithoutCourierId() {
        Order order = new Order(
                "Какаши", "Хатаке", "Коноха, 101 квартира", 4, "+7 999 888 77 66",
                3, "2020-07-07", "Никогда не нарушай правила", null
        );
        int orderId = createOrderAndGetId(order);

        orderClient.acceptOrder(orderId, 0)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Принятие заказа с несуществующим курьером - Орочимару в деревне")
    public void acceptOrderWithWrongCourierId() {
        Order order = new Order(
                "Сакура", "Харуно", "Коноха, 115 квартира", 4, "+7 515 666 13 15",
                5, "2020-06-06", "Саске, вернись в Коноху", null
        );
        int orderId = createOrderAndGetId(order);

        orderClient.acceptOrder(orderId, 999999)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @Test
    @DisplayName("Принятие заказа без номера. БАГ: ОР: status code 400, ФР: status code 404")
    @Description("Баг API: ОР: status code 400, ФР: status code 404")
    public void acceptOrderWithoutOrderId() {
        orderClient.acceptOrderWithoutId(courierId)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Принятие заказа с несуществующим номером - миссия из другой деревни")
    public void acceptOrderWithWrongOrderId() {
        orderClient.acceptOrder(999999, courierId)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Заказа с таким id не существует"));
    }
}