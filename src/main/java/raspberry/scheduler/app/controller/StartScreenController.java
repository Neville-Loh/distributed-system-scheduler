package raspberry.scheduler.app.controller;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
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
    private Text fileNameText;

    @FXML
    private void testButton(){
        System.out.println("i got pressed!");
    }

    @FXML
    private void selectFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file");
        Stage popup = new Stage();
        File selectedFile = fileChooser.showOpenDialog(popup);

        if(selectedFile != null) {

            String fileName = selectedFile.getName();
//        int userSelection = fileChooser.showSaveDialog(popup);
//        String test = fileChooser.titleProperty();
            System.out.println(fileName);

            fileNameText.setText(fileName);
        }else{
            System.out.println("cancelled");
        }

    }

    @FXML
    private void showMainScreen(ActionEvent event){
        ScreenController.goMainScreen(getClass(), event);

    }
}
