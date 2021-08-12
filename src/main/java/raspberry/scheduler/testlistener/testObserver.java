package raspberry.scheduler.testlistener;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

public class testObserver implements Observer {
    private String _name;
    private Label label;

    public testObserver(String name){
        super();
        _name = name;
    }

    public void setLabel(Label label){
        this.label = label;
    }

    public void setLabelText(String value){
        this.label.setText(value);
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println(_name + " "+ arg.toString());
              //  labelC.setLabel(arg.toString());
        Platform.runLater(() -> {
            try {
                setLabelText(arg.toString());
                System.out.println("button is clicked");
            } catch(Exception e) {
                e.printStackTrace();
            }
        });


    }
}
