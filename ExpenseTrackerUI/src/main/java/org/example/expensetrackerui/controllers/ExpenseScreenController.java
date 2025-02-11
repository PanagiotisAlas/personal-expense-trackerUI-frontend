package org.example.expensetrackerui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import org.example.expensetrackerui.exceptions.AuthenticationException;
import org.example.expensetrackerui.models.Expense;
import org.example.expensetrackerui.utils.ExpenseDataParser;
import org.example.expensetrackerui.utils.HttpClientUtil;
import org.example.expensetrackerui.utils.JwtStorageUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;

public class ExpenseScreenController {
    // FXML fields
    @FXML
    private MFXComboBox<String> expenseTypeDropdown;

    @FXML
    private DatePicker datePicker;

    @FXML
    private MFXTextField amountField;

    @FXML
    private MFXComboBox<String> categoryDropdown;

    @FXML
    private MFXComboBox<String> accountDropdown;

    @FXML
    private MFXTextField noteField;

    @FXML
    private MFXButton submitButton;



    private MainScreenController mainScreenController;
    private Integer expenseId = null;
    private boolean isEditMode = false;

    public void setMainScreenController(MainScreenController controller){
        this.mainScreenController = controller;
    }

    @FXML
    public void initialize(){

        expenseTypeDropdown.getItems().addAll("Expense", "Income");
        categoryDropdown.getItems().addAll(Arrays.asList("Food",
                "Transport", "Travel", "Household", "Health",
                "Social life", "Gift", "Apparel", "Education", "Beauty",
                "Other"));
        accountDropdown.getItems().addAll(Arrays.asList("Bank", "Cash",
                "Card"));

        if (!isEditMode){
            datePicker.setValue(LocalDate.now());
        }
        datePicker.getEditor().setDisable(true);
        datePicker.getEditor().setOpacity(1);
    }


    public void initEditMode(int id, int expenseType, LocalDate date,
                             double amount, String category, String account, String note){
        this.expenseId = id;
        this.isEditMode = true;
        Platform.runLater(() -> {
            expenseTypeDropdown.setValue(expenseType == 0 ? "Expense" : "Income");
        });

//        expenseTypeDropdown.setValue(expenseType.equals("0") ? "Expense"
//                : "Income");
        datePicker.setValue(date);
        amountField.setText(String.valueOf(amount));
        categoryDropdown.setValue(category);
        accountDropdown.setValue(account);
        noteField.setText(note);
    }

    @FXML
    private void handleSubmit(){
        if (!validateForm()) {
            return;
        }
        int expenseType = expenseTypeDropdown.getValue().equals("Expense") ? 0 : 1;
        LocalDate date = datePicker.getValue();
        double amount = Double.parseDouble(amountField.getText());
        String category = categoryDropdown.getValue();
        String account = accountDropdown.getValue();
        String note = noteField.getText();

        String jsonBody = ExpenseDataParser.serializeExpense(new Expense(
                0, expenseType, date, amount, category, account, note));

        String token = JwtStorageUtil.getToken();
        if(isEditMode) {
            System.out.println("Editing expense: ID=" +expenseId
                    + ", Type=" + expenseType);
            String path = "/expenses/" +expenseId;

        try {
            HttpClientUtil.sendPutRequestWithToken(path, token, jsonBody);
            if(mainScreenController != null){
                mainScreenController.refreshExpenses();
            }
        }catch (AuthenticationException e){
            handleAuthenticationFailure();

        }catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }finally {
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.close();
        }
        }else {
            System.out.println("Adding new expense: Type=" + expenseType);
            String path = "/expenses";
            try {
                HttpClientUtil.sendPostRequestWithToken(path,token,jsonBody);
                if (mainScreenController != null){
                    mainScreenController.refreshExpenses();
                }
            }catch (AuthenticationException e) {
                handleAuthenticationFailure();
            }catch (IOException | InterruptedException e){
                e.printStackTrace();
            }finally {
                Stage stage = (Stage) submitButton.getScene().getWindow();
                stage.close();
            }
        }

        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();

    }


        private void handleAuthenticationFailure() {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Session Expired");
            alert.setHeaderText(null);
            alert.setContentText("Your session has expired. Please log in again.");

            alert.showAndWait();
        }

    private boolean validateForm(){
        if(expenseTypeDropdown.getValue() == null) {
            showErrorMessage("Please select an expense type.");
            return false;
        }
        LocalDate date;
        try{
            date = datePicker.getValue();
            if (date == null || date.isAfter(LocalDate.now())
                    || date.isBefore(LocalDate.now().minusYears(1))){
                showErrorMessage("Please select a valid date.");
                return false;
            }
        }catch (Exception e){
            showErrorMessage("Invalid date selected.");
            return false;
        }
        String amountText = amountField.getText();
        if(amountText == null || amountText.isEmpty()){
            showErrorMessage("Amount cannot be empty.");
            return false;
        }
        try{
            Double.parseDouble(amountText);
        }catch (NumberFormatException e){
            showErrorMessage("Amount must be a numeric value.");
            return false;
        }
        if(categoryDropdown.getValue() == null) {
            showErrorMessage("Please select a category.");
            return false;
        }
        if (accountDropdown.getValue() == null) {
            showErrorMessage("Please select an account.");
            return false;
        }
        if(noteField.getText() == null || noteField.getText().isEmpty()){
            showErrorMessage("Please enter a note.");
            return false;
        }
        return true;
    }
    private void showErrorMessage(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
