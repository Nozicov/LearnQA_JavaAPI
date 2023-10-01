package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Кейсы по авторизации")
@Feature("Авторизация пользователя")
public class UserAuthTest extends BaseTestCase {

    String cookie;
    String header;
    int userIdAuth;

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();


    @BeforeEach
    public void loginUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");
        this.userIdAuth = this.getIntFromJson(responseGetAuth, "user_id");
    }

    @Test
    @Description("Тест для авторизации пользователя")
    @DisplayName("Позитивный тест авторизации пользователя")
    public void testAuthUser(){
        Response responseCheckAuth = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/auth", this.header, this.cookie);
        Assertions.assertIntJsonByName(responseCheckAuth, "user_id",  this.userIdAuth);
    }

    @Description("Тест для валидации сервиса авторизации пользователя")
    @DisplayName("Негативные тесты авторизации пользователя")
    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition){
        if(condition.equals("cookie")){
           Response responseForCheck = apiCoreRequests.makeGetRequestWithCookie(
                   "https://playground.learnqa.ru/api/user/auth",
                   this.cookie
           );
           Assertions.assertIntJsonByName(responseForCheck, "user_id", 0);
        }
        else if (condition.equals("headers")) {
            Response responseForCheck = apiCoreRequests.makeGetRequestWithToken(
                    "https://playground.learnqa.ru/api/user/auth",
                    this.header
            );
            Assertions.assertIntJsonByName(responseForCheck, "user_id", 0);
        }
        else {
            throw new IllegalArgumentException("Codition value is known: " + condition);
        }
    }
}
