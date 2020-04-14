package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.example.janelas.Home;


public class Main extends Application {
    private FXMLLoader loader;

    @Override
    public void start(Stage stage) throws Exception {
        loader = new FXMLLoader(getClass().getResource("janelas/Home.fxml"));
        stage.setScene(new Scene(loader.load()));
        stage.setOnCloseRequest(this::closeApp);
        stage.show();
    }

    private void closeApp(WindowEvent windowEvent) {
        Home controller = loader.getController();
        controller.finalizar();
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
