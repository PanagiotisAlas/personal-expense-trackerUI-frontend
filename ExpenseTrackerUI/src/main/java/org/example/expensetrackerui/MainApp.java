package org.example.expensetrackerui;

import io.github.palexdev.materialfx.theming.JavaFXThemes;
import io.github.palexdev.materialfx.theming.MaterialFXStylesheets;
import io.github.palexdev.materialfx.theming.UserAgentBuilder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.expensetrackerui.utils.JwtStorageUtil;
import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        UserAgentBuilder.builder()
                        .themes(JavaFXThemes.MODENA)
                        .themes(MaterialFXStylesheets
                        .forAssemble(true))
                        .setDeploy(true)
                        .setResolveAssets(true)
                        .build()
                        .setGlobal();


        stage.setTitle("Expense Tracker");
        stage.initStyle(StageStyle.DECORATED);
        stage.setWidth(900);
        stage.setHeight(600);
        stage.setResizable(false);

        String token = JwtStorageUtil.getToken();
       // loadMainScreen(stage);
        //loadLoginScreen(stage);
        if(token != null){
           loadMainScreen(stage);
            //loadLoadingScreen(stage);
        }else {
            loadLoginScreen(stage);


        }


    }

    private void loadMainScreen(Stage stage) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/org/example/expensetrackerui/views/MainScreen.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("/org/example/expensetrackerui/css/main_screen.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void loadLoadingScreen(Stage stage) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource(
                        "/resources/org/example/expensetrackerui/views/LoadingScreen.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets()
                .add(getClass()
                .getResource(
            "/org/example/expensetrackerui/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void loadLoginScreen(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/org/example/expensetrackerui/views/LoginScreen.fxml"));
        Scene scene = new Scene(loader.load());
        // Load the CSS file
        scene.getStylesheets().add(getClass().getResource("/org/example/expensetrackerui/css/style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        launch(args);
    }
}
