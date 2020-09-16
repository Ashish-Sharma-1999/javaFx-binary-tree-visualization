package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.FileInputStream;

public class Main extends Application {
    public Controller controller;
    public double sceneWidth=1000;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader=new FXMLLoader(getClass().getResource("sample.fxml"));
        FileInputStream input=new FileInputStream("D:/e drive/milkyway.jpg");
        Image bgimage=new Image(input);
        BackgroundImage bgImage= new BackgroundImage(bgimage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
        Background background=new Background(bgImage);
        GridPane rootPane=loader.load();
        rootPane.setBackground(background);
        controller=loader.getController();
        primaryStage.setTitle("Hello World");
        primaryStage.setFullScreen(true);
        Pane graphPane=(Pane) rootPane.getChildren().get(0);

        primaryStage.setScene(new Scene(rootPane));
        primaryStage.show();
        sceneWidth=rootPane.getWidth();
        controller.stageWidth=sceneWidth-200;
        graphPane.setPrefWidth(sceneWidth-200);
        primaryStage.setResizable(false);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
