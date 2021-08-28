package raspberry.scheduler.app.visualisation.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class stores and assign the colours for the different processors in the Gantt
 * chart.
 * @author Alan Lin
 */
public class ProcessorColors {

    private final static int PROCESSOR_COLORS = 9;
    private List<String> _processorCurrentColor = new ArrayList<String>();
    private List<String> _processorBestColor = new ArrayList<String>();
    private List<String> _colors = new ArrayList<String>(Arrays.asList("#F28266", "#F17063", "#EF5D60", "#EE4F64", "#EC4067", "#D9376D", "#C62D72", "#B32478", "#A01A7D"));
    private List<String> _colorsGreen = new ArrayList<String>(Arrays.asList("#99E2B4","#88D4AB","#78C6A3","#67B99A","#56AB91","#469D89","#358F80","#248277","#14746F"));
    private int _numProcessors;

    /**
     * Default constructor
     * @param numProcessors number of processors
     */
    public ProcessorColors(int numProcessors) {

        _numProcessors = numProcessors;
        setProcessorColors();
    }


    /**
     * Return the color for the specified processor
     * @param processorNumber processor number
     * @return processor color for the current schedule gantt chart
     */
    public String getProcessorCurrentColor(int processorNumber) {
        return _processorCurrentColor.get(processorNumber);
    }
    /**
     * Return the color for the specified processor
     * @param processorNumber processor number
     * @return processor color for the current best schedule gantt chart
     */
    public String getProcessorBestColor(int processorNumber){
        return _processorBestColor.get(processorNumber);
    }

    /**
     * Assign a color for a specified processor.
     */
    private void setProcessorColors() {
        for (int i = 0; i < _numProcessors; i++) {
            _processorCurrentColor.add(_colors.get(i % PROCESSOR_COLORS));
            _processorBestColor.add(_colorsGreen.get(i % PROCESSOR_COLORS));
        }

    }
}
