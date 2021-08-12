package raspberry.scheduler.app.visualisation.controller;

import eu.hansolo.tilesfx.Tile;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.effect.MotionBlur;
import javafx.scene.layout.VBox;
import raspberry.scheduler.algorithm.Astar;
import raspberry.scheduler.algorithm.OutputSchedule;
import raspberry.scheduler.app.App;
import raspberry.scheduler.app.visualisation.Updater;
import raspberry.scheduler.app.visualisation.model.GanttChart;
import raspberry.scheduler.cli.CLIConfig;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.chart.XYChart;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.graph.INode;
import raspberry.scheduler.io.GraphReader;

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
    private Tile _memTile;
    @FXML
    private VBox _ganttBox;

    private CLIConfig _config;
    private String _inputFileName;
    private String _outputFileName;
    private GanttChart _ganttChart;
    private int _numSchedules;
    private Updater _updater;
    private GanttChart _gantChart;

        /*
         for testing purpose delete after
        */
    private OutputSchedule _schedule;
    private int _numP;

    public MainController() throws FileNotFoundException {
        setUpTestSolution();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        _config = App.GetCLIConfig();
        setIdleStats();
        setupMemTile();
       // setUpGanttChart();
        setUpGanttChart();
        _updater = new Updater(_timeElapsed, _iterations, _status, _memTile, _ganttChart);

    }

    private void setIdleStats() {
        _inputFile.setText(_config.getDotFile());
        _outputFile.setText(_config.getOutputFile());
        _numProcessors.setText(String.valueOf(_config.get_numProcessors()));
        _numCores.setText(String.valueOf(_config.getNumCores()));

    }

    private void setupMemTile() {
        _memTile.setMaxValue(((double) Runtime.getRuntime().maxMemory() / (double) (1024 * 1024)));
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
        _ganttBox.getChildren().add(_ganttChart);
        MotionBlur blur = new MotionBlur();
        blur.setAngle(45);
        blur.setRadius(10.5);
        _ganttBox.setEffect(blur);
        _ganttChart.getStylesheets().add(getClass().getResource("/view/css/gantt.css").toExternalForm());

    }

    private void setUpTestSolution() throws FileNotFoundException {
//            GraphReader reader = new GraphReader("src/test/resources/input/example.dot");
//            IGraph graph = reader.read();
//            Astar astar = new Astar(graph, 2);
//            _schedule = astar.findPath();
//            _numP = _schedule.getTotalProcessorNum();
    }

}
