package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class Controller implements Initializable {

    CanvasHolder activeCanvas;
    ArrayList<CanvasHolder> canvases = new ArrayList<>();
    ArrayList<Color> defaultColors = new ArrayList<>();

    @FXML
    Pane pane;

    @FXML
    Button btNewCanvas;
    @FXML
    Button btClearCanvas;
    @FXML
    Slider slOpacity;
    @FXML
    ListView<String> lvCanvas;

    @FXML
    Button bLoad;
    @FXML
    Button bExport;

    @FXML
    Button btIncScale;
    @FXML
    Button btDecScale;

    @FXML
    TextField tfMaxX;
    @FXML
    TextField tfMaxY;

    @FXML
    ListView<String> lvPoly;
    @FXML
    ColorPicker cpPolyColor;


    Poly poly;
    Double scale = 1.0;
    Double step = 10.0;
    Integer canvasCount = 0;

    public void clearCanvas() {
        activeCanvas.gc.clearRect(0, 0, activeCanvas.canvas.getWidth(), activeCanvas.canvas.getHeight());
        drawGrid(activeCanvas.gc,step);
    }

    @FXML
    public void bExportClick() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(new Stage());

        if(file != null){
            try {
                WritableImage writableImage = new WritableImage((int)activeCanvas.canvas.getWidth(), (int)activeCanvas.canvas.getHeight());
                activeCanvas.canvas.snapshot(null, writableImage);
                RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
                ImageIO.write(renderedImage, "png", file);
            } catch (IOException ex) {
                System.out.println("Ошибка сохранения png файла!");
            }
        }
    }


    @FXML
    public void bNewCanvasClick() {
        ++canvasCount;
        CanvasHolder ch = new CanvasHolder();
        ch.canvas = new Canvas(600,600);
        ch.color = defaultColors.get(canvasCount-1);
        ch.gc = ch.canvas.getGraphicsContext2D();
        ch.opacity = 0.5;
        canvases.add(ch);
        activeCanvas = ch;

        pane.getChildren().add(activeCanvas.canvas);
        activeCanvas.canvas.toFront();

        lvCanvas.getItems().add(canvasCount.toString());
        lvCanvas.getSelectionModel().select(canvasCount-1);

        drawGrid(activeCanvas.gc,step);
    }

    @FXML
    public void bClearClick() {
        clearCanvas();
        activeCanvas.polygons.clear();
    }


    @FXML
    public void bLoadClick() {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("."));
        File file = fileChooser.showOpenDialog(new Stage());
        if(file!=null) {
            poly = new Poly(file.getName());
            readFile(file.getAbsolutePath().toString(),poly);
            poly.setColor(activeCanvas.color);
            poly.setOpacity(activeCanvas.opacity);
            activeCanvas.polygons.add(poly);

        }
        poly.calcMinMax();
        poly.normalize();
        poly.draw(activeCanvas.gc,scale);
        lvPoly.getItems().add(file.getName());
        tfMaxX.setText(Integer.toString(poly.getMaxX()));
        tfMaxY.setText(Integer.toString(poly.getMaxY()));
    }

    @FXML
    public void bIncClick() {
        activeCanvas.gc.clearRect(0, 0, activeCanvas.canvas.getWidth(), activeCanvas.canvas.getHeight());
        step *= 1.1;
        scale = 1.1;
        activeCanvas.gc.scale(scale,scale);
        drawGrid(activeCanvas.gc,step);
        poly.draw(activeCanvas.gc,scale);

    }
    @FXML
    public void bDecClick() {
        activeCanvas.gc.clearRect(0, 0, activeCanvas.canvas.getWidth(), activeCanvas.canvas.getHeight());
        step *= 0.9;
        scale = 0.9;
        activeCanvas.gc.scale(scale,scale);
        drawGrid(activeCanvas.gc,step);
        poly.draw(activeCanvas.gc,scale);
    }

    void readFile(String filename,Poly poly) {
        try(BufferedReader in = new BufferedReader(new FileReader(filename))) {
            String str;
            while ((str = in.readLine()) != null) {
                String[] data = str.split("\\s");
                int x = Integer.parseInt(data[0]);
                int y = Integer.parseInt(data[1]);
                poly.addPoint(x,y);
                }
            } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void drawGrid(GraphicsContext gc, double step) {
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(1);

        for(double y=0; y<=activeCanvas.canvas.getHeight(); y+=step)
            gc.strokeLine(1, y, activeCanvas.canvas.getWidth()-1, y);
        for(double x=0; x<=activeCanvas.canvas.getWidth(); x+=step)
            gc.strokeLine(x, 1, x, activeCanvas.canvas.getHeight()-1);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        defaultColors.add(Color.CADETBLUE);
        defaultColors.add(Color.BLUEVIOLET);
        bNewCanvasClick();
        pane.setStyle("-fx-padding: 1;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 2;" +
                "-fx-border-radius: 3;" +
                "-fx-border-color: grey;");


        activeCanvas.gc.clearRect(0, 0, activeCanvas.canvas.getWidth(), activeCanvas.canvas.getHeight());
        //gc.setFill(Color.ANTIQUEWHITE);
        drawGrid(activeCanvas.gc,step);

        lvCanvas.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                lvPoly.getItems().clear();

                int i = Integer.parseInt(newValue);
                activeCanvas = canvases.get(i-1);
                activeCanvas.canvas.toFront();

                for(Poly p: activeCanvas.polygons)
                    lvPoly.getItems().add(p.getName());
            }
        });
        lvPoly.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                String name = newValue;
                for(Poly p:activeCanvas.polygons) {
                    if(p.getName().equals(name))
                        cpPolyColor.setValue(p.getColor());
                }
            }
        });

        cpPolyColor.setOnAction(new EventHandler() {
            public void handle(Event t) {
                Color c = cpPolyColor.getValue();
                String polyName = lvPoly.getSelectionModel().getSelectedItem();
                for(Poly p:activeCanvas.polygons) {
                    if(p.getName().equals(polyName)) {
                        p.setColor(c);
                        p.draw(activeCanvas.gc, scale);
                    }
                }
            }
        });

        slOpacity.setValue(activeCanvas.opacity*100);

        slOpacity.setOnMouseReleased(event -> {
            activeCanvas.opacity = slOpacity.getValue()/50;
            clearCanvas();
            for(Poly p: activeCanvas.polygons) {
                p.setOpacity(activeCanvas.opacity);
                p.draw(activeCanvas.gc,scale);
            }
        });

        tfMaxX.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    int maxX = Integer.parseInt(newValue);
                    clearCanvas();
                    for(Poly p: activeCanvas.polygons) {
                        p.setMaxX(maxX);
                        p.normalize();
                        p.draw(activeCanvas.gc,scale);
                    }
                });
        tfMaxY.textProperty().addListener(
                (observable, oldValue, newValue) -> {
                    int maxY = Integer.parseInt(newValue);
                    clearCanvas();
                    for(Poly p: activeCanvas.polygons) {
                        p.setMaxY(maxY);
                        p.normalize();
                        p.draw(activeCanvas.gc,scale);
                    }
                });
    }
}
