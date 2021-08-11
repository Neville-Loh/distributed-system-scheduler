package raspberry.scheduler.app.visualisation.controller;

//import eu.hansolo.tilesfx.Tile;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import raspberry.scheduler.app.App;
import raspberry.scheduler.app.visualisation.Updater;
import raspberry.scheduler.app.visualisation.model.GanttChart;
import raspberry.scheduler.cli.CLIConfig;


import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
/**
 * Not sure if we want to have a sepearte timer/update/polling class or just do it all in one class so i'll just start and refactor later
 */


    @FXML
    private Label _inputFile, _outputFile, _numProcessors, _numCores, _timeElapsed, _iterations,_status;
    //@FXML
    //private Tile _memTile;
    private CLIConfig _config;
    private String _inputFileName;
    private String _outputFileName;
    private GanttChart _ganttChart;
    private int _numSchedules;
    private Updater _updater;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        _config = App.GetCLIConfig();
        setIdleStats();
        _updater = new Updater(_timeElapsed, _iterations,_status);
//, _memTile
    }

    private void setIdleStats(){
        _inputFile.setText(_config.getDotFile());
        _outputFile.setText(_config.getOutputFile());
        _numProcessors.setText(String.valueOf(_config.get_numProcessors()));
        _numCores.setText(String.valueOf(_config.getNumCores()));

    }

    private void setupMemTile(){
        //_memTile.setMaxValue(((double)Runtime.getRuntime().maxMemory()/(double)(1024 * 1024)));
    }





}
