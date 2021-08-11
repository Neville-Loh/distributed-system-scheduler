package raspberry.scheduler.app.visualisation;

import eu.hansolo.tilesfx.Tile;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
//import eu.hansolo.tilesfx.Tile;
import javafx.util.Duration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Updater {
    private Label _timeElapsed, _iterations, _status;
    private Tile _memTile;
    private Timeline _timer,_polling;
    private boolean _isRunning = true;
    private double _currentTime;
    private double _startTime;
    private DateFormat _timeFormat = new SimpleDateFormat("mm:ss:SSS");

    public Updater(Label timeElapsed, Label iterations, Label status, Tile memTile) {
        _timeElapsed = timeElapsed;
        _iterations = iterations;
        _status = status;
        _memTile = memTile;

        startTimer();
        startPolling();
    }


    private void startTimer() {
        _startTime = System.currentTimeMillis();
        _timer = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            updateTimer();
        }));
        _timer.setCycleCount(_timer.INDEFINITE);
        _timer.play();
    }

    private void updateTimer() {
        if (_isRunning) {
            _currentTime = System.currentTimeMillis();
            _timeElapsed.setText(_timeFormat.format(_currentTime - _startTime));
        }
    }

    public void stopTimer() {
        _isRunning = false;
        _timer.stop();
    }

    /**
     * May change to timer class later - not sure if timeline is going to have massive impact on performance vs using a background thread
     */


    private void startPolling(){
        _polling = new Timeline(new KeyFrame(Duration.millis(500), event -> {
            updateMemTile();
        }));
        _polling.setCycleCount(_timer.INDEFINITE);
        _polling.play();
    };

    private void updateMemTile() {

        double totalMem = Runtime.getRuntime().totalMemory();
        double freeMem = Runtime.getRuntime().freeMemory();
        _memTile.setValue((totalMem - freeMem)/(1024 * 1024));
    }


}
