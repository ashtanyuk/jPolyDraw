package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;

import java.util.ArrayList;
import java.util.OptionalInt;

class Point {
    public int x;
    public int y;
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class Contour {

    ArrayList<Point> points = new ArrayList<>(); // список вершин

    double[] x_norm; // нормализованные значения в диапазоне 0-1
    double[] y_norm;

    int minX = Integer.MAX_VALUE, maxX = 0;
    int minY = Integer.MAX_VALUE, maxY = 0;



    public Contour() {
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxY() {
        return maxY;
    }

    public void addPoint(int x, int y) {
        points.add(new Point(x,y));
    }

    public void normalize(int maxX, int maxY) {
        x_norm = new double[points.size()];
        y_norm = new double[points.size()];

        int i = 0;
        for(Point p: points) {
            x_norm[i] = (double) p.x / maxX;
            y_norm[i++] = (double) p.y / maxY;
        }

    }
    public void calcMinMax() {
        OptionalInt x = points.stream().mapToInt(point -> point.x).max();
        maxX = x.getAsInt();
        OptionalInt y = points.stream().mapToInt(point -> point.y).max();
        maxY = y.getAsInt();
        x =points.stream().mapToInt(point -> point.x).min();
        minX = x.getAsInt();
        y = points.stream().mapToInt(point -> point.y).min();
        minY = y.getAsInt();
    }
    public double[] getXArr(double alphaX) {
        double[] x = new double[points.size()];
        for(int i=0; i < x_norm.length; i++ )
            x[i] = x_norm[i] * alphaX;
        return x;
    }
    public double[] getYArr(double alphaY) {
        double[] y = new double[points.size()];
        for(int i=0; i < y_norm.length; i++ )
            y[i] = y_norm[i] * alphaY;
        return y;
    }
    public boolean search(int x, int y) {
        for(Point p: points)
           if(p.x == x && p.y == y)
              return true;
        return false;
    }
}

public class Poly {

    String name; // по имени файла
    ArrayList<Contour> contours = new ArrayList<>(); // список всех контуров в полигоне
    Color color = Color.CADETBLUE; // цвет по-умолчанию
    double opacity = 0.5; // прозрачность по-умолчанию

    int minX = Integer.MAX_VALUE, maxX = 0;
    int minY = Integer.MAX_VALUE, maxY = 0;

    public int getMaxX() {
        return maxX;
    }
    public int getMaxY() {
        return maxY;
    }


    double alphaX = 600 * 0.75;
    double alphaY = 600 * 0.75;

    boolean cbegin = true;
    Contour c = null;

    public Poly(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public void setColor(Color c) {
        color = c;
    }
    public Color getColor() {
        return color;
    }
    public void setOpacity(double op) {
        opacity = op;
    }
    public double getOpacity() {
        return opacity;
    }

    public void addPoint(int x, int y) {
        if(cbegin == true) {
            c = new Contour();
            c.addPoint(x, y);
            cbegin = false;
        }
        else {
            if(c.search(x,y) == true) {
                cbegin = true;
                contours.add(c);
            }
            else
                c.addPoint(x,y);
        }
    }

    // определяем мах/мин координаты по всем контурам
    public void calcMinMax() {
        for(Contour c: contours) {
            c.calcMinMax();
            if(minX > c.getMinX())
                minX = c.getMinX();
            if(minY > c.getMinY())
                minY = c.getMinY();
            if(maxX < c.getMaxX())
                maxX = c.getMaxX();
            if(maxY < c.getMaxY())
                maxY = c.getMaxY();
        }
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }
    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }

    public void normalize() {
        for(Contour c: contours) {
            c.normalize(maxX, maxY);
        }
    }

    public void draw(GraphicsContext gc, double scale) {
        gc.scale(scale, scale);
        gc.setFill(color);
        gc.setStroke(Color.BLACK);
        gc.setGlobalAlpha(opacity);

        for (Contour c : contours) {
            double x[] = c.getXArr(alphaX);
            double y[] = c.getYArr(alphaY);
            gc.fillPolygon(x, y, x.length);
            gc.strokePolygon(x, y, x.length);

        }

    }

}
