package org.example.steps;

import cucumber.api.java.ru.Дано;
import cucumber.api.java.ru.И;
import cucumber.api.java.ru.Когда;
import cucumber.api.java.ru.Тогда;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.example.RestRequests;
import org.junit.Assert;

@Slf4j
public class Booking {
    private static final String BASE_URI = "https://restful-booker.herokuapp.com";
    private static final String JSON_TOKEN_PATH = "token";
    private static final String JSON_REASON_PATH = "reason";
    private static final String BOOKING_ID_PATH = "bookingid";

    private String username;
    private String password;
    private String token;

    private String checkinDate;
    private String checkoutDate;
    private String updatedCheckoutDate;
    private String bookingId;
    private String errorReason;

    @Дано("^Логин (.*?) и пароль (.*?) пользователя$")
    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Когда("^Пользователь получает токен$")
    public void getToken() {
        token = RestRequests.getToken(BASE_URI, username, password, JSON_TOKEN_PATH);
        Assert.assertNotNull(token);
        log.info("Получили токен и вывели в консоль: {}", token);
    }

    @Тогда("^Токен сохраняется$")
    public void tokenSaved() {
        Assert.assertNotNull(token);
        log.info("Токен сохранен {}", token);
    }

    @Дано("^Даты въезда (.*?) и выезда (.*?)$")
    public void setBodyToCreateBooking(String checkinDate, String checkoutDate) {
        this.checkinDate = checkinDate;
        this.checkoutDate = checkoutDate;
    }

    @Когда("^Отправляем запрос на создание бронирования$")
    public void sendCreateBooking() {
        final String createBookingBody = "{\n" +
                "    \"firstname\" : \"Jim\",\n" +
                "    \"lastname\" : \"Brown\",\n" +
                "    \"totalprice\" : 111,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"" + this.checkinDate + "\",\n" +
                "        \"checkout\" : \"" + this.checkoutDate + "\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";

        bookingId = getBookingId(RestRequests.createBooking(BASE_URI, createBookingBody, 200));
        log.info("Бронирование создано: id = {}", bookingId);
    }

    @Тогда("^Проверка бронирование существует$")
    public void checkBooking() {
        log.info("Проверка бронирования с id = {}", bookingId);
        RestRequests.getBooking(BASE_URI, bookingId, 200);
    }

    @Когда("^Пользователь получает токен ошибку при запросе токена$")
    public void getTokenWithWrongCredentials() {
        errorReason = RestRequests.getToken(BASE_URI, "admin", "password12", JSON_REASON_PATH);
        log.info("Получение токена(неверные логин/пароль): {}", errorReason);
    }


    @Тогда("^Текст ошибки (.*?)$")
    public void checkErrorText(String errorText) {
        Assert.assertEquals(errorText, this.errorReason);
    }

    @Когда("^Отправляем запрос на создание бронирования с некорректным телом$")
    public void createBookingWithWrongBody() {
        String createBookingWrongBody = "{\n" +
                "    \"lastname\" : \"Brown\",\n" +
                "    \"totalprice\" : 111,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2022-09-01\",\n" +
                "        \"checkout\" : \"2022-09-09\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";
        Response bookingResponse = RestRequests.createBooking(BASE_URI, createBookingWrongBody, 500);
        errorReason = bookingResponse.statusLine();

        log.info("Создание бронирования(неверное тело)");
    }

    @И("^Новая дата выезда (.*?)$")
    public void setNewCheckoutDate(String updatedCheckoutDate) {
        this.updatedCheckoutDate = updatedCheckoutDate;
    }

    @И("^Отправляет запрос на обновление бронирования$")
    public void updateBooking() {
        log.info("Обновление бронирования с id = {}", this.bookingId);

        final String updateBookingBody = "{\n" +
                "    \"firstname\" : \"James\",\n" +
                "    \"lastname\" : \"Brown\",\n" +
                "    \"totalprice\" : 111,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2022-10-10\",\n" +
                "        \"checkout\" : \"" + this.updatedCheckoutDate + "\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";

        RestRequests.updateBooking(BASE_URI, this.bookingId, this.token, updateBookingBody);
    }

    @И("^Удаляет бронирование$")
    public void deleteBooking() {
        log.info("Удаление бронирования с id = {}", this.bookingId);
        RestRequests.deleteBooking(BASE_URI, bookingId, token, 201);
    }

    @Тогда("^Проверяет бронирование не существует$")
    public void checkIfBookingDeleted() {
        log.info("Проверка удаления бронирования с id = {}", this.bookingId);
        RestRequests.getBooking(BASE_URI, this.bookingId, 404);
    }

    @Дано("^Несуществующий ID бронирования (.*?)$")
    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    @Когда("^Проверка бронирование не существует$")
    public void checkBookingWithWrongId() {
        log.info("Проверка бронирования с несуществующим id");
        this.errorReason = RestRequests.getBooking(BASE_URI, this.bookingId, 404);
    }

    @Когда("^Удаляет бронирование с несуществующим ID$")
    public void deleteBookingWithWrongId() {
        log.info("Удаление бронирования с несуществующим id = {}", this.bookingId);
        this.errorReason = RestRequests.deleteBooking(BASE_URI, this.bookingId, token, 405);
    }

    private String getBookingId(Response response) {
        return response.jsonPath().get(BOOKING_ID_PATH).toString();
    }
}
