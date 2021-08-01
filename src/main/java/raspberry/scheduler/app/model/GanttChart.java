package raspberry.scheduler.app.model;

import javafx.scene.chart.Axis;
import javafx.scene.chart.XYChart;

public class GanttChart<X,Y> extends XYChart {
    /**
     * Constructs a XYChart given the two axes. The initial content for the chart
     * plot background and plot area that includes vertical and horizontal grid
     * lines and fills, are added.
     *
     * @param axis  X Axis for this XY chart
     * @param axis2 Y Axis for this XY chart
     */
    public GanttChart(Axis axis, Axis axis2) {
        super(axis, axis2);
    }

    @Override
    protected void dataItemAdded(Series series, int itemIndex, Data item) {

    }

    @Override
    protected void dataItemRemoved(Data item, Series series) {

    }

    @Override
    protected void dataItemChanged(Data item) {

    }

    @Override
    protected void seriesAdded(Series series, int seriesIndex) {

    }

    @Override
    protected void seriesRemoved(Series series) {

    }

    @Override
    protected void layoutPlotChildren() {

    }
}
