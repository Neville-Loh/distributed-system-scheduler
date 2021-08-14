package raspberry.scheduler.app.visualisation.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessorColors {

    private final static int PROCESSOR_COLORS = 9;
    private List<String> _processorColor = new ArrayList<String>();
    private List<String> _colors = new ArrayList<String>(Arrays.asList("#F28266", "#F17063", "#EF5D60", "#EE4F64", "#EC4067", "#D9376D", "#C62D72", "#B32478", "#A01A7D"));
    private int _numProcessors;

    public ProcessorColors(int numProcessors) {

        _numProcessors = numProcessors;
        setProcessorColors();
    }

    public String getProcessorColor(int processorNumber) {
        return _processorColor.get(processorNumber);
    }

    private void setProcessorColors() {
        for (int i = 0; i < _numProcessors; i++) {
            System.out.println(_numProcessors);
            System.out.println(i);
            System.out.println(_colors.get(0));
            _processorColor.add(_colors.get(i % PROCESSOR_COLORS));
        }

    }
}
