package homeWorks;

import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Ex12Test {

    @Test
    public void testGetHeader(){
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        Headers responseHeaders = response.getHeaders();
        System.out.println(responseHeaders);

        assertEquals(200, response.statusCode(), "Service response code is not equal to 200");
        assertTrue(responseHeaders.size() > 0, "Not headers");
        assertEquals(responseHeaders.getValue("x-secret-homework-header"), "Some secret value", "Header x-secret-homework-header value is not equal to x-secret-homework-header");

    }
}
