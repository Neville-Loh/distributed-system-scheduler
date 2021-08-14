package raspberry.scheduler.app.visualisation.controller;

import eu.hansolo.tilesfx.Tile;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.effect.MotionBlur;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import raspberry.scheduler.algorithm.OutputSchedule;
import raspberry.scheduler.app.App;
import raspberry.scheduler.app.visualisation.Updater;
import raspberry.scheduler.app.visualisation.model.GanttChart;
import raspberry.scheduler.cli.CLIConfig;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.rgb;

public class MainController implements Initializable {
    /**
     * Not sure if we want to have a sepearte timer/update/polling class or just do it all in one class so i'll just start and refactor later
     */


    @FXML
    private Label _inputFile, _outputFile, _numProcessors, _numCores, _timeElapsed, _iterations, _status;
    @FXML
    private Tile _memTile, _CPUChart;
    @FXML
    private VBox _ganttBox;

    private CLIConfig _config;
    private String _inputFileName;
    private String _outputFileName;
    private GanttChart _ganttChart;
    private int _numSchedules;
    private Updater _updater;
        /*
         for testing purpose delete after
        */
    private OutputSchedule _schedule;
    private int _numP;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        _config = App.GetCLIConfig();
        setIdleStats();
        setupMemTile();
        setupCPUChart();
       // setUpGanttChart();
        setUpGanttChart();
        _updater = new Updater(_timeElapsed, _iterations, _status, _memTile, _CPUChart, _ganttChart);

    }

    private void setIdleStats() {
        _inputFile.setText(_config.getDotFile());
        _outputFile.setText(_config.getOutputFile());
        _numProcessors.setText(String.valueOf(_config.get_numProcessors()));
        _numCores.setText(String.valueOf(_config.getNumCores()));

    }

    private void setupMemTile() {

        Platform.runLater(() -> {
            _memTile.setMaxValue(((double) Runtime.getRuntime().maxMemory() / (double) (1024 * 1024)));
            _memTile.setBarColor(rgb(255, 255, 255));
            _memTile.setThresholdColor(rgb(255, 255, 255));
            _memTile.setTickLabelDecimals(0);
        });


    }

    private void setupCPUChart() {

        _memTile.setMaxValue(100);
        _memTile.setBarColor(rgb(255, 255, 255));
        _memTile.setThresholdColor(rgb(255, 255, 255));
        _memTile.setTickLabelDecimals(0);
    }


    private void setUpGanttChart() {
//        String[] processors = new String[]{"1", "2", "3", "4"};
        _numP = _config.get_numProcessors();
        List<String> processors = new ArrayList<String>();
        for (int i=1; i<=_numP; i++) {
            processors.add(String.valueOf(i));
        }

        NumberAxis xAxis = new NumberAxis();
        CategoryAxis yAxis = new CategoryAxis();
        xAxis.setLabel("");
        //xAxis.setTickLabelFill(Color.CHOCOLATE);
        yAxis.setLabel("");
        // yAxis.setTickLabelFill(Color.CHOCOLATE);
        yAxis.setTickLabelGap(10);
        yAxis.setCategories(FXCollections.<String>observableArrayList(processors));

        _ganttChart = new GanttChart<Number, String>(xAxis, yAxis);
        _ganttChart.setTitle("Please work for the love of god");
        _ganttChart.setLegendVisible(false);
        _ganttChart.setBlockHeight(50);
       _ganttChart.setAnimated(false);
       double chartHeight = _ganttChart.getMaxHeight();
       _ganttChart.setPrefHeight(380);
       _ganttChart.setPrefWidth(800);
       System.out.println("" + chartHeight);
       _ganttChart.setBlockHeight(200/_numP);

        _ganttBox.getChildren().add(_ganttChart);

        MotionBlur blur = new MotionBlur();
        blur.setAngle(45);
        blur.setRadius(10.5);
       // _ganttBox.setEffect(blur);
        _ganttChart.getStylesheets().add(getClass().getResource("/view/css/Gantt.css").toExternalForm());
        Color color = new Color(0.49,0.57,0.60,1);
      //  _ganttChart.setBackground(new Background(new BackgroundFill(color,null,null)));
        _ganttChart.setStyle("-fx-fill:#31393C");

    }

}
