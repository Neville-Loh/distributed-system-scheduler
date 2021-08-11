
package raspberry.scheduler.app;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import raspberry.scheduler.cli.CLIConfig;


/**
 * FrontEnd
 */
public class App extends Application{

    private Stage _primaryStage;
    private static CLIConfig _config;

        public static void main(CLIConfig config) {
            System.out.println("Hello world");
            _config = config;
            launch();
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("/view/MainView2.fxml")
            );
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        }

        public static CLIConfig GetCLIConfig(){
            return _config;
        }

    }
