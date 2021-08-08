package raspberry.scheduler.app.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controller class for handling changes of screen
 * @author Alan
 */
public class ScreenController {
    /**
     * Switch to the main screen
     * @param controllerClass class that has called this method
     * @param event event get the window that has called goMainScreen
     */
    public static void goMainScreen(Class<?> controllerClass, ActionEvent event){
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        try{
            Parent parent = FXMLLoader.load(controllerClass.getResource("/view/MainView.fxml"));
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Switch to the title screen
     * @param controllerClass class that has called this method
     * @param event event get the window that has called goMainScreen
     */
    public static void goTitleScreen(Class<?> controllerClass, ActionEvent event){
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        try{
        Parent parent = FXMLLoader.load(controllerClass.getResource("/view/StartScreenController.fxml"));
        stage.setScene(new Scene(parent));
        stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
