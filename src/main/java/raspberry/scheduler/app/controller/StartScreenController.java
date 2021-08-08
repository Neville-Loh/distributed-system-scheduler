package raspberry.scheduler.app.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 *
 */
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
