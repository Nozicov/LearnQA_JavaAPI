import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Ex11Test {

    @Test
    public void testGetCookie(){
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        Map<String,String> responseCookies = response.getCookies();
        System.out.println(responseCookies);

        assertEquals(200, response.statusCode(), "Service response code is not equal to 200");
        assertFalse(responseCookies.isEmpty(), "Not cookies");
        assertEquals(responseCookies.get("HomeWork"), "hw_value", "HomeWork cookie value is not equal to hw_value");

    }
}
