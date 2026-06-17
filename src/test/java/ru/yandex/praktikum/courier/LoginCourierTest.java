package ru.yandex.praktikum.courier;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;
import ru.yandex.praktikum.BaseTest;
import ru.yandex.praktikum.models.Courier;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest extends BaseTest {

    @Step("Отправка запроса на логин курьера с логином: {login}")
    public Response loginCourierRequest(String login, String password) {
        return given()
                .header("Content-type", "application/json")
                .body(new Courier(login, password))
                .post("/api/v1/courier/login");
    }

    @Test
    @Step("Проверка успешного логина курьера")
    public void loginCourierSuccess() {
        Courier courier = createCourier();

        Response loginResponse = loginCourierRequest(courier.getLogin(), courier.getPassword());

        loginResponse.then()
                .statusCode(200)
                .body("id", notNullValue());

        courierId = loginResponse.then().extract().path("id");
    }

    @Test
    @Step("Проверка логина с неверным паролем")
    public void loginCourierWrongPassword() {
        Courier courier = createCourier();

        Response loginResponse = loginCourierRequest(courier.getLogin(), courier.getPassword());
        courierId = loginResponse.then().extract().path("id");

        loginCourierRequest(courier.getLogin(), "wrongPassword")
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Step("Проверка авторизации с неверным логином")
    public void loginCourierWrongLogin() {
        Courier courier = createCourier();

        Response loginResponse = loginCourierRequest(courier.getLogin(), courier.getPassword());
        courierId = loginResponse.then().extract().path("id");

        loginCourierRequest("wrongLogin", courier.getPassword())
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @Step("Проверка логина без пароля")
    public void loginCourierNoPassword() {
        Courier courier = createCourier();

        Response loginResponse = loginCourierRequest(courier.getLogin(), courier.getPassword());
        courierId = loginResponse.then().extract().path("id");

        loginCourierRequest(courier.getLogin(), "")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}