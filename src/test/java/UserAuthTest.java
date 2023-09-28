import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAuthTest {
        Map<String, String> authData = new HashMap<>();

    @Test
    public void testAuthUser(){
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");
        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();
        responseGetAuth.prettyPrint();

        Map<String,String> cookies = responseGetAuth.getCookies();
        Headers headers = responseGetAuth.getHeaders();
        int userIdOnAuth = responseGetAuth.jsonPath().getInt("user_id");

        assertEquals(200, responseGetAuth.getStatusCode());
        assertTrue(cookies.containsKey("auth_sid"));
        assertTrue(headers.hasHeaderWithName("x-csrf-token"));
        assertTrue(responseGetAuth.jsonPath().getInt("user_id") > 0);

        JsonPath responseCheckAuth = RestAssured
                .given()
                .cookie("auth_sid", responseGetAuth.getCookie("auth_sid"))
                .header("x-csrf-token", responseGetAuth.getHeader("x-csrf-token"))
                .get("https://playground.learnqa.ru/api/user/auth")
                .jsonPath();
        responseCheckAuth.prettyPrint();

        int userIdCheck = responseCheckAuth.getInt("user_id");

        assertTrue(userIdCheck > 0);
        assertEquals(userIdCheck, userIdOnAuth);


    }

    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition){
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        Map<String,String> cookies = responseGetAuth.getCookies();
        Headers headers = responseGetAuth.getHeaders();

        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");
        if(condition.equals("cookie")){
            spec.cookies("auth_sid", cookies.get("auth_sid"));
        } else if (condition.equals("headers")) {
            spec.header("x-csrf-token", headers.get("x-csrf-token"));
        } else {
            throw new IllegalArgumentException("Codition value is known: " + condition);
        }

        JsonPath responseForCheck = spec.get().jsonPath();
        assertEquals(0, responseForCheck.getInt("user_id"), "user_id not equals 0");
    }
}
