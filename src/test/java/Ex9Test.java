import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

public class Ex9Test {
    String login = "super_admin";
    String currentDir = System.getProperty("user.dir");
    List<String> ListPass = new ArrayList<>();

    @Test
    public void testPassword(){

        try {
            File file = new File(currentDir + "/src/test/java/passwords.txt");
            //создаем объект FileReader для объекта File
            FileReader fr = new FileReader(file);
            //создаем BufferedReader с существующего FileReader для построчного считывания
            BufferedReader reader = new BufferedReader(fr);
            // считаем сначала первую строку
            String line = reader.readLine();
            ListPass.add(line);
            while (line != null) {
                // считываем остальные строки в цикле
                line = reader.readLine();
                ListPass.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < ListPass.size() - 1 ; i++ ){
            Map<String,String> body = new HashMap<>();
            body.put("login", login);
            body.put("password", ListPass.get(i));

            Response responseLogin = RestAssured
                    .given()
                    .body(body)
                    .when()
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();
            Map<String,String> responseCookies = responseLogin.getCookies();


            Response responseCheck = RestAssured
                    .given()
                    .cookies(responseCookies)
                    .when()
                    .get("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();
            String check = responseCheck.body().asString();

            if (!check.equals("You are NOT authorized")){
                if (check.equals("You are authorized")){
                    System.out.println("Password: " + ListPass.get(i));
                    break;
                }
                else {
                    System.out.println("Error response: " + check);
                }
            }

        }

    }
}
