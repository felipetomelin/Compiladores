package com.example.teste;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        scene.getStylesheets().add(getClass().getResource("Sytle.css").toExternalForm());

        HelloController controller = fxmlLoader.getController();
        scene.addEventFilter(KeyEvent.KEY_RELEASED, controller::handleKeyEvents);

        stage.setTitle("FURB - Compiladores");
        stage.setMinWidth(910.00);
        stage.setMinHeight(600.00);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}