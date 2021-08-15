package raspberry.scheduler.app.visualisation;

import eu.hansolo.tilesfx.Tile;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
//import eu.hansolo.tilesfx.Tile;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import raspberry.scheduler.app.visualisation.controller.AlgoObservable;
import raspberry.scheduler.algorithm.OutputSchedule;
import raspberry.scheduler.app.visualisation.controller.MainController;
import raspberry.scheduler.app.visualisation.model.GanttChart;
import raspberry.scheduler.app.visualisation.util.ProcessorColors;
import raspberry.scheduler.graph.INode;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.paint.Color.rgb;

public class Updater {
    private Label _timeElapsed, _iterations;
    private VBox _statusBox;
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
    private ProcessorColors _assignedColors;

    private static final Image doneTick = new Image("/icons/doneTick.png");

    public Updater(Label timeElapsed, Label iterations, Tile memTile, Tile CPUChart, GanttChart ganttChart, VBox statusBox, ProcessorColors assignedColors) {
        _timeElapsed = timeElapsed;
        _iterations = iterations;
        _statusBox = statusBox;
        _memTile = memTile;
        _CPUChart = CPUChart;
        _ganttChart = ganttChart;
        _observable = AlgoObservable.getInstance();
        _assignedColors = assignedColors;
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
        // _polling.stop();
        _timer.stop();

        //clear progress indicator and add done image
        _statusBox.getChildren().clear();
        ImageView imv = new ImageView();
        imv.setImage(doneTick);
        _statusBox.getChildren().add(imv);
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
        if (_observable.getIsFinish() == true) {
            _polling.stop();
        }
        _polling.play();
    }

    private void updateMemTile() {
        Platform.runLater(() -> {
            double totalMem = Runtime.getRuntime().totalMemory();
            double freeMem = Runtime.getRuntime().freeMemory();
            _memTile.setValue((totalMem - freeMem) / (1024 * 1024));
        });


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

        if (_isRunning) {

            Platform.runLater(() -> {
                OutputSchedule solution = _observable.getSolution();
                int numP = _observable.getSolution().getTotalProcessorNum();
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
                        String color = _assignedColors.getProcessorColor(Integer.parseInt(processor) - 1);
                        series.getData().add(new XYChart.Data(startTime, processor, new GanttChart.Attributes(compTime, "-fx-background-color:" + color + ";", nodeName)));


                    }
                    _ganttChart.getData().add(series);
                }
            });

        }
    }


}
