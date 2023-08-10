package c195.sw2;

import helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;

import static helper.JDBC.*;

public class Main extends Application {
    /**
     * The entry point of the JavaFX application.
     * This method is automatically called when the application is launched.
     *
     * @param stage The primary stage for the application.
     * @throws IOException If an I/O error occurs while loading the FXML file.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("LogIn.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Schedule Application");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main method of the JavaFX application.
     * It launches the JavaFX application and sets the default time zone to UTC.
     */
    public static void main(String[] args) {
        launch();
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

    }
    static {
        openConnection();


    }



}