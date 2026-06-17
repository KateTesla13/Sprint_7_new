package ru.yandex.praktikum.courier;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.junit.Test;
import ru.yandex.praktikum.BaseTest;
import ru.yandex.praktikum.models.Courier;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class DeleteCourierTest extends BaseTest {

    @Test
    @Step("Успешное удаление курьера по ID: {courierId}")
    public void deleteCourierSuccess() {
        Courier courier = createCourier();

        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body(new Courier(courier.getLogin(), courier.getPassword()))
                .post("/api/v1/courier/login");

        int courierId = loginResponse.then().extract().path("id");

        given()
                .delete("/api/v1/courier/" + courierId)
                .then()
                .statusCode(200)
                .body("ok", equalTo(true));
    }

    @Test
    @Description("Баг API: ОР: status code <400> / ФР: status code <404>")
    @Step("Удаление курьера без ID")
    public void deleteCourierWithoutId() {
        given()
                .delete("/api/v1/courier/")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для удаления курьера"));
    }

    @Test
    @Description("Баг API: ОР: Курьера с таким id нет / ФР: Курьера с таким id нет.")
    @Step("Удаление курьера с неверным ID: 999999")
    public void deleteCourierWithWrongId() {
        given()
                .delete("/api/v1/courier/999999")
                .then()
                .statusCode(404)
                .body("message", equalTo("Курьера с таким id нет"));
    }
}