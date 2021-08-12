package raspberry.scheduler.algorithm;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import raspberry.scheduler.graph.INode;

public class TaskList {
    private final ObservableList<INode> nodesList;

    public TaskList() {
        nodesList = FXCollections.observableArrayList();
    }

    public ObservableList<INode> getPhoneNumbers() {
        return nodesList;
    }
}
