package raspberry.scheduler.app.visualisation.util;

import eu.hansolo.tilesfx.Tile;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import raspberry.scheduler.app.visualisation.model.AlgoStats;
import raspberry.scheduler.algorithm.common.OutputSchedule;

import raspberry.scheduler.app.visualisation.controller.MainController;

import raspberry.scheduler.app.visualisation.model.GanttChart;
import raspberry.scheduler.graph.INode;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class polls and updates the live time statistics for the frontend components.
 * @author: Alan, Young, Jonathon
 */
public class Updater {
    // Initialisation of variables
    private Label _timeElapsed, _iterations, _statusText;
    private VBox _statusBox;
    private Tile _memTile;
    private Tile _CPUChart;
    private Timeline _timer, _polling;
    private boolean _isRunning = true;
    private double _currentTime;
    private double _startTime;
    private DateFormat _timeFormat = new SimpleDateFormat("mm:ss:SSS");
    private AlgoStats _algoStats;
    private GanttChart _ganttChart, _currentBestSchedule;
    private ProcessorColors _assignedColors;
    private static final Image _doneTick = new Image("/icons/doneTick.png");

    /**
     * Default constructor for class
     * @param timeElapsed time passed since visualisation is launched (may change later)
     * @param iterations number of iterations the algorithm has passed through
     * @param memTile The memory usage tile
     * @param CPUChart CPU chart tile
     * @param ganttChart Gantt chart
     * @param statusBox Status symbol (spinning circle during execution/ tick for completion)
     * @param assignedColors Assigned colors for the processors in the Gantt chart
     */
    public Updater(Label timeElapsed, Label iterations,Label statusText, Tile memTile, Tile CPUChart, GanttChart ganttChart,GanttChart currentBest, VBox statusBox, ProcessorColors assignedColors) {
        _timeElapsed = timeElapsed;
        _iterations = iterations;
        _statusBox = statusBox;
        _statusText = statusText;
        _memTile = memTile;
        _CPUChart = CPUChart;
        _ganttChart = ganttChart;
        _currentBestSchedule = currentBest;
        _algoStats = AlgoStats.getInstance();
        _assignedColors = assignedColors;
        // Begin polling and record time
        startTimer();
        startPolling();
    }

    /**
     * Initialises the timer and begin the sequence for the algorithm and frontend.
     */
    private void startTimer() {
        _startTime = System.currentTimeMillis();
        _timer = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            updateTimer();
        }));
        _timer.setCycleCount(_timer.INDEFINITE);
        _timer.play();
    }

    /**
     * Updates the timer based on time elapsed.
     */
    private void updateTimer() {
        if (_isRunning) {
            _currentTime = System.currentTimeMillis();
            _timeElapsed.setText(_timeFormat.format(_currentTime - _startTime));
            if (_algoStats.getIsFinish()) {
                stopTimer();
            }
        }
    }

    /**
     * Halts the timer when the algorithm has been completed.
     */
    public void stopTimer() {
        _isRunning = false;
        _timer.stop();

        //clear progress indicator and add done image
        _statusBox.getChildren().clear();
        ImageView imv = new ImageView();
        imv.setImage(_doneTick);
        _statusBox.getChildren().add(imv);
        _statusText.setText("Done");
    }

    /**
     * Initialises the polling process, where all live time statistics are collected.
     * //todo May change to timer class later - not sure if timeline is going to
     *  have massive impact on performance vs using a background thread (prob not (said neville))
     */
    private void startPolling() {

        _polling = new Timeline(new KeyFrame(Duration.millis(200), event -> {
            updateMemTile();
            updateIterations();
            updateCPUChart();
            try {
                updateGanttChart();
                updateCurrentBest();
            }catch(NullPointerException e){

            }

        }));
        _polling.setCycleCount(_timer.INDEFINITE);
        _polling.play();
    }

    /**
     * Updates the memory usage tile.
     */
    private void updateMemTile() {
            double totalMem = Runtime.getRuntime().totalMemory();
            double freeMem = Runtime.getRuntime().freeMemory();
            _memTile.setValue((totalMem - freeMem) / (1024 * 1024));

    }

    /**
     * Updates the number of iterations the algorithm has passed through.
     */
    private void updateIterations() {
        String iteration = String.valueOf(_algoStats.getIterations());
        _iterations.setText(iteration);
    }

    /**
     * Updates the CPU usage chart.
     */
    private void updateCPUChart() {

        OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
        double CPUUsage = ((com.sun.management.OperatingSystemMXBean) bean).getProcessCpuLoad();
        _CPUChart.setValue(CPUUsage * 100);
    }

    /**
     * Updates the current schedule Gantt chart to display the current schedule
     */
    private void updateGanttChart() {

        if (_isRunning) {
                OutputSchedule solution = _algoStats.getSolution();
                int numP = _algoStats.getSolution().getTotalProcessorNum();
                List<String> processors = new ArrayList<String>();
                for (int i = 1; i <= numP; i++) {
                    processors.add(String.valueOf(i));
                }
                _ganttChart.getData().clear();
                for (String processor : processors) {
                    XYChart.Series series = new XYChart.Series();
                    List<INode> nodesList = solution.getNodes(Integer.parseInt(processor));
                    for (INode node : nodesList) {
                        int startTime = solution.getStartTime(node);
                        int compTime = node.getValue();
                        String nodeName = node.getName();
                        String color = _assignedColors.getProcessorCurrentColor(Integer.parseInt(processor) - 1);
                        series.getData().add(new XYChart.Data(startTime, processor, new GanttChart.Attributes(compTime, "-fx-background-color:" + color + ";", nodeName)));


                    }
                    _ganttChart.getData().add(series);
                }
        }
    }
    /**
     * Updates the current best schedule Gantt chart to display the current best optimal schedule
     */
    private void updateCurrentBest(){
        if(_isRunning){
            OutputSchedule solution = _algoStats.getcurrentBestSchedule();
            int numP = _algoStats.getSolution().getTotalProcessorNum();
            List<String> processors = new ArrayList<String>();
            for(int i = 1; i <=numP; i++){
                processors.add(String.valueOf(i));
            }
            _currentBestSchedule.getData().clear();
            for(String processor: processors){
                XYChart.Series series = new XYChart.Series();
                List<INode> nodesList = solution.getNodes(Integer.parseInt(processor));
                for(INode node : nodesList){
                    int startTime = solution.getStartTime(node);
                    int compTime = node.getValue();
                    String nodeName = node.getName();
                    String color = _assignedColors.getProcessorBestColor(Integer.parseInt(processor) - 1);
                    series.getData().add(new XYChart.Data(startTime, processor, new GanttChart.Attributes(compTime, "-fx-background-color:" + color + ";", nodeName)));
                }
                _currentBestSchedule.getData().add(series);
            }

        }
    }


}
