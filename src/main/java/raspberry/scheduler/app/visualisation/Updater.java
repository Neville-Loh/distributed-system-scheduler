package raspberry.scheduler.app.visualisation;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
//import eu.hansolo.tilesfx.Tile;
import javafx.util.Duration;

public class Updater {
    private Label _timeElapsed, _iterations, _status;
    //private Tile _memTile;
    private Timeline _timer;
    private boolean _isRunning;
    private double _currentTime;
    private double _startTime;

    public Updater(Label timeElapsed, Label iterations, Label status) {
        _timeElapsed = timeElapsed;
        _iterations = iterations;
        _status = status;
       // _memTile = memTile;
//, Tile memTile
        startTimer();
    }


    private void startTimer() {
        _startTime = System.currentTimeMillis();
        _timer = new Timeline(new KeyFrame(Duration.millis(500), event -> {updateTimer();}));
        _timer.setCycleCount( _timer.INDEFINITE );
        _timer.play();
    }

    private void updateTimer() {
        if(_isRunning) {
            _currentTime = System.currentTimeMillis();
            _timeElapsed.setText(String.valueOf(_currentTime - _startTime));
        }
    }

    public void stopTimer(){
        _isRunning = false;
        _timer.stop();
    }

    private void updateMemTile(){
        double totalMem = Runtime.getRuntime().totalMemory();
        double freeMem = Runtime.getRuntime().freeMemory();
        //_memTile.setValue(totalMem - freeMem);
    }


}
