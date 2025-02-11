package org.example.expensetrackerui.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.example.expensetrackerui.models.Expense;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;

public class ExpenseDataParser {

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new
                    LocalDateTypeAdapter())
            .create();

    public static List<Expense> parseExpenseList(String jsonResponse){
        Type expenseListType = new TypeToken<List<Expense>>(){}.getType();
        return gson.fromJson(jsonResponse, expenseListType);
    }
    public static String serializeExpense(Object expense){
        return gson.toJson(expense);
    }

    public static List<String> parseCategoryList(String jsonResponse) {
        Type categoryListType = new TypeToken<List<String>>(){}.getType();
        return gson.fromJson(jsonResponse, categoryListType);
    }
}
