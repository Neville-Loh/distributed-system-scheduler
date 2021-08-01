
package raspberry.scheduler.app;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import raspberry.scheduler.graph.Graph;
import raspberry.scheduler.algorithm.Astar;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Objects;

public class App extends Application{

    private Stage primaryStage;

        public static void main(String[] args) {
            System.out.println("Hello world");
            launch(args);
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("/view/StartScreenView.fxml")
            );

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();

        }
    }
