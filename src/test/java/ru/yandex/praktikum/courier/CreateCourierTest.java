package ru.yandex.praktikum.courier;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;
import ru.yandex.praktikum.BaseTest;
import ru.yandex.praktikum.generators.DataGenerator;
import ru.yandex.praktikum.models.Courier;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest extends BaseTest {

    @Step("Отправка запроса на создание курьера")
    public Response createCourierRequest(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .post("/api/v1/courier");
    }

    @Step("Отправка запроса на логин курьера")
    public Response loginCourier(String login, String password) {
        return given()
                .header("Content-type", "application/json")
                .body(new Courier(login, password))
                .post("/api/v1/courier/login");
    }
    @Test
    public void createCourierSuccess() { // тест на создание курьера (успешный)
        String login = DataGenerator.getRandomLogin();
        String password = DataGenerator.getRandomPassword();
        String firstName = DataGenerator.getRandomFirstName();
        Courier courier = new Courier(login, password, firstName);

        createCourierRequest(courier)
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        Response loginResponse = loginCourier(login, password);
        courierId = loginResponse.then().extract().path("id");
    }
    @Test
    public void createCourierNoLogin() { // тест на создание курьера с пустым логином
        Courier courier = new Courier("", "password123", "Naruto15");
        createCourierRequest(courier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
    // id не создаётся, следовательно, удалять не нужно

    @Test
    public void createCourierNoPassword() { // тест на создание курьера с пустым паролем
        Courier courier = new Courier("Sakura1010", null, "Naruto15");
        createCourierRequest(courier)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
        // id не создаётся, следовательно, удалять не нужно
    }
    @Description("Баг API: ОР: Этот логин уже используется / ФР: Этот логин уже используется. Попробуйте другой.")
    @Test
    public void createCourierDuplicateLogin() { // тест в котором проверяем реакцию программы на дубликат логина
        String login = DataGenerator.getRandomLogin();
        String password = DataGenerator.getRandomPassword();
        String firstName = DataGenerator.getRandomFirstName();

        Courier courier = new Courier(login, password, firstName);

        // первый запрос — успешное создание
        createCourierRequest(courier).then().statusCode(201);

        Response loginResponse = loginCourier(login, password);
        courierId = loginResponse.then().extract().path("id");

        // второй запрос — ошибка 409
        createCourierRequest(courier)
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется"));
        // тест падает, так как текст сообщения отличается от документации
    }
}