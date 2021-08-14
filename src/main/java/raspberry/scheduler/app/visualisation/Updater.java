package raspberry.scheduler.app.visualisation;

import eu.hansolo.tilesfx.Tile;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
//import eu.hansolo.tilesfx.Tile;
import javafx.util.Duration;
import raspberry.scheduler.algorithm.AlgoObservable;
import raspberry.scheduler.algorithm.OutputSchedule;
import raspberry.scheduler.app.visualisation.controller.MainController;
import raspberry.scheduler.app.visualisation.model.GanttChart;
import raspberry.scheduler.graph.INode;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Updater {
    private Label _timeElapsed, _iterations, _status;
    private Tile _memTile;
    private Tile _CPUChart;
    private Timeline _timer, _polling;
    private boolean _isRunning = true;
    private double _currentTime;
    private double _startTime;
    private DateFormat _timeFormat = new SimpleDateFormat("mm:ss:SSS");
    private AlgoObservable _observable;
    private MainController mainController;
    private GanttChart _ganttChart;

    public Updater(Label timeElapsed, Label iterations, Label status, Tile memTile, Tile CPUChart, GanttChart ganttChart) {
        _timeElapsed = timeElapsed;
        _iterations = iterations;
        _status = status;
        _memTile = memTile;
        _CPUChart = CPUChart;
        _ganttChart = ganttChart;
        _observable = AlgoObservable.getInstance();
//        mainController = new MainController();
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
        _polling.stop();
        _timer.stop();
    }

    /**
     * May change to timer class later - not sure if timeline is going to have massive impact on performance vs using a background thread
     */


    private void startPolling() {
        _polling = new Timeline(new KeyFrame(Duration.millis(200), event -> {
            updateMemTile();
            updateIterations();
            updateCPUChart();
            updateGanttChart();
            if (_observable.getIsFinish()) {
                stopTimer();
            }
        }));
        _polling.setCycleCount(_timer.INDEFINITE);
        _polling.play();
    }

    private void updateMemTile() {

        double totalMem = Runtime.getRuntime().totalMemory();
        double freeMem = Runtime.getRuntime().freeMemory();
        _memTile.setValue((totalMem - freeMem) / (1024 * 1024));
    }

    private void updateIterations() {
        String iteration = String.valueOf(_observable.getIterations());
        _iterations.setText(iteration);
    }

    private void updateCPUChart() {

        OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
        double CPUUsage = ((com.sun.management.OperatingSystemMXBean) bean).getProcessCpuLoad();
        _CPUChart.setValue(CPUUsage * 100);
    }

    private void updateGanttChart() {
//        mainController.setUpGanttChartOnSolution(_observable.getSolution());
        OutputSchedule solution = _observable.getSolution();
        int numP = _observable.getSolution().getTotalProcessorNum();
        List<String> processors = new ArrayList<String>();
        for (int i=1; i<=numP; i++) {
            processors.add(String.valueOf(i));
        }
        _ganttChart.getData().clear();
        for (String processor: processors) {
            XYChart.Series series = new XYChart.Series();
//            seriesList.add(series);
            List<INode> nodesList = solution.getNodes(Integer.parseInt(processor));

            for (INode node: nodesList) {
                int startTime = solution.getStartTime(node);
                int compTime = node.getValue();
                String nodeName = node.getName();
          //      System.out.println(nodeName);
                series.getData().add(new XYChart.Data(startTime, processor, new GanttChart.Attributes(compTime, "status-green", nodeName)));


            }
            _ganttChart.getData().add(series);
        }
    }


}
