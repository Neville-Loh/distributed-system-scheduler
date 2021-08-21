package raspberry.scheduler.app.visualisation.controller;

import eu.hansolo.tilesfx.Tile;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import raspberry.scheduler.app.VisualisationLauncher;
import raspberry.scheduler.app.visualisation.util.Updater;
import raspberry.scheduler.app.visualisation.model.GanttChart;
import raspberry.scheduler.app.visualisation.util.ProcessorColors;
import raspberry.scheduler.cli.CLIConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.scene.paint.Color.rgb;

/**
 * The MainController class handles the initialisation of the frontend component of the program.
 * It builds the live components as well as the Gantt chart.
 */
public class MainController implements Initializable {

    // Initialisation of fields
    @FXML
    private Label _inputFile, _outputFile, _numProcessors, _numCores, _timeElapsed, _iterations,_statusText;
    @FXML
    private Tile _memTile, _CPUChart;
    @FXML
    private VBox _ganttBox, _currentBestBox, _statusBox;
    @FXML
    private HBox _logoBox;

    private ProgressIndicator _statusIndicator;
    private CLIConfig _config;
    private ProcessorColors _assignedColors;
    private GanttChart _ganttChart, _currentBest;
    private Updater _updater;
    private int _numP;
    private static final Image _logo = new Image("/icons/Logo.png");

    /**
     * Initialises all the different features of frontend, including live components and the
     * Gantt chart.
     * @param location The location used to resolve relative paths for the root object,
     *                 or null if unknown.
     * @param resources The resources used to localise the root object, or null if not localised.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // setting up all the different components
        _config = VisualisationLauncher.GetCLIConfig();
        insertLogo();
        setIdleStats();
        setupMemTile();
        setupCPUChart();
        setUpGanttChart();
        setUpCurrentBestGanttChart();
        setUpStatus();
        // creating an updater for the live time components
        _updater = new Updater(_timeElapsed, _iterations,_statusText, _memTile, _CPUChart, _ganttChart,_currentBest, _statusBox, _assignedColors);

    }

    /**
     * Inserts the logo into top left hand view
     */
    private void insertLogo(){
        ImageView imv = new ImageView();
        imv.setImage(_logo);
        imv.setFitHeight(50);
        imv.setFitWidth(50);
        _logoBox.getChildren().add(imv);
    }

    /**
     * Initialises the progress indicator for the algorithm, which is represented
     * by a spinning circle during execution and a tick upon completion.
     */
    private void setUpStatus() {

        _statusIndicator = new ProgressIndicator();
        _statusIndicator.setStyle("-fx-accent : black");
        _statusBox.getChildren().add(_statusIndicator);
    }

    /**
     * Initialises the section showing the number of processors running and number of
     * cores used.
     */
    private void setIdleStats() {
        String filePath = _config.getDotFile();
        Path p = Paths.get(filePath);
        String fileName = p.getFileName().toString();
        _inputFile.setText(fileName);
        filePath = _config.getOutputFile();
        p = Paths.get(filePath);
         fileName = p.getFileName().toString();
        _outputFile.setText(fileName);
        _numProcessors.setText(String.valueOf(_config.get_numProcessors()));
        _numCores.setText(String.valueOf(_config.getNumCores()));

    }

    /**
     * Initialises the memory tile, represented by a gauge meter showing the memory usage in
     * megabytes.
     */
    private void setupMemTile() {

            _memTile.setMaxValue(((double) Runtime.getRuntime().maxMemory() / (double) (1024 * 1024)));
            _memTile.setBarColor(rgb(255, 136, 0));
            _memTile.setThresholdColor(rgb(255, 136, 0));
            _memTile.setTickLabelDecimals(0);
            _memTile.setNeedleColor(rgb(0, 0, 0));
            _memTile.setTitle("Memory usage");

    }

    /**
     * Initialises the CPU tile, represented by a gauge meter showing the CPU Usage as a
     * percentage.
     */
    private void setupCPUChart() {

            _CPUChart.setMaxValue(100);
            _CPUChart.setBarColor(rgb(56, 163, 165));
            _CPUChart.setThresholdColor(rgb(56, 163, 165));
            _CPUChart.setTickLabelDecimals(0);
            _CPUChart.setNeedleColor(rgb(0, 0, 0));
            _CPUChart.setTitle("CPU Usage");
    }

    /**
     * Initialises the current schedule Gantt chart, which shows the current output schedule.
     */
    private void setUpGanttChart() {
        _numP = _config.get_numProcessors();
        _assignedColors = new ProcessorColors(_numP);
        List<String> processors = new ArrayList<String>();
        for (int i = 1; i <= _numP; i++) {
            processors.add(String.valueOf(i));
        }

        NumberAxis xAxis = new NumberAxis();
        CategoryAxis yAxis = new CategoryAxis();
        xAxis.setLabel("Time");
        yAxis.setLabel("Processors");
        yAxis.setTickLabelGap(10);
        yAxis.setCategories(FXCollections.<String>observableArrayList(processors));
        _ganttChart = new GanttChart<Number, String>(xAxis, yAxis);
        _ganttChart.setAnimated(false);
        _ganttChart.setTitle("Current Schedule");
        _ganttChart.setLegendVisible(false);
        _ganttChart.setBlockHeight(50);
        _ganttChart.setAnimated(false);
        _ganttChart.setPrefHeight(400);
        _ganttChart.setPrefWidth(900);
        _ganttChart.minHeight(400);
        _ganttChart.minWidth(900);
        _ganttChart.setBlockHeight(230 / _numP);
        _ganttBox.getChildren().add(_ganttChart);
        _ganttChart.getStylesheets().add(getClass().getResource("/view/css/Gantt.css").toExternalForm());
        _ganttChart.setStyle("-fx-fill:#31393C");

    }

    /**
     * Initialises the Current best schedule Gantt chart, which shows the current best output schedule.
     */
    private void setUpCurrentBestGanttChart(){

        List<String> processors = new ArrayList<String>();
        for (int i = 1; i <= _numP; i++) {
            processors.add(String.valueOf(i));
        }
        NumberAxis xAxis = new NumberAxis();
        CategoryAxis yAxis = new CategoryAxis();
        xAxis.setLabel("Time");
        yAxis.setLabel("Processors");
        yAxis.setTickLabelGap(10);
        yAxis.setCategories(FXCollections.<String>observableArrayList(processors));
        _currentBest = new GanttChart<Number, String>(xAxis, yAxis);
        _currentBest.setAnimated(false);
        _currentBest.setTitle("Current Best Schedule");
        _currentBest.setLegendVisible(false);
        _currentBest.setBlockHeight(50);
        _currentBest.setAnimated(false);
        _currentBest.setPrefHeight(400);
        _currentBest.setPrefWidth(900);
        _currentBest.minHeight(400);
        _currentBest.minWidth(900);
        _currentBest.setBlockHeight(230 / _numP);
        _currentBestBox.getChildren().add(_currentBest);
        _currentBest.getStylesheets().add(getClass().getResource("/view/css/Gantt.css").toExternalForm());
        _currentBest.setStyle("-fx-fill:#31393C");
    }

}
