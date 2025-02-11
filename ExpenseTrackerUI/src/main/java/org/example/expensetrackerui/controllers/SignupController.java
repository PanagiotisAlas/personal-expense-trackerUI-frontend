package org.example.expensetrackerui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.example.expensetrackerui.models.AuthRequestDTO;
import org.example.expensetrackerui.services.AuthService;

import java.io.IOException;

public class SignupController {
    public MFXTextField fullNameField;
    public MFXTextField usernameField;
    public MFXPasswordField passwordField;
    public MFXButton signupButton;

    public void handleSignup(ActionEvent actionEvent) {
        AuthRequestDTO requestDTO = new AuthRequestDTO();
        requestDTO.setFullName(fullNameField.getText());
        requestDTO.setUsername(usernameField.getText());
        requestDTO.setPassword(passwordField.getText());

        Stage stage = (Stage) fullNameField.getScene().getWindow();
        AuthService.signup(requestDTO,stage);
    }

    public void handleLogin(MouseEvent mouseEvent) {
        try {
            // Load the signup screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/expensetrackerui/views/LoginScreen.fxml"));
            Scene loginScene = new Scene(loader.load());

            // Set the scene on the current stage
            Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
            loginScene.getStylesheets().add(getClass().getResource("/org/example/expensetrackerui/css/style.css").toExternalForm());
            stage.setScene(loginScene);
        } catch (IOException e) {
            System.out.println("handleLogin");
            e.printStackTrace();
            // Log the error or show an error message to the user
        }
    }
}
