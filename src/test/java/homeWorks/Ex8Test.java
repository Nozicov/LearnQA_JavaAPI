package homeWorks;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;

public class Ex8Test {

    @Test
    public void testTokens() throws InterruptedException {
        JsonPath createJob = RestAssured
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        String tokenJob = createJob.get("token");
        int timeJob = createJob.get("seconds");
        int timeJobMillisec = timeJob * 1000;

        if (tokenJob == null){
            System.out.println("Not correct create new job! Received: 'token' in response");
        }
        else {
            System.out.println("Successfully create new job! Token: '" + tokenJob + ", seconds: " + timeJob );
        }

        JsonPath jobIsNotReady = RestAssured
                .given()
                .queryParam("token", tokenJob)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        String receivedStatusNotReady = jobIsNotReady.get("status");
        String expectedStatusNotReady = "Job is NOT ready";
        if (!receivedStatusNotReady.equals(expectedStatusNotReady)){
            System.out.println("Not correct status new job! Received: '" + receivedStatusNotReady + "', Expected: '" + expectedStatusNotReady +"'");
        }
        else {
            System.out.println("Correct status new job! Status: '" + receivedStatusNotReady + "'");
        }

        Thread.sleep(timeJobMillisec);
        JsonPath jobIsReady = RestAssured
                .given()
                .queryParam("token", tokenJob)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        String receivedStatusIsReady = jobIsReady.get("status");
        String receivedResultIsReady = jobIsReady.get("result");
        String expectedStatusIsReady = "Job is ready";
        if (!receivedStatusIsReady.equals(expectedStatusIsReady)){
            System.out.println("Not correct status finish job! Received: '" + receivedStatusIsReady + "', Expected: '" + expectedStatusIsReady +"'");
            if (receivedResultIsReady == null){
                System.out.println("Not status in Response");
            }
        }
        else {
            System.out.println("Correct status finish job! Status: '" + receivedStatusIsReady +"' and result: " + receivedResultIsReady);
        }

    }

    @Test
    public void testNotCorrectToken(){
        String notCorrectToken = String.valueOf(Math.random());
        JsonPath response = RestAssured
                .given()
                .queryParam("token", notCorrectToken)
                .when()
                .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                .jsonPath();
        String receivedError  = response.get("error");
        String expectedError  = "No job linked to this token";
        if (!receivedError.equals(expectedError)){
            System.out.println("Not correct error for not correct token! Received: '" + receivedError + "', Expected: '" + expectedError +"'");
        }
        else {
            System.out.println("Correct error message for not correct token: '" + receivedError + "'");
        }
    }
}
