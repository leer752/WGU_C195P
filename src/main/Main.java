package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * <h1>Main</h1>
 * This is the Main class -- basically, where everything starts! When the program is ran, it starts from here and branches
 * into each view as they are called. When everything is done, it returns to Main and closes the program.
 * <p>
 * This program is a GUI-based scheduling desktop application. Additional details can be found in README.txt.
 * <p>
 * Lambda justifications are found in main.util.Common (setCharLimit) and main.controllers.SchedulerController
 * (setAppointmentTableColumns).
 *
 * @author Lee Rhodes
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("resources/fxml/Login.fxml"));
        primaryStage.setTitle("Appointment Scheduler");
        primaryStage.setMinWidth(595);
        primaryStage.setMinHeight(300);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
