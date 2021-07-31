package main.java.raspberry.scheduler.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class StartScreenController{
    @FXML
    private Text title;

    @FXML
    private Button startButton;

    @FXML
    private Button selectButton;

    @FXML
    private void testButton(){
        System.out.println("i got pressed!");
    }

    @FXML
    private void selectFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file");
        fileChooser.showOpenDialog(new Stage());

    }
}
