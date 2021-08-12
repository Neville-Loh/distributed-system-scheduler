package raspberry.scheduler.testlistener;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import raspberry.scheduler.algorithm.AlgoObservable;
import raspberry.scheduler.algorithm.Astar;
import raspberry.scheduler.app.App;
import raspberry.scheduler.cli.CLIConfig;
import raspberry.scheduler.graph.IGraph;
import raspberry.scheduler.io.GraphReader;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;


public class TESTMAIN extends Application {
    private static AlgoObservable observable;
    private static testObserver micky;

    public static void main(String[] args) throws FileNotFoundException {
        micky = new testObserver("micky");
        observable = AlgoObservable.getInstance();

        observable.addObserver(micky);

        launch();

    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/test.fxml")
        );
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
//            primaryStage.setResizable(false);
        primaryStage.show();
        new Thread(() -> {
            GraphReader reader = new GraphReader("src/test/resources/input/big.dot");
            IGraph graph = null;
            try {
                graph = reader.read();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Astar astar = new Astar(graph, 2);
            astar.findPath();
            if (observable.getIsFinish()) {
                System.out.println("-------------FINISHED-------------");
            }
        }).start();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                Platform.exit();
                System.exit(0);
            }
        });


    }

    private void startIncrement() {
        try {
            observable.increment();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static testObserver getObs(){
        return micky;
    }

}
