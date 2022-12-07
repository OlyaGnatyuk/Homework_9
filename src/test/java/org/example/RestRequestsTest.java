package org.example;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class RestRequestsTest {
    private static final String BASE_URI = "https://restful-booker.herokuapp.com";

    private static final String CREATE_BOOKING_BODY = "{\n" +
            "    \"firstname\" : \"Jim\",\n" +
            "    \"lastname\" : \"Brown\",\n" +
            "    \"totalprice\" : 111,\n" +
            "    \"depositpaid\" : true,\n" +
            "    \"bookingdates\" : {\n" +
            "        \"checkin\" : \"2022-09-01\",\n" +
            "        \"checkout\" : \"2022-09-09\"\n" +
            "    },\n" +
            "    \"additionalneeds\" : \"Breakfast\"\n" +
            "}";

    private static final String CREATE_BOOKING_WRONG_BODY = "{\n" +
            "    \"lastname\" : \"Brown\",\n" +
            "    \"totalprice\" : 111,\n" +
            "    \"depositpaid\" : true,\n" +
            "    \"bookingdates\" : {\n" +
            "        \"checkin\" : \"2022-09-01\",\n" +
            "        \"checkout\" : \"2022-09-09\"\n" +
            "    },\n" +
            "    \"additionalneeds\" : \"Breakfast\"\n" +
            "}";
    private static final String UPDATE_BOOKING_BODY = "{\n" +
            "    \"firstname\" : \"James\",\n" +
            "    \"lastname\" : \"Brown\",\n" +
            "    \"totalprice\" : 111,\n" +
            "    \"depositpaid\" : true,\n" +
            "    \"bookingdates\" : {\n" +
            "        \"checkin\" : \"2022-10-10\",\n" +
            "        \"checkout\" : \"2022-10-13\"\n" +
            "    },\n" +
            "    \"additionalneeds\" : \"Breakfast\"\n" +
            "}";
    private static final String JSON_TOKEN_PATH = "token";
    private static final String JSON_REASON_PATH = "reason";
    private static final String BOOKING_ID_PATH = "bookingid";

    @Test
    public void testGetToken() {
        String token = RestRequests.getToken(BASE_URI, "admin", "password123", JSON_TOKEN_PATH);
        log.info("Получили токен и вывели в консоль: {}", token);
    }

    @Test
    public void testCreateAndCheckBooking() {
        String id = getBookingId(RestRequests.createBooking(BASE_URI, CREATE_BOOKING_BODY, 200));
        log.info("Бронирование создано: id = {}", id);

        log.info("Проверка бронирования с id = {}", id);
        RestRequests.getBooking(BASE_URI, id, 200);
    }

    @Test
    public void testUpdateBooking() {
        String token = RestRequests.getToken(BASE_URI, "admin", "password123", JSON_TOKEN_PATH);

        log.info("Создание бронирования:");
        String id = getBookingId(RestRequests.createBooking(BASE_URI, CREATE_BOOKING_BODY, 200));

        log.info("Обновление бронирования с id = {}", id);
        RestRequests.updateBooking(BASE_URI, id, token, UPDATE_BOOKING_BODY);

        log.info("Проверка бронирования с id = {}", id);
        RestRequests.getBooking(BASE_URI, id, 200);
    }

    @Test
    public void testDeleteBooking() {
        String token = RestRequests.getToken(BASE_URI, "admin", "password123", JSON_TOKEN_PATH);

        log.info("Создание бронирования:");
        String id = getBookingId(RestRequests.createBooking(BASE_URI, CREATE_BOOKING_BODY, 200));

        log.info("Удаление бронирования с id = {}", id);
        RestRequests.deleteBooking(BASE_URI, id, token, 201);

        log.info("Проверка удаления бронирования с id = {}", id);
        RestRequests.getBooking(BASE_URI, id, 404);
    }

    @Test
    public void testGetTokenWithWrongCredentials() {
        String errorReason = RestRequests.getToken(BASE_URI, "admin", "password12", JSON_REASON_PATH);
        log.info("Получение токена(неверные логин/пароль): {}", errorReason);
    }

    @Test
    public void testCreateBookingWithWrongBody() {
        Response bookingResponse = RestRequests.createBooking(BASE_URI, CREATE_BOOKING_WRONG_BODY, 500);

        log.info("Создание бронирования(неверное тело):");
        log.info(bookingResponse.statusLine());
    }

    @Test
    public void testCheckBookingWithWrongId() {
        RestRequests.getBooking(BASE_URI, "5000000", 404);
        log.info("Проверка бронирования с несуществующим id");
    }

    @Test
    public void testDeleteBookingWithWrongId() {
        String token = RestRequests.getToken(BASE_URI, "admin", "password123", JSON_TOKEN_PATH);

        String wrongBookingId = "5000000";
        RestRequests.deleteBooking(BASE_URI, wrongBookingId, token, 405);
        log.info("Удаление бронирования с несуществующим id = {}", wrongBookingId);
    }

    private String getBookingId(Response response) {
        return response.jsonPath().get(BOOKING_ID_PATH).toString();
    }
}