package ru.yandex.praktikum.courier;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import ru.yandex.praktikum.BaseTest;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest extends BaseTest {

    @Test
    @DisplayName("Авторизация с правильным логином курьера")
    public void loginCourierSuccess() {
        courierClient.login(courier.getLogin(), courier.getPassword())
                .then()
                .statusCode(SC_OK)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("Авторизация логина с неверным паролем")
    public void loginCourierWrongPassword() {
        courierClient.login(courier.getLogin(), "wrongPassword")
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Авторизации с неверным логином")
    public void loginCourierWrongLogin() {
        courierClient.login("wrongLogin", courier.getPassword())
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Авторизация без пароля")
    public void loginCourierNoPassword() {
        courierClient.login(courier.getLogin(), "")
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
    @Test
    @DisplayName("Авторизация без логина")
    public void loginCourierWithoutLogin() {
        courierClient.login("", courier.getPassword())
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}