package ru.yandex.praktikum.order;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import ru.yandex.praktikum.BaseTest;
import ru.yandex.praktikum.models.Courier;
import ru.yandex.praktikum.models.Order;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class AcceptOrderTest extends BaseTest {

    @Step("Логин курьера: {courier.login}")
    private int loginCourierAndGetId(Courier courier) {
        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body(new Courier(courier.getLogin(), courier.getPassword()))
                .post("/api/v1/courier/login");

        return loginResponse.then().extract().path("id");
    }

    @Step("Создание заказа")
    private int createOrderAndGetId(Order order) {
        Response orderResponse = given()
                .header("Content-type", "application/json")
                .body(order)
                .post("/api/v1/orders");

        orderResponse.then().statusCode(201);
        int track = orderResponse.then().extract().path("track");

        Response trackResponse = given()
                .queryParam("t", track)
                .get("/api/v1/orders/track");

        return trackResponse.then().extract().path("order.id");
    }

    @Test
    @DisplayName("Успешное принятие заказа - Наруто принимает заказ Саске")
    @Step("Проверка успешного принятия заказа курьером")
    public void acceptOrderSuccess() {
        Courier courier = createCourier();
        this.courierId = loginCourierAndGetId(courier);

        long timestamp = System.currentTimeMillis();

        Order order = new Order(
                "Саске" + timestamp, "Учиха" + timestamp, "Коноха, 112 квартира", 4, "+7 113 355 35 35",
                3, "2020-06-10", "Я восстановлю свой клан", null
        );
        int orderId = createOrderAndGetId(order);

        given()
                .queryParam("courierId", this.courierId)
                .put("/api/v1/orders/accept/" + orderId)
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Повторное принятие заказа - Саске уже взял заказ")
    @Step("Проверка повторного принятия заказа")
    public void acceptOrderAlreadyInWork() {
        Courier courier = createCourier();
        this.courierId = loginCourierAndGetId(courier);

        Order order = new Order(
                "Наруто", "Узумаки", "Коноха, 142 квартира", 4, "+7 800 355 35 35",
                5, "2020-06-06", "Саске, вернись в Коноху", null
        );
        int orderId = createOrderAndGetId(order);

        given()
                .queryParam("courierId", this.courierId)
                .put("/api/v1/orders/accept/" + orderId)
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));

        given()
                .queryParam("courierId", this.courierId)
                .put("/api/v1/orders/accept/" + orderId)
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот заказ уже в работе"));
    }

    @Test
    @DisplayName("Принятие заказа без courierId - как без Хокаге")
    @Step("Проверка принятия заказа без courierId")
    public void acceptOrderWithoutCourierId() {
        Courier courier = createCourier();
        this.courierId = loginCourierAndGetId(courier);

        Order order = new Order(
                "Какаши", "Хатаке", "Коноха, 101 квартира", 4, "+7 999 888 77 66",
                3, "2020-07-07", "Никогда не нарушай правила", null
        );
        int orderId = createOrderAndGetId(order);

        given()
                .put("/api/v1/orders/accept/" + orderId)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Принятие заказа с несуществующим курьером - Орочимару в деревне")
    @Step("Проверка принятия заказа с неверным courierId")
    public void acceptOrderWithWrongCourierId() {
        Courier courier = createCourier();
        this.courierId = loginCourierAndGetId(courier);

        Order order = new Order(
                "Сакура", "Харуно", "Коноха, 115 квартира", 4, "+7 515 666 13 15",
                5, "2020-06-06", "Саске, вернись в Коноху", null
        );
        int orderId = createOrderAndGetId(order);

        given()
                .queryParam("courierId", 999999)
                .put("/api/v1/orders/accept/" + orderId)
                .then()
                .statusCode(404)
                .body("message", equalTo("Курьера с таким id не существует"));
    }

    @Test
    @DisplayName("Принятие заказа без номера. БАГ: ОР: status code 400, ФР: status code 404")
    @Description("Баг API: ОР: status code 400, ФР: status code 404")
    @Step("Проверка принятия заказа без номера заказа")
    public void acceptOrderWithoutOrderId() {
        Courier courier = createCourier();
        this.courierId = loginCourierAndGetId(courier);

        given()
                .queryParam("courierId", this.courierId)
                .put("/api/v1/orders/accept/")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для поиска"));
    }

    @Test
    @DisplayName("Принятие заказа с несуществующим номером - миссия из другой деревни")
    @Step("Проверка принятия заказа с неверным номером заказа")
    public void acceptOrderWithWrongOrderId() {
        Courier courier = createCourier();
        this.courierId = loginCourierAndGetId(courier);


        given()
                .queryParam("courierId", this.courierId)
                .put("/api/v1/orders/accept/999999")
                .then()
                .statusCode(404)
                .body("message", equalTo("Заказа с таким id не существует"));
    }
}