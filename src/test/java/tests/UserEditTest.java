package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {
    String cookie;
    String header;
    int userIdAuth;
    @Test
    public void testEditJustCreatedTest(){

        //GENERATE USER
        Map<String,String> userData = DataGenerator.getRegistrationData();

        JsonPath resposeCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        String userId = resposeCreateAuth.getString("id");

        // LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");

        //EDIT
        String newName = "newName";
        Map<String,String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", header)
                .cookie("auth_sid", cookie)
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData, "firstName", newName);

    }
}
