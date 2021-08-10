package raspberry.scheduler.app.visualisation.model.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
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
    private TextFlow inputFile;

    @FXML
    private Text timeElapsed;

    @FXML
    private TextFlow outputFile;

    @FXML
    private Text numProcessors;

    private CLIConfig _config;
    private int _numProcessors;
    private String _inputFileName;
    private String _outputFileName;
    private Timeline _timer;
    private GanttChart _ganttChart;


    private double _currentTime;
    private double _startTime;
    private int _numSchedules;


    private void setInputFileName(){
        inputFile.setText(_inputFileName);
    }

    private void setNumProcessors(){
        numProcessors.setText(numProcessors.toString());
    }

    private void setOutputFile(){
        outputFile.setText(_outputFileName);
    }
    public void setCLIconfig(CLIConfig config){
        _config = config;
    }

    public void startTimer(){
        _starTime = System.currentTimeMillis();
      /*  _timer= new Timeline(new KeyFrame(Duration.millis(0),));

        _timer.play();*/

    }

    private void updateTimer(){
        _currentTime = System.currentTimeMillis();


        timeElapsed.setText((_currentTime - _startTime).toString());
    }

    private void stopTimer() {
        _timer.stop();
    }




}
