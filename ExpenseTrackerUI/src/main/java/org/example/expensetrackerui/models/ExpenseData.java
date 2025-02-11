package org.example.expensetrackerui.models;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class ExpenseData {
    private ObservableList<Expense> expenseList;

    public ExpenseData(){
        expenseList = FXCollections.observableArrayList();
    }

    public ObservableList<Expense> getExpenenses(){
        return expenseList;
    }

    public void addExpense(Expense expense) {
        expenseList.add(expense);
    }
}
