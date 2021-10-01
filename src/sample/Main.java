package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Polygon Drawing, 2021");


        Scene scene = new Scene(root, 1200, 600, Color.WHITESMOKE);
        primaryStage.setScene(scene);
        primaryStage.resizableProperty().setValue(false);
        primaryStage.show();


//        root.setStyle(
//                "    -fx-background-color: #D3D3D333,\n" +
//                "        linear-gradient(from 0.5px 0.0px to 10.5px  0.0px, repeat, black 5%, transparent 5%),\n" +
//                "        linear-gradient(from 0.0px 0.5px to  0.0px 10.5px, repeat, black 5%, transparent 5%);\n"
//                );

//        Polygon polygon = new Polygon();
//        polygon.getPoints().addAll(new Double[]{
//                100.0, 100.0,
//                200.0, 100.0,
//                200.0, 200.0,
//                100.0, 200.0});
//        polygon.setFill(Color.STEELBLUE);
//        Group g=new Group();
//        g.getChildren().add(polygon);
//        root.getChildren().addAll(g);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
