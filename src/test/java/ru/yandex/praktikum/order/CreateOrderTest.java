package ru.yandex.praktikum.order;

import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import ru.yandex.praktikum.BaseTest;
import ru.yandex.praktikum.models.Order;
import java.util.List;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest extends BaseTest {

    private final List<String> colors;
    private final String testName;

    public CreateOrderTest(List<String> colors, String testName) {
        this.colors = colors;
        this.testName = testName;
    }

    @Parameterized.Parameters(name = "{1}")
    public static Object[][] getColorData() {
        return new Object[][]{
                {null, "Без цвета"},
                {List.of("BLACK"), "Только BLACK"},
                {List.of("GREY"), "Только GREY"},
                {List.of("BLACK", "GREY"), "BLACK и GREY"}
        };
    }
        @Test
        @DisplayName("Создание заказа с разными вариантами цвета")
        public void createOrderWithColor() {
            Order order = new Order(
                    "Naruto", "Uzumaki", "Konoha, 142 apt.", 4, "+7 800 355 35 35", 5, "2020-06-06", "Saske, come back to Konoha", colors
            );

            orderClient.create(order)
                    .then()
                    .statusCode(SC_CREATED)
                    .body("track", notNullValue());
        }
    }
