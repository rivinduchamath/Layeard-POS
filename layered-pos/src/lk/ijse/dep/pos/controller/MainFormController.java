package lk.ijse.dep.pos.controller;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.dep.pos.db.DBConnection;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * FXML Controller class
 *
 * @author ranjith-suranga
 */
public class MainFormController implements Initializable {

    public Label pgb;
    @FXML
    private AnchorPane root;
    @FXML
    private ImageView imgCustomer;
    @FXML
    private ImageView imgItem;
    @FXML
    private ImageView imgOrder;
    @FXML
    private ImageView imgViewOrders;
    @FXML
    private Label lblMenu;
    @FXML
    private Label lblDescription;

    /**
     * Initializes the lk.ijse.dep.pos.controller class.
     */
    public void initialize(URL url, ResourceBundle rb) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(2000), root);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    @FXML
    private void playMouseExitAnimation(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();
            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1);
            scaleT.setToY(1);
            scaleT.play();

            icon.setEffect(null);
            lblMenu.setText("Welcome");
            lblDescription.setText("Please select one of above main operations to proceed");
        }
    }

    @FXML
    private void playMouseEnterAnimation(MouseEvent event) {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();

            switch (icon.getId()) {
                case "imgCustomer":
                    lblMenu.setText("Manage Customers");
                    lblDescription.setText("Click to add, edit, delete, search or lk.ijse.dep.pos.view customers");
                    break;
                case "imgItem":
                    lblMenu.setText("Manage Items");
                    lblDescription.setText("Click to add, edit, delete, search or lk.ijse.dep.pos.view items");
                    break;
                case "imgOrder":
                    lblMenu.setText("Place Orders");
                    lblDescription.setText("Click here if you want to place a new order");
                    break;
                case "imgViewOrders":
                    lblMenu.setText("Search Orders");
                    lblDescription.setText("Click if you want to search orders");
                    break;
            }

            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1.2);
            scaleT.setToY(1.2);
            scaleT.play();

            DropShadow glow = new DropShadow();
            glow.setColor(Color.CORNFLOWERBLUE);
            glow.setWidth(20);
            glow.setHeight(20);
            glow.setRadius(20);
            icon.setEffect(glow);
        }
    }


    @FXML
    private void navigate(MouseEvent event) throws IOException {
        if (event.getSource() instanceof ImageView) {
            ImageView icon = (ImageView) event.getSource();

            Parent root = null;

            FXMLLoader fxmlLoader = null;
            switch (icon.getId()) {
                case "imgCustomer":
                    root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/dep/pos/view/ManageCustomerForm.fxml"));
                    break;
                case "imgItem":
                    root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/dep/pos/view/ManageItemForm.fxml"));
                    break;
                case "imgOrder":
                    root = FXMLLoader.load(this.getClass().getResource("/lk/ijse/dep/pos/view/PlaceOrderForm.fxml"));
                    break;
                case "imgViewOrders":
                    fxmlLoader = new FXMLLoader(this.getClass().getResource("/lk/ijse/dep/pos/view/SearchOrdersForm.fxml"));
                    root = fxmlLoader.load();
                    break;
            }

            if (root != null) {
                Scene subScene = new Scene(root);
                Stage primaryStage = (Stage) this.root.getScene().getWindow();

                primaryStage.setScene(subScene);
                primaryStage.centerOnScreen();

                TranslateTransition tt = new TranslateTransition(Duration.millis(350), subScene.getRoot());
                tt.setFromX(-subScene.getWidth());
                tt.setToX(0);
                tt.play();

            }
        }
    }

    public void btnBackUp_onMouseClicked(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save the DB Backup");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL File", "*.sql"));
        File file = fileChooser.showSaveDialog(this.root.getScene().getWindow());
        if (file != null) {
            try {
                Process process = Runtime.getRuntime().exec("mysqldump -h" + DBConnection.host + " -u" + DBConnection.username + "--port" + DBConnection.port +" -p" + DBConnection.password + " " + DBConnection.db + " --result-file " + file.getAbsolutePath() + ((file.getAbsolutePath().endsWith(".sql")) ? "" : ".sql"));

                this.root.getScene().setCursor(Cursor.WAIT);
                int exitCode = process.waitFor();
                if (exitCode != 0) {

                    String[] executeCmd = new String[]{"mysql", DBConnection.db, "-u" + DBConnection.username,"--port" + DBConnection.port , "-p" + DBConnection.password, "-e", " source " + file.getAbsolutePath()};

                    this.root.getScene().setCursor(Cursor.WAIT);
                    this.pgb.setVisible(true);

                    Task task = new Task<Void>() {

                        @Override
                        protected Void call() throws Exception {
                            Process process = Runtime.getRuntime().exec(executeCmd);
                            int existCode = process.waitFor();
                            if (existCode != 0) {

                                BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                                br.lines().forEach(System.out::println);
                                throw new RuntimeException("Error ");
                            } else {
                                return null;
                            }
                        }
                    };
                    task.setOnSucceeded(event -> {
                        new Alert(Alert.AlertType.INFORMATION, "Restore Success").show();
                        Platform.runLater(() -> this.root.getScene().setCursor(Cursor.DEFAULT));
                    });
                    task.setOnFailed(event -> {
                        this.root.getScene().setCursor(Cursor.DEFAULT);
                        new Alert(Alert.AlertType.INFORMATION, "Restore Failed").show();
                    });

                    new Thread(task).start();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void btnRestore_onMouseClicked(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Restore the DB Backup");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SQL File", "*.sql"));
        File file = fileChooser.showOpenDialog(this.root.getScene().getWindow());
        if (file != null) {

            if(DBConnection.password.length() < 0) {
                String[] executeCmd = new String[]{"mysql", DBConnection.db, "-u"  + DBConnection.password, "-e", " source " + file.getAbsolutePath()};
            }

                String[] executeCmd = new String[]{"mysql", DBConnection.db, "-u" + DBConnection.username, "--port" + DBConnection.port , "-p" + DBConnection.password, "-e", " source " + file.getAbsolutePath()};

            this.root.getScene().setCursor(Cursor.WAIT);


            Task task = new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    Process process = Runtime.getRuntime().exec(executeCmd);
                    int existCode = process.waitFor();
                    if (existCode != 0) {

                        BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                        br.lines().forEach(System.out::println);
                        throw new RuntimeException("Error");
                    } else {
                        return null;
                    }
                }
            };
            task.setOnSucceeded(event -> {
                new Alert(Alert.AlertType.INFORMATION, "Restore Success").show();
                Platform.runLater(() -> this.root.getScene().setCursor(Cursor.DEFAULT));
            });
            task.setOnFailed(event -> {
                this.root.getScene().setCursor(Cursor.DEFAULT);
                new Alert(Alert.AlertType.INFORMATION, "Restore Failed").show();
            });

            new Thread(task).start();
        }
    }

}