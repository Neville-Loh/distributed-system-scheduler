package raspberry.scheduler.app.visualisation.controller;

import eu.hansolo.tilesfx.Tile;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
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
    private NumberAxis _xAxis;
    private CategoryAxis _yAxis;
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
        setUpGanttChartOnSolution();
        _updater = new Updater(_timeElapsed, _iterations, _status, _memTile);

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
        String[] processors = new String[]{"1", "2", "3", "4"};


        NumberAxis xAxis = new NumberAxis();
        CategoryAxis yAxis = new CategoryAxis();
        GanttChart<Number, String> _ganttChart = new GanttChart<Number, String>(xAxis, yAxis);
        xAxis.setLabel("");
        //xAxis.setTickLabelFill(Color.CHOCOLATE);
        xAxis.setMinorTickCount(4);

        yAxis.setLabel("");
        // yAxis.setTickLabelFill(Color.CHOCOLATE);
        yAxis.setTickLabelGap(10);
        yAxis.setCategories(FXCollections.<String>observableArrayList(Arrays.asList(processors)));

        _ganttChart.setTitle("Please work for the love of god");
        _ganttChart.setLegendVisible(false);
        _ganttChart.setBlockHeight(50);
        String processor = processors[0];
        XYChart.Series series1 = new XYChart.Series();
        series1.getData().add(new XYChart.Data(0, processor, new GanttChart.Attributes(1, "status-green", "2")));
        series1.getData().add(new XYChart.Data(1, processor, new GanttChart.Attributes(1, "status-green", "2")));
        series1.getData().add(new XYChart.Data(2, processor, new GanttChart.Attributes(1, "status-green", "2")));
        series1.getData().add(new XYChart.Data(3, processor, new GanttChart.Attributes(1, "status-green", "2")));

        processor = processors[1];
        XYChart.Series series2 = new XYChart.Series();
        series2.getData().add(new XYChart.Data(0, processor, new GanttChart.Attributes(1, "status-green", "2")));
        series2.getData().add(new XYChart.Data(1, processor, new GanttChart.Attributes(1, "status-green", "2")));
        series2.getData().add(new XYChart.Data(2, processor, new GanttChart.Attributes(1, "status-green", "2")));

        processor = processors[2];
        XYChart.Series series3 = new XYChart.Series();
        series3.getData().add(new XYChart.Data(0, processor, new GanttChart.Attributes(1, "status-green", "2")));
        series3.getData().add(new XYChart.Data(1, processor, new GanttChart.Attributes(1, "status-green", "2")));
        series3.getData().add(new XYChart.Data(3, processor, new GanttChart.Attributes(1, "status-green", "2")));

        _ganttChart.getData().addAll(series1, series2, series3);

        _ganttBox.getChildren().add(_ganttChart);
        _ganttChart.getStylesheets().add(getClass().getResource("/view/css/gantt.css").toExternalForm());

    }

    private void setUpGanttChartOnSolution() {
//        String[] processors = new String[]{"1", "2", "3", "4"};
        List<String> processors = new ArrayList<String>();
        for (int i=1; i<=_numP; i++) {
            processors.add(String.valueOf(i));
        }

        NumberAxis xAxis = new NumberAxis();
        CategoryAxis yAxis = new CategoryAxis();
        GanttChart<Number, String> _ganttChart = new GanttChart<Number, String>(xAxis, yAxis);
        xAxis.setLabel("");
        //xAxis.setTickLabelFill(Color.CHOCOLATE);
        yAxis.setLabel("");
        // yAxis.setTickLabelFill(Color.CHOCOLATE);
        yAxis.setTickLabelGap(10);
        yAxis.setCategories(FXCollections.<String>observableArrayList(processors));

        _ganttChart.setTitle("Please work for the love of god");
        _ganttChart.setLegendVisible(false);
        _ganttChart.setBlockHeight(50);

//        List<XYChart.Series> seriesList = new ArrayList<XYChart.Series>();

        for (String processor: processors) {
            XYChart.Series series = new XYChart.Series();
//            seriesList.add(series);
            List<INode> nodesList = _schedule.getNodes(Integer.parseInt(processor));

            for (INode node: nodesList) {
                int startTime = _schedule.getStartTime(node);
                int compTime = node.getValue();
                String nodeName = node.getName();
                System.out.println(nodeName);
                series.getData().add(new XYChart.Data(startTime, processor, new GanttChart.Attributes(compTime, "status-green", nodeName)));


            }
            _ganttChart.getData().add(series);

//            series.getData().add(new XYChart.Data(0, processor, new GanttChart.Attributes(1, "status-green", "2")));
//            series.getData().add(new XYChart.Data(1, processor, new GanttChart.Attributes(1, "status-green", "2")));
//            series.getData().add(new XYChart.Data(2, processor, new GanttChart.Attributes(1, "status-green", "2")));
//            series.getData().add(new XYChart.Data(3, processor, new GanttChart.Attributes(1, "status-green", "2")));
//            _ganttChart.getData().add(series);


        }
//        String processor = processors[0];
//        XYChart.Series series1 = new XYChart.Series();
//        series1.getData().add(new XYChart.Data(0, processor, new GanttChart.Attributes(1, "status-green", "2")));
//        series1.getData().add(new XYChart.Data(1, processor, new GanttChart.Attributes(1, "status-green", "2")));
//        series1.getData().add(new XYChart.Data(2, processor, new GanttChart.Attributes(1, "status-green", "2")));
//        series1.getData().add(new XYChart.Data(3, processor, new GanttChart.Attributes(1, "status-green", "2")));
//
//        processor = processors[1];
//        XYChart.Series series2 = new XYChart.Series();
//        series2.getData().add(new XYChart.Data(0, processor, new GanttChart.Attributes(1, "status-green", "2")));
//        series2.getData().add(new XYChart.Data(1, processor, new GanttChart.Attributes(1, "status-green", "2")));
//        series2.getData().add(new XYChart.Data(2, processor, new GanttChart.Attributes(1, "status-green", "2")));
//
//        processor = processors[2];
//        XYChart.Series series3 = new XYChart.Series();
//        series3.getData().add(new XYChart.Data(0, processor, new GanttChart.Attributes(1, "status-green", "2")));
//        series3.getData().add(new XYChart.Data(1, processor, new GanttChart.Attributes(1, "status-green", "2")));
//        series3.getData().add(new XYChart.Data(3, processor, new GanttChart.Attributes(1, "status-green", "2")));

//        _ganttChart.getData().add();

        _ganttBox.getChildren().add(_ganttChart);
        _ganttChart.getStylesheets().add(getClass().getResource("/view/css/gantt.css").toExternalForm());

    }

    private void setUpTestSolution() throws FileNotFoundException {
            GraphReader reader = new GraphReader("src/test/resources/input/example.dot");
            IGraph graph = reader.read();
            Astar astar = new Astar(graph, 2);
            _schedule = astar.findPath();
            _numP = _schedule.getTotalProcessorNum();
    }

}
