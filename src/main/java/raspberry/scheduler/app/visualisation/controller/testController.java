package raspberry.scheduler.app.visualisation.controller;

import eu.hansolo.tilesfx.Tile;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.rgb;

public class testController implements Initializable {
    @FXML
    private Tile _memTile;

    private void setupMemTile() {

        _memTile.setMaxValue(((double) Runtime.getRuntime().maxMemory() / (double) (1024 * 1024)));
        _memTile.setBarColor(rgb(255, 136, 0));
        _memTile.setThresholdColor(rgb(255, 136, 0));
        _memTile.setTickLabelDecimals(0);
        _memTile.setNeedleColor(rgb(0, 0, 0));
        _memTile.setTitle("Memory usage");

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupMemTile();
    }
}
