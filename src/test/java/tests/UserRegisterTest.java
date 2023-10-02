package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Epic("Кейсы по регистрации")
@Feature("Регистрация пользователя")
public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    String url = "https://playground.learnqa.ru/api/user/";

    @Test
    @Description("Негативный тест регистрации пользователя с существующим email")
    @DisplayName("Email already exists")
    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";

        Map<String,String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makeCreateUser(
                url,
                userData
        );

        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
    }

    @Test
    @Description("Негативный тест регистрации пользователя с некорректным email")
    @DisplayName("Invalid email format")
    public void testCreateUserNotCorrectEmail(){
        Map<String,String> userData = new HashMap<>();
        userData.put("email", "vinkotovexample.com");
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makeCreateUser(
                url,
                userData
        );

        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
    }

    @Test
    @Description("Негативный тест регистрации пользователя с коротким именем")
    @DisplayName("Username field is too short")
    public void testCreateUserShortName(){
        Map<String,String> userData = new HashMap<>();
        userData.put("username", "v");
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makeCreateUser(
                url,
                userData
        );

        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too short");
        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
    }

    @Test
    @Description("Негативный тест регистрации пользователя с длинным именем")
    @DisplayName("Username field is too long")
    public void testCreateUserLongName(){
        Map<String,String> userData = new HashMap<>();
        userData.put("username", DataGenerator.getRandomText(251));
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makeCreateUser(
                url,
                userData
        );

        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too long");
        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
    }


    @ParameterizedTest
    @ValueSource(strings = {"username", "firstName", "lastName", "email", "password"})
    public void testValidateVariables(String notVariable){
        Map<String,String> userData = new HashMap<>();
        userData.put(notVariable, null);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makeCreateUser(
                url,
                userData
        );

        Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: " + notVariable);
        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
    }


    @Test
    @Description("Позитивный тест регистрации пользователя")
    @DisplayName("Успешная регистрация пользователя")
    public void testCreateUserSuccessfully(){
        Map<String,String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makeCreateUser(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHashField(responseCreateAuth, "id");
    }

}
