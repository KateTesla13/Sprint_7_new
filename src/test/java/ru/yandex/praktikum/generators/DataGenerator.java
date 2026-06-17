package ru.yandex.praktikum.generators;

import java.util.UUID;

public class DataGenerator {

    public static String getRandomLogin() {
        return "login_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String getRandomPassword() {
        return "pass_" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String getRandomFirstName() {
        return "name_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
