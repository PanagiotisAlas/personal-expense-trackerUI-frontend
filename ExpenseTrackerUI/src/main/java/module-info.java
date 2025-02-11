module org.example.expensetrackerui {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.net.http;
    requires static lombok;
    requires MaterialFX;
    requires java.prefs;
    requires jdk.internal.vm.compiler;
    requires javafx.base;


    opens org.example.expensetrackerui to javafx.fxml;
    opens org.example.expensetrackerui.controllers to javafx.fxml;
    opens org.example.expensetrackerui.models to com.google.gson, javafx.base;
    exports org.example.expensetrackerui;
}