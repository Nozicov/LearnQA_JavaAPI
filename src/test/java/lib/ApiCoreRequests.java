package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiCoreRequests {

    @Step("Make a GET-request with cookie and token")
    public Response makeGetRequestAuth(String url, String token, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request not auth")
    public Response makeGetRequestNotAuth(String url){
        return given()
                .filter(new AllureRestAssured())
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with auth cookie only")
    public Response makeGetRequestWithCookie(String url, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with auth token only")
    public Response makeGetRequestWithToken(String url, String token){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();
    }

    @Step("Make a POST-request")
    public Response makePostRequest(String url, Map<String,String> Data){
        return given()
                .filter(new AllureRestAssured())
                .body(Data)
                .post(url)
                .andReturn();
    }

    @Step("Make a PUT-request")
    public Response makePutRequestAuth(String url, Map<String,String> Data, String token, String cookie){
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .body(Data)
                .put(url)
                .andReturn();
    }

    @Step("Make a PUT-request")
    public Response makePutRequestNotAuth(String url, Map<String,String> Data){
        return given()
                .filter(new AllureRestAssured())
                .body(Data)
                .put(url)
                .andReturn();
    }

}
