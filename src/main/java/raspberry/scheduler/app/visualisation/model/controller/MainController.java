package raspberry.scheduler.app.visualisation.model.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import raspberry.scheduler.app.visualisation.model.GanttChart;
import raspberry.scheduler.cli.CLIConfig;

public class MainController {
/**
 * Not sure if we want to have a sepearte timer/update/polling class or just do it all in one class so i'll just start and refactor later
 */


    @FXML
    private Label _inputFile, _outputFile, _numProcessors, _numCores, _timeElapsed, _iterations;

    private CLIConfig _config;
    private String _inputFileName;
    private String _outputFileName;
    private Timeline _timer;
    private GanttChart _ganttChart;


    private double _currentTime;
    private double _startTime;
    private int _numSchedules;



    private void setInputFileName(){
        _inputFile.setText(_inputFileName);
    }

    private void setNumProcessors(){
        _numProcessors.setText(_numProcessors.toString());
    }

    private void setOutputFile(){
        _outputFile.setText(_outputFileName);
    }
    public void setCLIconfig(CLIConfig config){
        _config = config;
    }

    public void startTimer(){
        _startTime = System.currentTimeMillis();
      /*  _timer= new Timeline(new KeyFrame(Duration.millis(0),));

        _timer.play();*/

    }

    private void updateTimer(){
        _currentTime = System.currentTimeMillis();


        _timeElapsed.setText(String.valueOf((_currentTime - _startTime)));
    }

    private void stopTimer() {
        _timer.stop();
    }




}
