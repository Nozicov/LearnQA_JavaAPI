import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

public class Ex5Test {

    @Test
    public void testGetJsonHomeWork(){
        JsonPath resoponse = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();
        System.out.println("Response:");
        resoponse.prettyPrint();

        System.out.println("Text message two:");
        String message = resoponse.get("messages.message[1]").toString();
        System.out.println(message);
    }
}
