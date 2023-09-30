package tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserRegisterTest extends BaseTestCase {

    String email = "vinkotov@example.com";
    String password = "123";
    String username = "vinkotov";
    String firstName = "vinkotov";
    String lastName = "vinkotov";


    @Test
    public void testCreateUserWithExistingEmail(){
        Map<String,String> userData = new HashMap<>();
        userData.put("email", email);
        userData.put("password", password);
        userData.put("username", username);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
    }

    @Test
    public void testCreateUserSuccessfully(){
        String randomEmail = DataGenerator.getRandomEmail();

        Map<String,String> userData = new HashMap<>();
        userData.put("email", randomEmail);
        userData.put("password", password);
        userData.put("username", username);
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);

        Response responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .andReturn();

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHashField(responseCreateAuth, "id");
    }

}
