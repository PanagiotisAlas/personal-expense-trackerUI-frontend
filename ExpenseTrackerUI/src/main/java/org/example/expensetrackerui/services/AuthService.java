package org.example.expensetrackerui.services;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.expensetrackerui.models.AuthRequestDTO;
import org.example.expensetrackerui.models.AuthResponse;
import org.example.expensetrackerui.utils.HttpClientUtil;
import org.example.expensetrackerui.utils.JwtStorageUtil;

import java.io.IOException;
import java.net.http.HttpResponse;

public class AuthService {
    private static final String BASE_URL = "http://localhost:8080";
    private static final Gson gson = new Gson();

    public static void signup(AuthRequestDTO requestDTO, Stage stage){
        new Thread(() -> {
            try{
                String url = BASE_URL + "/signup";
                String jsonBody = gson.toJson(requestDTO);
                HttpResponse<String> response
                        = HttpClientUtil.sendPostRequest(url,jsonBody);
                AuthResponse authResponse = gson
                        .fromJson(response.body(), AuthResponse.class);
                if("success".equalsIgnoreCase(authResponse.getMessage())){
                    JwtStorageUtil.saveToken(authResponse.getToken());
                    Platform.runLater(()-> navigateToLoadingScreen(stage));
                }else {
                    System.out.println(authResponse.getMessage());
                }
            }catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
    }

    public static void login(AuthRequestDTO requestDTO, Stage stage){
        new Thread(() -> {
            try{
                String url = BASE_URL + "/login";
                String jsonBody = gson.toJson(requestDTO);
                HttpResponse<String> response
                        = HttpClientUtil.sendPostRequest(url,jsonBody);
                AuthResponse authResponse = gson
                        .fromJson(response.body(), AuthResponse.class);
                if("success".equalsIgnoreCase(authResponse.getMessage())){
                    JwtStorageUtil.saveToken(authResponse.getToken());
                    Platform.runLater(()-> navigateToMainScreen(stage));
                }else {
                    System.out.println(authResponse.getMessage());
                }
            }catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }).start();
    }

    private static void navigateToLoadingScreen(Stage stage){
        try {
            // Load the signup screen
            FXMLLoader loader = new FXMLLoader(AuthService.class
                    .getResource("/org/example/expensetrackerui/views/LoadingScreen.fxml"));
            Scene loadingScene = new Scene(loader.load());

            // Set the scene on the current stage
            loadingScene.getStylesheets()
                    .add(AuthService.class.getResource(
                "/org/example/expensetrackerui/css/style.css")
                    .toExternalForm());
            stage.setScene(loadingScene);
        } catch (IOException e) {
            System.out.println("navigateToLoadingScreen");
            e.printStackTrace();
            // Log the error or show an error message to the user
        }
    }

    private static void navigateToMainScreen(Stage stage) {
        try{
            FXMLLoader loader = new FXMLLoader(AuthService.class.getResource("/org/example/expensetrackerui/views/MainScreen.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(AuthService.class.getResource("/org/example/expensetrackerui/css/main_screen.css").toExternalForm());
            stage.setScene(scene);
        }catch (IOException e) {
            System.out.println("navigateToMainScreen");
            e.printStackTrace();
            //showAlert(Alert.AlertType.ERROR, "Loading Error", "Could not load the MainScreen.");
        }
    }
}
