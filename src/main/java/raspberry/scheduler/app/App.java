
package raspberry.scheduler.app;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import raspberry.scheduler.cli.CLIConfig;


/**
 * FrontEnd
 */
public class App extends Application{

    private static CLIConfig _config;

        public static void main(CLIConfig config) {
            System.out.println("Launched with visualisation");
            _config = config;
            launch();
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("/view/MainView.fxml")
            );
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
//            primaryStage.setResizable(false);
            primaryStage.show();
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                    Platform.exit();
                    System.exit(1);
                }
            });

        }

        public static CLIConfig GetCLIConfig(){
            return _config;
        }

    }
