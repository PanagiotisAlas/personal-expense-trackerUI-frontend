package org.example.expensetrackerui.utils;

import com.google.gson.Gson;
import org.example.expensetrackerui.exceptions.AuthenticationException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpClientUtil {
    private static final HttpClient client = HttpClient.newBuilder().build();
    private static final Gson gson = new Gson();
    private static final String baseUrl = "http://localhost:8080";

    public static void sendPutRequestWithToken(String path, String token, String jsonBody)
        throws IOException, InterruptedException, AuthenticationException{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers
                .ofString());

        if(response.statusCode() == 403){
            throw new AuthenticationException("Session has expired. Please log in again.");
        }

        if(response.statusCode() == 200){
            System.out.println("Expense updated successfully.");
        }else {
            throw new IOException("Failed to update data: " + response.statusCode());
        }
    }

    public static void sendPostRequestWithToken(String path, String token, String jsonBody)
        throws IOException,
            InterruptedException,
            AuthenticationException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 403){
            throw new AuthenticationException("Session has expired. Please log in again.");
        }
        if(response.statusCode() != 200){
            response.body();
        }else {
            throw new IOException("Failed to fetch data: " + response.statusCode());
        }
    }

    public static HttpResponse<String> sendPostRequest(
            String url, String jsonBody) throws IOException, InterruptedException{
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        return client.send(httpRequest, HttpResponse
                .BodyHandlers.ofString());
    }
    public static String sendGetRequestWithToken(
            String path, String token)
            throws IOException, InterruptedException, AuthenticationException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() == 403){
            throw new AuthenticationException("Session has expired. Please log in again.");
        }

        if(response.statusCode() == 200) {
            return response.body();
        }else {
            throw new IOException("Failed to fetch data: " + response.statusCode());
        }
    }

    public static void sendDeleteRequestWithToken(String path, String token)
        throws IOException, InterruptedException, AuthenticationException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", "Bearer " + token)
                .DELETE()
                .build();

        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 403) {
            throw new AuthenticationException("Session has expired. Please log in again");
        }
        if (response.statusCode() != 204) {
            throw new IOException("Failed to delete data: " + response.statusCode());
        }
    }

    public static HttpResponse<String> sendGetRequest(
            String url, String token){
        return null;
    }
}
