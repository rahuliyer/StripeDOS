package org.example;

import com.stripe.stripeterminal.external.callable.ConnectionTokenCallback;
import com.stripe.stripeterminal.external.callable.ConnectionTokenProvider;
import com.stripe.stripeterminal.external.models.ConnectionTokenException;

import javax.json.Json;
import javax.json.JsonReader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TokenProvider implements ConnectionTokenProvider {
    @Override
    public void fetchConnectionToken(ConnectionTokenCallback callback) {
        try {
            String apiKey = StripeAPIKeyProvider.getApiKey();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI("https://api.stripe.com/v1/terminal/connection_tokens"))
                    .POST(HttpRequest.BodyPublishers.ofString(""))
                    .header("Authorization", "Bearer " + apiKey)
                    .build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            // Print the status code and response body
            System.out.println("Status Code: " + httpResponse.statusCode());
            System.out.println("Response Body: " + httpResponse.body());

            JsonReader jr = Json.createReader(new StringReader(httpResponse.body()));
            String secret = jr.readObject().getString("secret");
            callback.onSuccess(secret);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(
                    new ConnectionTokenException("Failed to fetch connection token", e));
        }
    }
}

