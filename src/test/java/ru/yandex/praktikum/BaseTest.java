package ru.yandex.praktikum;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import ru.yandex.praktikum.clients.CourierClient;
import ru.yandex.praktikum.clients.OrderClient;
import ru.yandex.praktikum.generators.DataGenerator;
import ru.yandex.praktikum.models.Courier;
import ru.yandex.praktikum.models.Order;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.equalTo;

public class BaseTest {

    protected Courier courier;
    protected int courierId;
    protected CourierClient courierClient = new CourierClient();
    protected OrderClient orderClient = new OrderClient();

    @Before
    @Step("Подготовка тестовых данных")
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        courier = createCourier();
        courierId = loginAndGetId(courier);
    }

    @After
    @Step("Удаление курьера с ID: {courierId}")
    public void cleanUp() {
        if (courierId != 0) {
            courierClient.delete(courierId);
        }
    }

    @Step("Создание курьера")
    protected Courier createCourier() {
        String login = DataGenerator.getRandomLogin();
        String password = DataGenerator.getRandomPassword();
        String firstName = DataGenerator.getRandomFirstName();
        Courier courier = new Courier(login, password, firstName);

        courierClient.create(courier)
                .then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));

        return courier;
    }

    @Step("Логин курьера и получение ID")
    protected int loginAndGetId(Courier courier) {
        return courierClient.login(courier.getLogin(), courier.getPassword())
                .then()
                .extract()
                .path("id");
    }

    @Step("Создание заказа и получение его ID")
    protected int createOrderAndGetId(Order order) {
        int track = orderClient.create(order)
                .then()
                .statusCode(SC_CREATED)
                .extract()
                .path("track");

        return orderClient.getOrderByTrack(track)
                .then()
                .extract()
                .path("order.id");
    }

    @Step("Создание заказа и получение трека")
    protected int createOrderAndGetTrack(Order order) {
        return orderClient.create(order)
                .then()
                .statusCode(SC_CREATED)
                .extract()
                .path("track");
    }
}