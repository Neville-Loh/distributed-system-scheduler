
package raspberry.scheduler.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import raspberry.scheduler.algorithm.Astar;
import raspberry.scheduler.algorithm.OutputSchedule;
import raspberry.scheduler.cli.CLIConfig;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.io.GraphReader;
import raspberry.scheduler.io.Writer;

import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * This class launches the front end visualisation.
 */
public class App extends Application {

    private static CLIConfig _config;
    private static GraphReader _reader;

    public static void main(CLIConfig config, GraphReader reader) {
        System.out.println("Launched with visualisation");
        _config = config;
        _reader = reader;
        launch();

    }

    @Override
    public void start(Stage primaryStage) throws IOException {
            Parent root = FXMLLoader.load(getClass().getResource("/view/MainView.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

            primaryStage.setResizable(false);
            primaryStage.show();
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                    Platform.exit();
                    System.exit(1);
                }
            });

            new Thread(() -> {
                try {
                    startAlgo();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
    }

    public static CLIConfig GetCLIConfig() {
        return _config;
    }

    private void startAlgo() throws IOException {
        IGraph graph = _reader.read();
        Astar astar = new Astar(graph, _config.get_numProcessors());
        OutputSchedule outputSchedule = astar.findPath();
        Writer writer = new Writer(_config.getOutputFile(), graph, outputSchedule);
        writer.write();
    }

}
