package main.java.raspberry.scheduler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application{

    //private Stage primaryStage;
    Parent root;

    public static void main(String[] args) {
        // write your code here
        System.out.println("Hello world");

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //this.primaryStage = primaryStage;
       // this.primaryStage.setTitle("placeholder");
       // FXMLLoader loader = new FXMLLoader();
       // loader.setLocation(this.getClass().getResource("/main.java.raspberry.scheduler/view/StartScreenView.fxml"));
        root = FXMLLoader.load(getClass().getResource("/main.java.raspberry.scheduler/view/StartScreenView.fxml"));
       //  Parent layout = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();


    }
}
