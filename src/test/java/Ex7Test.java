import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

public class Ex7Test {

    @Test
    public void testLongRedirect(){

        int i = 0;
        String url = "https://playground.learnqa.ru/api/long_redirect";
        int statusCode = 0;

        while (statusCode != 200){
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .get(url)
                    .andReturn();
            statusCode = response.statusCode();
            url = response.getHeader("Location");
            if (statusCode != 200){
                System.out.println("Status Code Response: " + statusCode);
                System.out.println("Redirect Url: " + url);
                i = i + 1;
            }
        }
        System.out.println("Всего редиректов:" + i);
    }

}
