package raspberry.scheduler.app.visualisation.model;

import javafx.beans.NamedArg;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Constructs a XYChart given the two axes. The initial content for the chart
 * plot background and plot area that includes vertical and horizontal grid
 * lines and fills, are added.
 * X = X Axis for this XY chart
 * Y = Y Axis for this XY chart
 *
 * Gantt chart adapted from https://stackoverflow.com/questions/27975898/gantt-chart-from-scratch
 * Source code credit to user 'Roland' from StackOverflow. This code is licensed under the  Attribution-ShareAlike
 * 4.0 International license. It is free to be used and adapted for any purposes.
 */
public class GanttChart<X, Y> extends XYChart<X, Y> {
    /**
     * This class handles the attributes of a single assigned task, such as its
     * length, colour on the Gantt chart, style class and task number assignment.
     */
    public static class Attributes {

        public long _length;//Length of task given
        public String _color; //Style for plot
        public String _styleClass; //style class used for rectangle borders
        public String _taskNum; //task number for task

        /**
         * Default constructor for Attributes
         * @param lengthMs   - length (time) for task
         * @param color - color for scheduled task
         * @param taskNum    - the task number for the given task
         */
        public Attributes(long lengthMs, String color, String taskNum) {
            super();
            _length = lengthMs;
            _styleClass = "white-border";
            _color = color;
            _taskNum = taskNum;
        }

        /**
         * Returns the task number of the task specified
         * @return task number
         */
        public String getTaskNum() {
            return _taskNum;
        }

        /**
         * Returns the duration of the task
         * @return task duration
         */
        public long getLength() {
            return _length;
        }

        /**
         * Sets the duration of the task
         * @param length task duration
         */
        public void setLength(long length) {
            _length = length;
        }

        /**
         * Returns the style class for the scheduled task
         * @return style class
         */
        public String getStyleClass() {
            return _styleClass;
        }

        /**
         * Assigns a style class for the scheduled task
         * @param styleClass style class for the scheduled task
         */
        public void setStyleClass(String styleClass) {
            _styleClass = styleClass;
        }

        /**
         * Returns the color of the specified task
         * @return task color
         */
        public String getColor(){
            return _color;
        }
    }

    //Set the height of the processor task column to default (10)
    private double blockHeight = 10;

    /**
     * Constructor for Gantt Chart
     */
    public GanttChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis) {
        this(xAxis, yAxis, FXCollections.<Series<X, Y>>observableArrayList());
    }

    /**
     * Constructor for Gantt Chart
     * @param xAxis x axis
     * @param yAxis y axis
     * @param data input data
     */
    public GanttChart(@NamedArg("xAxis") Axis<X> xAxis, @NamedArg("yAxis") Axis<Y> yAxis, @NamedArg("data") ObservableList<Series<X, Y>> data) {
        super(xAxis, yAxis);
        if (!(xAxis instanceof ValueAxis && yAxis instanceof CategoryAxis)) {
            throw new IllegalArgumentException("Axis type incorrect, X and Y should both be NumberAxis");
        }
        setData(data);
    }

    /**
     * Returns style class for specified task.
     */
    private static String getStyleClass(Object obj) {
        return ((Attributes) obj).getStyleClass();
    }

    /**
     * Returns the length of the specified task.
     */
    private static double getLength(Object obj) {
        return ((Attributes) obj).getLength();
    }

    /**
     * Returns the task number of the specified task.
     */
    private static String getTaskNum(Object obj) {
        return ((Attributes) obj).getTaskNum();
    }

    /**
     * Returns the color of the specified task.
     */
    private static String getColor(Object obj){
        return ((Attributes) obj).getColor();
    }

    /**
     * Handles the positioning and plotting of the data points on the Gantt chart.
     */
    @Override
    protected void layoutPlotChildren() {

        for (int seriesIndex = 0; seriesIndex < getData().size(); seriesIndex++) {

            Series<X, Y> series = getData().get(seriesIndex);

            Iterator<Data<X, Y>> iter = getDisplayedDataIterator(series);
            while (iter.hasNext()) {
                Data<X, Y> item = iter.next();
                double x = getXAxis().getDisplayPosition(item.getXValue());
                double y = getYAxis().getDisplayPosition(item.getYValue());
                if (Double.isNaN(x) || Double.isNaN(y)) {
                    continue;
                }
                Node block = item.getNode();
                Rectangle ellipse;
                if (block != null) {
                    if (block instanceof StackPane) {
                        StackPane region = (StackPane) item.getNode();
                        if (region.getShape() == null) {
                            ellipse = new Rectangle(getLength(item.getExtraValue()), getBlockHeight());
                        } else if (region.getShape() instanceof Rectangle) {
                            ellipse = (Rectangle) region.getShape();
                        } else {
                            return;
                        }
                        ellipse.setWidth(getLength(item.getExtraValue()) * ((getXAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis) getXAxis()).getScale()) : 1));
                        ellipse.setHeight(getBlockHeight() * ((getYAxis() instanceof NumberAxis) ? Math.abs(((NumberAxis) getYAxis()).getScale()) : 1));
                        y -= getBlockHeight() / 2.0;

                        // Note: workaround for RT-7689 - saw this in ProgressControlSkin
                        // The region doesn't update itself when the shape is mutated in place, so we
                        // null out and then restore the shape in order to force invalidation.
                        region.setShape(null);
                        region.setShape(ellipse);
                        region.setScaleShape(false);
                        region.setCenterShape(false);
                        region.setCacheShape(false);

                        Label num = new Label(getTaskNum(item.getExtraValue()));

                        num.setMinSize(num.USE_PREF_SIZE, num.USE_PREF_SIZE);
                        num.setPadding(new Insets(ellipse.getHeight(),0,0,ellipse.getWidth()));
                        num.setStyle("-fx-font-family: 'System', Arial; -fx-font-weight: BOLD; -fx-text-fill: white; -fx-font-size: 26");

                        region.getChildren().add(num);

                        block.setLayoutX(x);
                        block.setLayoutY(y);
                    }
                }
            }
        }
    }

    /**
     * Returns the block height
     * @return block height
     */
    public double getBlockHeight() {
        return blockHeight;
    }

    /**
     * Assign a different block height of the processor row from default (10)
     * @param blockHeight block height of processor row in Gantt chart.
     */
    public void setBlockHeight(double blockHeight) {
        this.blockHeight = blockHeight;
    }

    /**
     * Adds tasks when they are passed through the algorithm.
     * @param series series
     * @param itemIndex task index
     * @param item task
     */
    @Override
    protected void dataItemAdded(Series<X, Y> series, int itemIndex, Data<X, Y> item) {
        Node block = createContainer(series, getData().indexOf(series), item, itemIndex);
        getPlotChildren().add(block);
    }

    /**
     * Removes tasks when a different schedule is found.
     * @param item task
     * @param series series
     */
    @Override
    protected void dataItemRemoved(final Data<X, Y> item, final Series<X, Y> series) {
        final Node block = item.getNode();
        getPlotChildren().remove(block);
        removeDataItemFromDisplay(series, item);
    }

    /**
     * Indicates when an item has changed (been removed or added to the Gantt chart).
     * @param item task
     */
    @Override
    protected void dataItemChanged(Data<X, Y> item) {
    }

    /**
     * Indicates when a series of data has been added.
     * @param series series
     * @param seriesIndex series index
     */
    @Override
    protected void seriesAdded(Series<X, Y> series, int seriesIndex) {
        for (int j = 0; j < series.getData().size(); j++) {
            Data<X, Y> item = series.getData().get(j);
            Node container = createContainer(series, seriesIndex, item, j);
            getPlotChildren().add(container);
        }
    }

    /**
     * Indicates when a series of data has been removed.
     * @param series series
     */
    @Override
    protected void seriesRemoved(final Series<X, Y> series) {
        for (XYChart.Data<X, Y> d : series.getData()) {
            final Node container = d.getNode();
            getPlotChildren().remove(container);
        }
        removeSeriesFromDisplay(series);

    }

    /**
     * Initialises the data frame for the Gantt chart
     * @param series series
     * @param seriesIndex series index
     * @param item task
     * @param itemIndex task index
     * @return container
     */
    private Node createContainer(Series<X, Y> series, int seriesIndex, final Data<X, Y> item, int itemIndex) {

        Node container = item.getNode();

        if (container == null) {
            container = new StackPane();
            item.setNode(container);
        }

        container.getStyleClass().add(getStyleClass(item.getExtraValue()));
        container.setStyle(getColor(item.getExtraValue()));

        return container;
    }

    /**
     * Changes the axis range when the number of rows/processors changes.
     */
    @Override
    protected void updateAxisRange() {
        final Axis<X> xa = getXAxis();
        final Axis<Y> ya = getYAxis();
        List<X> xData = null;
        List<Y> yData = null;
        if (xa.isAutoRanging()) xData = new ArrayList<X>();
        if (ya.isAutoRanging()) yData = new ArrayList<Y>();
        if (xData != null || yData != null) {
            for (Series<X, Y> series : getData()) {
                for (Data<X, Y> data : series.getData()) {
                    if (xData != null) {
                        xData.add(data.getXValue());
                        xData.add(xa.toRealValue(xa.toNumericValue(data.getXValue()) + getLength(data.getExtraValue())));
                    }
                    if (yData != null) {
                        yData.add(data.getYValue());
                    }
                }
            }
            if (xData != null) xa.invalidateRange(xData);
            if (yData != null) ya.invalidateRange(yData);
        }
    }

}