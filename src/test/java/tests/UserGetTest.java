package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestCase {

    String cookie;
    String header;
    int userIdAuth;

    @Test
    public void testGetUserDataNotAuth(){
        Response response = RestAssured
                .given()
                .get("https://playground.learnqa.ru/api/user/1")
                .andReturn();

        Assertions.assertJsonHashField(response, "username");
        Assertions.assertJsonHasNotField(response, "id");
        Assertions.assertJsonHasNotField(response, "firstName");
        Assertions.assertJsonHasNotField(response, "lastName");
    }

    @Test
    public void testGetUserDetailsAuthAsSameUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
        this.userIdAuth = this.getIntFromJson(responseGetAuth, "user_id");

        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/" + userIdAuth)
                .andReturn();

        String[] expectedField = {"id", "username", "email", "firstName", "lastName"};
        Assertions.assertJsonHashFields(responseUserData, expectedField);
    }
}


