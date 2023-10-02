package tests;

import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestCase {

    String cookie;
    String header;
    int userIdAuth;

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testGetUserDataNotAuth(){
        Response response = apiCoreRequests.makeGetRequestNotAuth(
                "https://playground.learnqa.ru/api/user/1"
        );

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

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData
        );

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
        this.userIdAuth = this.getIntFromJson(responseGetAuth, "user_id");

        Response responseUserData = apiCoreRequests.makeGetRequestAuth(
                        "https://playground.learnqa.ru/api/user/" + userIdAuth,
                        header,
                        cookie
                );

        String[] expectedField = {"id", "username", "email", "firstName", "lastName"};
        Assertions.assertJsonHashFields(responseUserData, expectedField);
    }

    @Test
    public void testGetUserDataProtection(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData
        );

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
        this.userIdAuth = this.getIntFromJson(responseGetAuth, "user_id");

        int oldUser = userIdAuth - 1;

        Response responseUserData = apiCoreRequests.makeGetRequestAuth(
                "https://playground.learnqa.ru/api/user/" + oldUser,
                header,
                cookie
        );

        Assertions.assertJsonHashField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "id");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
    }
}


