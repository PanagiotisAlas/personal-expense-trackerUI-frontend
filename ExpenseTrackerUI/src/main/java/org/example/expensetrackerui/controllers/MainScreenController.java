package org.example.expensetrackerui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.expensetrackerui.exceptions.AuthenticationException;
import org.example.expensetrackerui.models.Expense;
import org.example.expensetrackerui.models.ExpenseData;
import javafx.util.Callback;
import org.example.expensetrackerui.utils.ExpenseDataParser;
import org.example.expensetrackerui.utils.HttpClientUtil;
import org.example.expensetrackerui.utils.JwtStorageUtil;

//import javax.security.auth.callback.Callback;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

//import static org.graalvm.compiler.lir.gen.LIRGeneratorTool.ArrayIndexOfVariant.Table;

public class MainScreenController {
    @FXML
    private TableView<Expense> expenseTable;

    @FXML
    private TableColumn<Expense, String> categoryColumn;

    @FXML
    private TableColumn<Expense, String> descriptionColumn;

    @FXML
    private TableColumn<Expense, Double> amountColumn;

    @FXML
    private TableColumn<Expense, LocalDate> dateColumn;

    @FXML
    private TableColumn<Expense, Void> editColumn;

    @FXML
    private TableColumn<Expense, Void> deleteColumn;

    private ExpenseData expenseData;
    @FXML
    private MFXButton logoutButton;
    @FXML
    private MFXButton viewMonthlyStatsButton;
    @FXML
    private MFXButton addExpenseButton;
    @FXML
    private DatePicker datePicker;

    @FXML
    public void initialize(){
        LocalDate currentDate = LocalDate.now();
        datePicker.setValue(currentDate);

        expenseTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        /*datePicker.setValue(LocalDate.now());

        expenseData = new ExpenseData();
        */
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("note"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        addEditButtonToTable();
        addDeleteButtonToTable();

        expenseTable.getColumns().add(categoryColumn);
        expenseTable.getColumns().add(descriptionColumn);
        expenseTable.getColumns().add(amountColumn);
        expenseTable.getColumns().add(dateColumn);
        expenseTable.getColumns().add(editColumn);
        expenseTable.getColumns().add(deleteColumn);

        fetchExpensesByDate(currentDate.toString());
        datePicker.valueProperty().addListener((observable,oldValue,newValue)-> {
            if(newValue != null){
                fetchExpensesByDate(newValue.toString());
            }
        });
        /*addDummyExpenses();*/

        /*expenseTable.setItems(expenseData.getExpenenses());*/
    }
    private void fetchExpensesByDate(String date){
        String formattedDate = date;
        String token = JwtStorageUtil.getToken();
        if(token == null || token.isEmpty()){
            System.out.println("No token found. User is not authenticated.");
            return;
        }
        String path = "/expenses/day/" + formattedDate;
        try {
            String response = HttpClientUtil.sendGetRequestWithToken(path, token);
            List<Expense> expenses =
                    ExpenseDataParser.parseExpenseList(response);
            expenseTable.getItems().clear();
            expenseTable.getItems().addAll(expenses);
        }catch (AuthenticationException e){
            handleAuthenticationFailure();
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
    private void handleAuthenticationFailure(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Session Expired");
        alert.setHeaderText(null);
        alert.setContentText("Your session has expired. Please log in again");

        alert.setOnHidden(evt -> redirectToLogin());

        alert.showAndWait();
    }
    private void redirectToLogin(){
        try {
            JwtStorageUtil.clearToken();

            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/org/example/expensetrackerui/views/LoginScreen.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene loginScene = new Scene(loader.load());

            loginScene.getStylesheets().add(getClass().getResource(
                    "/org/example/expensetrackerui/css/style.css")
                    .toExternalForm());
            stage.setScene(loginScene);
            stage.show();
        }catch (IOException e) {
            System.out.println("redirectToLogin");
            e.printStackTrace();
        }
    }

    private void addDummyExpenses(){
        expenseData.addExpense(new Expense(100,0,LocalDate.of(
                2025,10,2),50.0,"Food","Credit Card", "Dinner at restaurant"));
        expenseData.addExpense(new Expense(100,0,LocalDate.of(
                2025,10,2),150.0,"Transport","Cash", "Bus fare"));
        expenseData.addExpense(new Expense(102,0,LocalDate.of(
                2025,10,2),250.0,"Shopping","Debit Card", "Clothes purchase"));
    }

    // Adding Edit button in each row
    private void addEditButtonToTable() {
        Callback<TableColumn<Expense, Void>, TableCell<Expense, Void>> cellFactory
                = param -> new TableCell<>() {
            private final MFXButton btn = new MFXButton("Edit");

            {
                btn.setOnAction(event -> {
                    Expense expense = getTableView().getItems().get(getIndex());
                    openExpenseScreenInEditMode(expense);
                    System.out.println("Editing: " + expense.getNote());
                    // Add your edit logic here
                });
                btn.getStyleClass().add("outlined-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        };
        editColumn.setCellFactory(cellFactory);
    }
    private void openExpenseScreenInEditMode(Expense expense) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/org/example/expensetrackerui/views/ExpenseScreen.fxml"));
            VBox expensePane = loader.load();
            ExpenseScreenController expenseScreenController = loader.getController();
            expenseScreenController.setMainScreenController(this);
            expenseScreenController.initEditMode(
                    expense.getId(),
                    expense.getExpenseType(),
                    expense.getDate(),
                    expense.getAmount(),
                    expense.getCategory(),
                    expense.getAccount(),
                    expense.getNote());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(expensePane);

            scene.getStylesheets().add(getClass().getResource(
                    "/org/example/expensetrackerui/css/expense_screen.css"
            ).toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Edit expense");
            stage.setWidth(600);
            stage.setResizable(false);
            stage.showAndWait();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void addDeleteButtonToTable() {
        Callback<TableColumn<Expense, Void>, TableCell<Expense, Void>> cellFactory
                = param -> new TableCell<>() {
            private final MFXButton btn = new MFXButton("Delete");

            {
                btn.setOnAction(event -> {
                    Expense expense = getTableView().getItems().get(getIndex());
                    System.out.println("Deleting: " + expense.getNote());
                    showDeleteConfirmation(expense);
                    // Add your delete logic here
                    getTableView().getItems().remove(expense);
                });
                btn.getStyleClass().add("outlined-button");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        };
        deleteColumn.setCellFactory(cellFactory);
    }

    private void showDeleteConfirmation(Expense expense){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this expense?");
        alert.setContentText("Expense: " + expense.getNote());

        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        alert.showAndWait().ifPresent(response -> {
            if(response == ButtonType.OK){
                deleteExpense(expense);
                System.out.println("Deleted expense: "  + expense.getNote());
            }else {
                System.out.println("Delete canceled.");
            }
        });
    }
    private void deleteExpense(Expense expense){
        String token = JwtStorageUtil.getToken();
        if (token == null || token.isEmpty()){
            System.out.println("No token found. User is not authenticated");
            return;
        }
        String path = "/expenses/" + expense.getId();
        try {
            HttpClientUtil.sendDeleteRequestWithToken(path, token);
            System.out.println("Deleted expense: " + expense.getNote());

            refreshExpenses();
        }catch (AuthenticationException e) {
            handleAuthenticationFailure();
        }catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }





    @FXML
    private void handleLogout(){
        JwtStorageUtil.clearToken();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/org/example/expensetrackerui/views/LoginScreen.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene loginScene = new Scene(loader.load());
            loginScene.getStylesheets().add(getClass().getResource(
                    "/org/example/expensetrackerui/css/style.css")
                    .toExternalForm());
            stage.setScene(loginScene);
            stage.show();
        }catch (IOException e) {
            System.out.println("handleLogout");
            e.printStackTrace();
        }

    }
    @FXML
    private void handleAddExpense(){
        try{
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/org/example/expensetrackerui/views/ExpenseScreen.fxml"));
            VBox expensePane = loader.load();


            ExpenseScreenController expenseScreenController = loader.getController();
            expenseScreenController.setMainScreenController(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(expensePane);

            scene.getStylesheets().add(getClass().getResource(
                    "/org/example/expensetrackerui/css/expense_screen.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Add Expense");

            stage.setWidth(600);
            stage.setResizable(false);
            stage.showAndWait();
        }catch (IOException e){
            System.out.println("handleAddExpense");
            e.printStackTrace();
        }

    }
    @FXML
    private void handleViewMonthlyStats(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/org/example/expensetrackerui/views/StatisticsScreen.fxml"));
            VBox statisticsPane = loader.load();

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(statisticsPane);

            scene.getStylesheets().add(getClass().getResource(
                    "/org/example/expensetrackerui/css/statistics_screen.css").toExternalForm());

            stage.setTitle("Monthly Statistics");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        }catch (IOException e){
            e.printStackTrace();
        }

    }
    public void refreshExpenses(){
        LocalDate selectedDate = datePicker.getValue();

        if(selectedDate != null) {
            fetchExpensesByDate(selectedDate.toString());
        }
    }
}
