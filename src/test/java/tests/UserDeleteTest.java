package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Epic("Кейсы по авторизации")
@Feature("Авторизация пользователя")
public class UserDeleteTest extends BaseTestCase {
    String cookie;
    String header;
    protected final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("Позитивный тест удаления пользователя")
    @DisplayName("Успешное удаление пользователя")
    public void testDeleteUser(){

        //GENERATE USER
        Map<String,String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        String userId = responseCreateAuth.jsonPath().getString("id");

        // LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData
        );

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");

        //DELETE
        Response deleteUser = apiCoreRequests.makeDeleteRequestAuth(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie
        );
        deleteUser.prettyPrint();

        Assertions.assertResponseCodeEquals(deleteUser, 200);

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequestAuth(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie
        );

        Assertions.assertResponseCodeEquals(responseUserData, 404);
        Assertions.assertResponseTextEquals(responseUserData, "User not found");

    }
    @Test
    @DisplayName("Негативный тест по удалению резервного пользователя")
    @Description("Попытка удалить зарезервированного пользователя")
    public void testDeletedLimitUser(){
        // LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData
        );

        String userId = responseGetAuth.jsonPath().getString("user_id");

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");

        // DELETE
        Response deleteUser = apiCoreRequests.makeDeleteRequestAuth(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie
        );

        Assertions.assertResponseCodeEquals(deleteUser, 400);
        Assertions.assertResponseTextEquals(deleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");
    }

    @Test
    @Description("Негативный тест по удалению другого пользователя")
    @DisplayName("Попытка удалить другого пользователя")
    public void testDeleteOtherUserAuth(){

        //GENERATE USER
        Map<String,String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        int userId = responseCreateAuth.jsonPath().getInt("id");
        int userOldId = userId - 1;

        // LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/login",
                authData
        );

        this.cookie = this.getCookie(responseGetAuth, "auth_sid");
        this.header = this.getHeader(responseGetAuth, "x-csrf-token");

        // DELETE
        Response deleteUser = apiCoreRequests.makeDeleteRequestAuth(
                "https://playground.learnqa.ru/api/user/" + userOldId,
                header,
                cookie
        );

        Assertions.assertResponseCodeEquals(deleteUser, 200);

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequestAuth(
                "https://playground.learnqa.ru/api/user/" + userOldId,
                header,
                cookie
        );

        Assertions.assertResponseCodeEquals(responseUserData, 200);
        Assertions.assertJsonHashField(responseUserData, "username");
    }
}
