package lk.ijse.dep.pos;

import javafx.scene.control.Alert;
import lk.ijse.dep.pos.db.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.logging.*;

public class AppInitializer extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)  {

        try {
            Logger rootLogger = Logger.getLogger("");
            FileHandler fileHandler = new FileHandler("error.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            rootLogger.addHandler(fileHandler);



            DBConnection.getInstance().getConnection();
            URL resource = this.getClass().getResource("/lk/ijse/dep/pos/view/MainForm.fxml");
            Parent root = FXMLLoader.load(resource);
            Scene mainScene = new Scene(root);
            primaryStage.setScene(mainScene);
            primaryStage.setTitle("JDBC POS - 2019");
            primaryStage.centerOnScreen();
//            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            primaryStage.show();
        }catch (IOException e){
            new Alert(Alert.AlertType.INFORMATION,"Something went wrong").show();
           Logger.getLogger("lk.ijse.dep.pos").log(Level.SEVERE,null,e);
        }
    }
}
