package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class CanvasHolder {
        public Canvas canvas;
        public GraphicsContext gc;
        public Color color;
        public double opacity; // 0.0 - 1.0
        public ArrayList<Poly> polygons = new ArrayList<>();
        public int minX, maxX, minY, maxY;
}

