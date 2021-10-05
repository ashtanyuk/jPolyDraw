package sample;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
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

    Poly poly; // ссылка на полигон

    ArrayList<Point> points = new ArrayList<>(); // список вершин

    double[] x_norm; // нормализованные значения в диапазоне 0-1
    double[] y_norm;

    public Contour(Poly poly) {
        this.poly = poly;
    }

    public void addPoint(int x, int y) {
        points.add(new Point(x,y));
    }


    public void calcMinMax() {
//        OptionalInt x = points.stream().mapToInt(point -> point.x).max();
//        maxX = x.getAsInt();
//        OptionalInt y = points.stream().mapToInt(point -> point.y).max();
//        maxY = y.getAsInt();
//        x =points.stream().mapToInt(point -> point.x).min();
//        minX = x.getAsInt();
//        y = points.stream().mapToInt(point -> point.y).min();
//        minY = y.getAsInt();
    }

    public double[] getXArrInt() {

        double maxX = poly.getMaxX();
        double minX = poly.getMinX();

        //x'=minx+(maxx-minx)*(x+0.5)/width;
        //y'=miny+(maxy-miny)*(y+0.5)/height;


        double[] x = new double[points.size()];
        for(int i=0; i < points.size(); i++ )
            x[i] = points.get(i).x;
            //x[i] = - minX + (maxX-minX)*(points.get(i).x)/1000;
        return x;
    }
    public double[] getYArrInt() {
        double maxY = poly.getMaxY();
        double minY = poly.getMinY();

        double[] y = new double[points.size()];
        for(int i=0; i < points.size(); i++ )
            y[i] = points.get(i).y;
            //y[i] = - minY + (maxY-minY)*(points.get(i).y)/600;
        return y;
    }
    public double[] getXArrDbl(double alphaX) {
        double[] x = new double[points.size()];
        for(int i=0; i < x_norm.length; i++ )
            x[i] = x_norm[i] * alphaX;
        return x;
    }
    public double[] getYArrDbl(double alphaY) {
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

    int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
    int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;

    public int getMaxX() {
        return maxX;
    }
    public int getMaxY() {
        return maxY;
    }
    public int getMinX() {
        return minX;
    }
    public int getMinY() {
        return minY;
    }

    double alphaX = 1000 * 0.75;
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
            c = new Contour(this);
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
//        for(Contour c: contours) {
//            c.calcMinMax();
//            if(minX > c.getMinX())
//                minX = c.getMinX();
//            if(minY > c.getMinY())
//                minY = c.getMinY();
//            if(maxX < c.getMaxX())
//                maxX = c.getMaxX();
//            if(maxY < c.getMaxY())
//                maxY = c.getMaxY();
//        }
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }
    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }
    public void setMinX(int minX) {
        this.minX = minX;
    }
    public void setMinY(int minY) {
        this.minY = minY;
    }

    public void normalize() {
//        for(Contour c: contours) {
//            c.normalize(minX, maxX, minY, maxY);
//        }
    }

    public void draw(GraphicsContext gc, double scale) {
        gc.scale(scale, scale);
        gc.setFill(color);
        gc.setStroke(Color.BLACK);
        gc.setGlobalAlpha(opacity);

        double xrange = maxX - minX;
        double yrange = maxY - minY;

        //Affine t = new Affine(1, 0, -minX, 0, 1, -minY);
        //Affine t = new Affine();
        //t.appendTranslation( -minX, -minY );
        //t.appendTranslation(0,0);
        //t.appendScale( 1/xratio, 1/yratio );
        //t.appendScale(scale,scale);
        //gc.setTransform( t );

        double xratio = xrange / 1000;
        double yratio = yrange / 600;


        for (Contour c : contours) {
            double x[] = c.getXArrInt();
            double y[] = c.getYArrInt();

            for(int i = 0; i < x.length; i++) {
                x[i] = 1000 * (x[i] - minX)/xrange;
                y[i] = 600  * (y[i] - minY)/yrange;
            }

            gc.fillPolygon(x, y, x.length);
            gc.strokePolygon(x, y, x.length);
        }
    }
}
