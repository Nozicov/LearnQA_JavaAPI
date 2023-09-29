package homeWorks;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class Ex6Test {

    @Test
    public void testLongRedirect(){
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();
        response.prettyPrint();

        String redirectUrl = response.getHeader("Location");
        System.out.println("Redirect Url 01: " + redirectUrl);
    }

    @Test
    public void testRedirectPlayGround(){
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/")
                .andReturn();
        response.prettyPrint();

        String redirectUrl = response.getHeader("Location");
        System.out.println("Redirect Url 02: " + redirectUrl);
    }
}
