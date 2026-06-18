package ru.yandex.praktikum.courier;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import ru.yandex.praktikum.BaseTest;
import ru.yandex.praktikum.generators.DataGenerator;
import ru.yandex.praktikum.models.Courier;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest extends BaseTest {

    @Test
    @DisplayName("Успешное создание курьера")
    public void createCourierSuccess() {
        String login = DataGenerator.getRandomLogin();
        String password = DataGenerator.getRandomPassword();
        String firstName = DataGenerator.getRandomFirstName();
        Courier courier = new Courier(login, password, firstName);

        courierClient.create(courier)
                .then()
                .statusCode(SC_CREATED)
                .body("ok", equalTo(true));
    }
    @Test
    @DisplayName("Создание курьера без логина")
    public void createCourierNoLogin() {
        Courier courier = new Courier("", "password123", "Naruto15");
        courierClient.create(courier)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    public void createCourierNoPassword() {
        Courier courier = new Courier("Sakura1010", null, "Naruto15");
        courierClient.create(courier)
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера с уже существующим логином")
    @Description("Баг API: ОР: Этот логин уже используется / ФР: Этот логин уже используется. Попробуйте другой.")
    public void createCourierDuplicateLogin() {
        String login = DataGenerator.getRandomLogin();
        String password = DataGenerator.getRandomPassword();
        String firstName = DataGenerator.getRandomFirstName();

        Courier courier = new Courier(login, password, firstName);

        // первый запрос — успешное создание
        courierClient.create(courier).then().statusCode(SC_CREATED);

        // второй запрос — ошибка 409
        courierClient.create(courier)
                .then()
                .statusCode(SC_CONFLICT)
                .body("message", equalTo("Этот логин уже используется"));
        // тест падает, так как текст сообщения отличается от документации
    }
}