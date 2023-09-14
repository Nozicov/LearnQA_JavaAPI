import groovyjarjarantlr4.v4.codegen.model.SrcOp;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HelloWorldTest {

    @Test
    public void testPrintHello(){
        System.out.println("Hello from NEE");
    }

    @Test
    public void testGetHello(){
        Map<String, String> params = new HashMap<>();
        params.put("name", "NEE");

        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();
        String name = response.get("answe2r");
        if (name == null){
            System.out.println("Not answer");
        }
        else {
            System.out.println(name);
        }
    }

    @Test
    public void testGetCheckType(){
        Response response = RestAssured
                .given()
                .queryParam("param1", "value1")
                .queryParam("param2", "value2")
                .get("https://playground.learnqa.ru/api/check_type")
                .andReturn();
        int statusCode = response.statusCode();
        System.out.println("statusCode: " + statusCode);
        response.print();
    }

    @Test
    public void testPostCheckType(){
        Map<String, Object> body = new HashMap<>();
        body.put("param1", "value1");
        body.put("param2", "value2");

        Response response = RestAssured
                .given()
                .body(body)
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();
        response.print();
    }

    @Test
    public void testGetText(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    public void testGet500(){
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_500")
                .andReturn();
        int statusCode = response.statusCode();
        System.out.println("statusCode: " + statusCode);
    }

    @Test
    public void testGet303(){
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();
        int statusCode = response.statusCode();
        System.out.println("statusCode: " + statusCode);
        response.print();

        String locationHeader = response.getHeader("Location");
        System.out.println("Location: " + locationHeader);


    }

    @Test
    public void testAllHeaders(){
        Map<String,String> headers = new HashMap<>();
        headers.put("MyHeader1", "head1");
        headers.put("MyHeader2", "head2");

        Response response = RestAssured
                .given()
                .headers(headers)
                .when()
                .get("https://playground.learnqa.ru/api/show_all_headers")
                .andReturn();
        response.prettyPrint();

        Headers responseHeaders = response.getHeaders();
        System.out.println(responseHeaders);


    }

    @Test
    public void testGetAuthCookie(){
        HashMap<String,String> data = new HashMap<>();
        data.put("login","secret_login");
        data.put("password", "secret_pass");

        Response responseForGet = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();
        System.out.println("\n Response Body:");
        responseForGet.prettyPrint();

        System.out.println("\n Response Headers:");
        Headers responseHeaders = responseForGet.getHeaders();
        System.out.println(responseHeaders);

        System.out.println("\n Response Cookie:");
        Map<String, String> responseCookie = responseForGet.getCookies();
        System.out.println(responseCookie);

        System.out.println("\n Response Cookie auth_cookie:");
        String responseAuthCookie = responseForGet.getCookie("auth_cookie");
        System.out.println(responseAuthCookie);

        Map<String, String> cookies = new HashMap<>();
        if (responseAuthCookie != null){
            cookies.put("auth_cookie", responseAuthCookie);
        }

        Response responseForCheck = RestAssured
                .given()
                .body(data)
                .cookies(cookies)
                .when()
                .get("https://playground.learnqa.ru/api/check_auth_cookie")
                .andReturn();

        responseForCheck.print();
    }

}
