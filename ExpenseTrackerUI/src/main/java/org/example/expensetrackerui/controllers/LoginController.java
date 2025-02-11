package org.example.expensetrackerui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.expensetrackerui.models.AuthRequestDTO;
import org.example.expensetrackerui.services.AuthService;

import java.io.IOException;

public class LoginController {
    public MFXTextField usernameField;
    public MFXPasswordField passwordField;
    public MFXButton submitButton;

    public void handleLogin(ActionEvent actionEvent) {
        AuthRequestDTO requestDTO = new AuthRequestDTO();

        requestDTO.setUsername(usernameField.getText());
        requestDTO.setPassword(passwordField.getText());

        Stage stage = (Stage) usernameField.getScene().getWindow();
        AuthService.login(requestDTO,stage);
    }


    @FXML
    private void handleCreateAccount(MouseEvent event) {
        try {
            // Load the signup screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/expensetrackerui/views/SignupScreen.fxml"));
            Scene signupScene = new Scene(loader.load());

            // Set the scene on the current stage
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            signupScene.getStylesheets().add(getClass().getResource("/org/example/expensetrackerui/css/style.css").toExternalForm());
            stage.setScene(signupScene);
        } catch (IOException e) {
            System.out.println("handleCreateAccount");
            e.printStackTrace();
            // Log the error or show an error message to the user
        }
    }
}
