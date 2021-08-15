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
    private List<String> _processorColor = new ArrayList<String>();
    private List<String> _colors = new ArrayList<String>(Arrays.asList("#F28266", "#F17063", "#EF5D60", "#EE4F64", "#EC4067", "#D9376D", "#C62D72", "#B32478", "#A01A7D"));
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
     * @return processor color
     */
    public String getProcessorColor(int processorNumber) {
        return _processorColor.get(processorNumber);
    }

    /**
     * Assign a color for a specified processor.
     */
    private void setProcessorColors() {
        for (int i = 0; i < _numProcessors; i++) {
            _processorColor.add(_colors.get(i % PROCESSOR_COLORS));
        }

    }
}
