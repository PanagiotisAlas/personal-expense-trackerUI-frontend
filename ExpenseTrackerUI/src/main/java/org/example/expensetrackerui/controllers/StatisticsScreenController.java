package org.example.expensetrackerui.controllers;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.expensetrackerui.exceptions.AuthenticationException;
import org.example.expensetrackerui.models.Expense;
import org.example.expensetrackerui.utils.ExpenseDataParser;
import org.example.expensetrackerui.utils.HttpClientUtil;
import org.example.expensetrackerui.utils.JwtStorageUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsScreenController {
    @FXML
    private PieChart expensePieChart;

    @FXML
    private MFXComboBox<String> monthPicker;

    @FXML
    private MFXComboBox<Integer> yearPicker;

    @FXML
    private MFXButton backButton;

    private List<String> categories;

    List<String> months = Arrays.asList("January", "February", "March", "April", "May",
            "June", "July", "August", "September", "October", "November", "December");

    @FXML
    public void initialize(){
        monthPicker.getItems().addAll(months);

        Platform.runLater(() -> {
            int currentMonth = LocalDate.now().getMonthValue();
            monthPicker.setValue(monthPicker.getItems().get(currentMonth -1));


        int currentYear = LocalDate.now().getYear();
        //fetchCategories();
        for(int year = 2020; year <= currentYear; year ++ ){
            yearPicker.getItems().add(year);
        }
        yearPicker.setValue(currentYear);
            fetchCategories();


        });
        monthPicker.valueProperty().addListener((observable, oldValue, newValue ) -> {
            fetchExpensesByMonthYear();
        });

        yearPicker.valueProperty().addListener((observable, oldValue, newValue ) -> {
            fetchExpensesByMonthYear();
        });

        backButton.setOnAction(event -> handleBackButton());
    }

    private void fetchCategories(){
        String token = JwtStorageUtil.getToken();

        try{
            String response = HttpClientUtil.sendGetRequestWithToken(
                    "/expenses/categories", token);
            categories = ExpenseDataParser.parseCategoryList(response);

            fetchExpensesByMonthYear();

        }catch (AuthenticationException e) {
            handleAuthenticationFailure();
        }catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void fetchExpensesByMonthYear() {
        if (categories == null || categories.isEmpty()) {
            return;
        }
        String token = JwtStorageUtil.getToken();
        int selectedYear = yearPicker.getValue();
        int selectedMonth = months.indexOf(monthPicker.getValue()) +1;

        expensePieChart.getData().clear();

        Map<String, Double> categoryExpenses = new HashMap<>();

        try{
            for(String category : categories) {
                String path = "/expenses/category/" + category.toLowerCase()
                        + "/month?month=" + selectedYear + "-" +
                        String.format("%02d", selectedMonth);
                String response =
                        HttpClientUtil.sendGetRequestWithToken(path,token);
                List<Expense> expenseList =
                        ExpenseDataParser.parseExpenseList(response);
                List<Double> expenses =
                        expenseList.stream().map(expense -> expense
                                .getExpenseType() == 0? expense.getAmount() : 0).toList();

                double totalExpense = expenses.stream().filter(amount -> amount > 0)
                        .mapToDouble(Double::doubleValue).sum();
                categoryExpenses.put(category, totalExpense);
            }
        }catch (AuthenticationException e) {
            handleAuthenticationFailure();
            return;
        }catch (Exception e){
            showExpenseNotFoundError();
        }

        for(Map.Entry<String, Double> entry : categoryExpenses.entrySet()) {
            if (entry.getValue() > 0) {
                expensePieChart.getData().add(new PieChart.Data(
                        entry.getKey(), entry.getValue()));
            }
        }
    }
    private void showExpenseNotFoundError(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("No expense found");
        alert.setHeaderText("No expense");
        alert.setContentText("No expenses found for the selected date. Try another date");

        alert.showAndWait();
    }

    private void handleAuthenticationFailure() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Session Expired");
        alert.setHeaderText(null);
        alert.setContentText("Your session has expired. PLease log in again");
    }

    private void loadPieChartData(){

        expensePieChart.getData().clear();

        PieChart.Data foodExpense = new PieChart.Data("Food", 300);
        PieChart.Data transportExpense = new PieChart.Data("Transport", 150);
        PieChart.Data healthExpense = new PieChart.Data("Health", 200);
        PieChart.Data shoppingExpense = new PieChart.Data("Shopping", 250);

        expensePieChart.getData().addAll(foodExpense, transportExpense, healthExpense, shoppingExpense);
    }

    private void handleBackButton(){
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.close();
    }
}
