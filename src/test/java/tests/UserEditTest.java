package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Кейсы по редактированию")
@Feature("Редактирование пользователя")
public class UserEditTest extends BaseTestCase {
    String cookie;
    String header;

    protected final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Description("Позитивный тест редактирования пользователя")
    @DisplayName("Успешное редактирование пользователя")
    public void testEditJustCreated(){

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

        //EDIT
        String newName = "newName";
        Map<String,String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestAuth(
                "https://playground.learnqa.ru/api/user/",
                editData,
                header,
                cookie
        );

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequestAuth(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie
                );

        Assertions.assertJsonByName(responseUserData, "firstName", newName);

    }

    @Test
    @Description("Негативный тест редактирования пользователя без авторизации")
    @DisplayName("Попытка редактирования пользователя без авторизации")
    public void testEditUserNotAuth(){

        //GENERATE USER
        Map<String,String> userData = DataGenerator.getRegistrationData();
        String username = userData.get("username");

                Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        String userId = responseCreateAuth.jsonPath().getString("id");

        //EDIT
        String newUsername = "newName" + userId;
        userData.put("username", newUsername);

        Response responseEditUser = apiCoreRequests.makePutRequestNotAuth(
                "https://playground.learnqa.ru/api/user/" + userId,
                userData
        );

        Assertions.assertResponseCodeEquals(responseEditUser,400);
        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");

        //GET
        Response responseUserData = apiCoreRequests.makeGetRequestAuth(
                "https://playground.learnqa.ru/api/user/" + userId,
                header,
                cookie
        );

        Assertions.assertJsonHashNotFields(responseUserData, new String[]{"firstName", "email", "password", "lastName"});
        Assertions.assertJsonByName(responseUserData, "username", username);
    }

    @Test
    @Description("Негативный тест редактирования другого пользователя")
    @DisplayName("Попытка редактирования другого пользователя")
    public void testEditOtherUserAuth(){

        //GENERATE USER
        Map<String,String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequest(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        String userName = userData.get("username");
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

        //EDIT
        String newUserName = "username" + userId;
        Map<String,String> editData = new HashMap<>();
        editData.put("username", newUserName);

        Response responseEditUser = apiCoreRequests.makePutRequestAuth(
                "https://playground.learnqa.ru/api/user/" + userOldId,
                editData,
                header,
                cookie
        );

        Assertions.assertResponseCodeEquals(responseEditUser,200);

        //GET OLD USER
        Response responseOldUserData = apiCoreRequests.makeGetRequestAuth(
                "https://playground.learnqa.ru/api/user/" + userOldId,
                header,
                cookie
        );
        String oldUsername = responseOldUserData.jsonPath().getString("username");

        Assertions.assertJsonHashNotFields(responseOldUserData, new String[]{"firstName", "email", "password", "lastName"});
        assertNotEquals(oldUsername, newUserName, "One user was able to change another user's data");
    }

    @Test
    @Description("Негативный тест редактирования пользователя с некорректным email")
    @DisplayName("Попытка редактирования пользователя с некорректным email")
    public void testEditUserNotCorrectEmail(){

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

        //EDIT
        String newEmail = "email" + userId + "_example.com";
        Map<String,String> editData = new HashMap<>();
        editData.put("email", newEmail);

        Response responseEditUser = apiCoreRequests.makePutRequestAuth(
                "https://playground.learnqa.ru/api/user/",
                editData,
                header,
                cookie
        );

        Assertions.assertResponseCodeEquals(responseEditUser,400);
        Assertions.assertResponseTextEquals(responseEditUser, "Invalid email format");
    }

    @Test
    @Description("Негативный тест редактирования пользователя с коротким именем")
    @DisplayName("Попытка редактирования пользователя с коротким firstName ")
    public void testEditUserSmallFirstName(){

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

        //EDIT
        Map<String,String> editData = new HashMap<>();
        editData.put("firstName", "a");

        Response responseEditUser = apiCoreRequests.makePutRequestAuth(
                "https://playground.learnqa.ru/api/user/",
                editData,
                header,
                cookie
        );

        Assertions.assertResponseCodeEquals(responseEditUser,400);
        Assertions.assertJsonByName(responseEditUser, "error", "Too short value for field firstName");
    }

}
