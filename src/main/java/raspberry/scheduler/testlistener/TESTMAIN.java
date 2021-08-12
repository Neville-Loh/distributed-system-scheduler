package raspberry.scheduler.testlistener;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import raspberry.scheduler.app.App;
import raspberry.scheduler.cli.CLIConfig;


public class TESTMAIN extends Application {
    private static testObservable observable;
    private static testObserver micky;

    public static void main(String[] args) {
        micky = new testObserver("micky");
        observable = new testObservable();

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
            startIncrement();
        }).start();


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
