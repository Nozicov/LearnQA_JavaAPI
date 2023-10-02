package lib;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DataGenerator {

    public static String getRandomEmail(){
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        return "email" + timestamp + "@example.com";
    }

    public static Map<String ,String> getRegistrationData(){
        Map<String, String> data = new HashMap<>();
        data.put("email", DataGenerator.getRandomEmail());
        data.put("password", "123");
        data.put("username", "test");
        data.put("firstName", "test");
        data.put("lastName", "test");

        return data;
    }

    public static Map<String ,String> getRegistrationData(Map<String ,String> nonDefaultValues){
        Map<String ,String> defaultValues = DataGenerator.getRegistrationData();

        Map<String ,String> userData = new HashMap<>();
        String[] keys = {"email", "password", "username", "firstName", "lastName"};
        for (String key : keys){
            if (nonDefaultValues.containsKey(key)){
                userData.put(key, nonDefaultValues.get(key));
            }
            else {
                userData.put(key, defaultValues.get(key));
            }
        }
        return userData;
    }

    public static String getRandomText(Integer max){
            int leftLimit = 97; // letter 'a'
            int rightLimit = 122; // letter 'z'
            int targetStringLength = max;
            Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                    .limit(targetStringLength)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
    }

}
