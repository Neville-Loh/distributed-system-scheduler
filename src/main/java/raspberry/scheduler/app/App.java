
package raspberry.scheduler.app;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * FrontEnd
 */
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
