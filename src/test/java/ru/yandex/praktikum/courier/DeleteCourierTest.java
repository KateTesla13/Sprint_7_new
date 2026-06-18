package ru.yandex.praktikum.courier;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import ru.yandex.praktikum.BaseTest;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class DeleteCourierTest extends BaseTest {

    @Test
    @DisplayName("Успешное удаление курьера по ID")
    public void deleteCourierSuccess() {
        courierClient.delete(courierId)
                .then()
                .statusCode(SC_OK)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Удаление курьера без ID")
    @Description("Баг API: ОР: status code <400> / ФР: status code <404>")
    public void deleteCourierWithoutId() {
        courierClient.deleteWithoutId()
                .then()
                .statusCode(SC_BAD_REQUEST)
                .body("message", equalTo("Недостаточно данных для удаления курьера"));
    }

    @Test
    @DisplayName("Удаление курьера с неверным ID")
    @Description("Баг API: ОР: Курьера с таким id нет / ФР: Курьера с таким id нет.")
    public void deleteCourierWithWrongId() {
        courierClient.delete(999999)
                .then()
                .statusCode(SC_NOT_FOUND)
                .body("message", equalTo("Курьера с таким id нет"));
    }
}