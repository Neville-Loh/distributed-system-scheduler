package raspberry.scheduler.testlistener;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class LabelController implements Initializable{
    @FXML
    private Label label;



    public void setLabel(String value) {
        label.setText(value);
    }

    public void setLabel1() {
        label.setText("hi");

        System.out.println("hi");
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        testObserver obs = TESTMAIN.getObs();
        obs.setLabel(label);
    }
}
