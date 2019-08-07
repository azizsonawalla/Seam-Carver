package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scene.fxml"));
        Controller controller = new Controller();
        loader.setController(controller);
        primaryStage.setTitle("Seam Carver");
        Scene scene = new Scene(loader.load(), 1200, 900);
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        controller.initializeUIElements();
        controller.setStage(primaryStage);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
