package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class StripeAPIKeyProvider  {
    static String apiKey = "";
    static String API_KEY_FILE = System.getProperty("user.home") + File.separator + ".stripe_api_key";

    public static String getApiKey() throws Exception {
        if (apiKey.equals("") == false) {
            return apiKey;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(API_KEY_FILE));
            apiKey = reader.readLine();

            return apiKey;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
