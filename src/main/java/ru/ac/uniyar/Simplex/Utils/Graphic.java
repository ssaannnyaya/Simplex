package ru.ac.uniyar.Simplex.Utils;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class Graphic {
    private Group graphic;
    private SimplexTable table;
    private Fraction ansX1;
    private Fraction ansX2;
    private Fraction scale;
    private boolean isDecimal;
    private final int size = 720;
    private final int centerX1 = 120;
    private final int centerX2 = 600;
    private final int cell = 60;

    public Graphic(SimplexTable table, Fraction ansX1, Fraction ansX2, boolean isDecimal) {
        graphic = new Group();
        this.table = table.clone();
        this.ansX1 = ansX1;
        this.ansX2 = ansX2;
        scale = Fraction.one();
        this.isDecimal = isDecimal;
    }

    public void draw() {
        graphic.getChildren().clear();
        fillArea();
        drawGrid();
        drawLines();
        drawAnswer();
    }

    public Group getPane() {
        return graphic;
    }

    public Line getLine(Fraction x1, Fraction x2, Fraction b, Color color) {
        if (x1.equals(Fraction.zero()) && x2.equals(Fraction.zero())) {
            return new Line(centerX1, centerX2, centerX1, centerX2);
        }
        if (x1.equals(Fraction.zero())) {
            double px2 = centerX2 - b.divide(x2).divide(scale).toDecimal() * cell;
            if (px2 <= size && px2 >= 0) {
                Line line = new Line(0,
                        px2,
                        size,
                        px2);
                line.setStroke(color);
                return line;
            }
            return new Line(centerX1, centerX2, centerX1, centerX2);
        }
        if (x2.equals(Fraction.zero())) {
            double px1 = centerX1 + b.divide(x1).divide(scale).toDecimal() * cell;
            if (px1 >= 0 && px1 <= size) {
                Line line = new Line(px1,
                        0,
                        px1,
                        size);
                line.setStroke(color);
                return line;
            }
            return new Line(centerX1, centerX2, centerX1, centerX2);
        }
        long px1_2s = centerX2 - (long)( b.plus(x1.multiply(scale).multiply(new Fraction(2,1))).divide(x2).divide(scale).toDecimal() * cell);
        long px2_2s = centerX1 + (long)( b.plus(x2.multiply(scale).multiply(new Fraction(2,1))).divide(x1).divide(scale).toDecimal() * cell);
        long px1_10s = centerX2 - (long)( b.minus(x1.multiply(scale).multiply(new Fraction(10,1))).divide(x2).divide(scale).toDecimal() * cell);
        long px2_10s = centerX1 + (long)( b.minus(x2.multiply(scale).multiply(new Fraction(10,1))).divide(x1).divide(scale).toDecimal() * cell);

        if (px2_2s >= 0 && px2_2s <= size && px1_10s >= 0 && px1_10s <= size) {
            Line line = new Line(px2_2s, size, size, px1_10s);
            line.setStroke(color);
            return line;
        }
        if (px2_2s >= 0 && px2_2s <= size && px2_10s >= 0 && px2_10s <= size) {
            Line line = new Line(px2_2s, size, px2_10s, 0);
            line.setStroke(color);
            return line;
        }
        if (px1_2s >= 0 && px1_2s <= size && px1_10s >= 0 && px1_10s <= size) {
            Line line = new Line(0, px1_2s, size, px1_10s);
            line.setStroke(color);
            return line;
        }
        if (px1_2s >= 0 && px1_2s <= size && px2_10s >= 0 && px2_10s <= size) {
            Line line = new Line(0, px1_2s, px2_10s, 0);
            line.setStroke(color);
            return line;
        }
        if (px2_2s >= 0 && px2_2s <= size && px1_2s >= 0 && px1_2s <= size) {
            Line line = new Line(px2_2s, size, 0, px1_2s);
            line.setStroke(color);
            return line;
        }
        if (px1_10s >= 0 && px1_10s <= size && px2_10s >= 0 && px2_10s <= size) {
            Line line = new Line(size, px1_10s, px2_10s, 0);
            line.setStroke(color);
            return line;
        }
        Line line = new Line(centerX1, centerX2, centerX1, centerX2);
        line.setStroke(color);
        return line;
    }

    public Circle getCircle(Fraction x1, Fraction x2, double r, Color color) {
        long px1 = 120 + (long) (x1.divide(scale).toDecimal() * cell);
        long px2 = 600 - (long) (x2.divide(scale).toDecimal() * cell);
        if (px1 > 0 && px1 < size & px2 > 0 && px2 < size) {
            Circle circle = new Circle(px1, px2, r);
            circle.setStroke(color);
            circle.setFill(color);
            return circle;
        }
        return new Circle(centerX1, centerX2, 0);
    }

    public Line getNormalVector(Fraction startX1, Fraction startX2, Fraction endX1, Fraction endX2, Color color) {
        long px1 = 120 + (long) (startX1.divide(scale).toDecimal() * cell);
        long px2 = 600 - (long) (startX2.divide(scale).toDecimal() * cell);
        Fraction x1 = new Fraction(endX1.toString());
        Fraction x2 = new Fraction(endX2.toString());
        if (px1 > 0 && px1 < size & px2 > 0 && px2 < size) {
            while (x1.abs().moreThen(scale) || x2.abs().moreThen(scale)) {
                x1 = x1.divide(new Fraction("2"));
                x2 = x2.divide(new Fraction("2"));
            }
            Line line = new Line(px1, px2, px1 + x1.toDecimal() * cell, px2 - x2.toDecimal() * cell);
            line.setStroke(color);
            return line;
        }
        return new Line(centerX1, centerX2, centerX1, centerX2);
    }

    public Fraction[] findPoint(Fraction a1, Fraction b1, Fraction c1, Fraction a2, Fraction b2, Fraction c2) {
        if (a1.equals(Fraction.zero())) {
            Fraction x2 = c1.divide(b1);
            Fraction x1 = c2.minus(b2.multiply(x2)).divide(a2);
            return new Fraction[]{x1, x2};
        } else {
            Fraction x2 = a1.multiply(c2).minus(a2.multiply(c1)).divide(a1.multiply(b2).minus(a2.multiply(b1)));
            Fraction x1 = c1.minus(b1.multiply(x2)).divide(a1);
            return new Fraction[]{x1, x2};
        }
    }

    public boolean isIntersect(Fraction a1, Fraction b1, Fraction a2, Fraction b2) {
        return !a1.multiply(b2).minus(a2.multiply(b1)).equals(Fraction.zero());
    }

    public void fillArea() {
        ArrayList<Fraction[]> points = new ArrayList<>();
        Fraction[][] restricts = new Fraction[4][3];
        restricts[0] = new Fraction[]{Fraction.one().negative(), Fraction.zero(), Fraction.zero()};
        restricts[1] = new Fraction[]{Fraction.zero(), Fraction.one().negative(), Fraction.zero()};
        restricts[2] = new Fraction[]{Fraction.one(), Fraction.zero(), new Fraction("10").multiply(scale)};
        restricts[3] = new Fraction[]{Fraction.zero(), Fraction.one(), new Fraction("10").multiply(scale)};
        for (int i = 0; i < table.getM(); i++) {
            for (int j = i + 1; j < table.getM(); j++) {
                if (isIntersect(table.getTable()[i][0], table.getTable()[i][1], table.getTable()[j][0], table.getTable()[j][1])) {
                    boolean inArea = true;
                    Fraction[] point = findPoint(table.getTable()[i][0],
                            table.getTable()[i][1],
                            table.getTable()[i][2],
                            table.getTable()[j][0],
                            table.getTable()[j][1],
                            table.getTable()[j][2]);
                    for (int k = 0; k < table.getM(); k++) {
                        if (point[0].multiply(table.getTable()[k][0]).plus(point[1].multiply(table.getTable()[k][1])).moreThen(table.getTable()[k][2])) {
                            inArea = false;
                        }
                    }
                    for (int k = 0; k < 4; k++) {
                        if (point[0].multiply(restricts[k][0]).plus(point[1].multiply(restricts[k][1])).moreThen(restricts[k][2])) {
                            inArea = false;
                        }
                    }
                    if (inArea) {
                        points.add(point);
                    }
                }
            }
            for (int j = 0; j < 4; j++) {
                if (isIntersect(table.getTable()[i][0], table.getTable()[i][1], restricts[j][0], restricts[j][1])) {
                    boolean inArea = true;
                    Fraction[] point = findPoint(table.getTable()[i][0],
                            table.getTable()[i][1],
                            table.getTable()[i][2],
                            restricts[j][0],
                            restricts[j][1],
                            restricts[j][2]);
                    for (int k = 0; k < table.getM(); k++) {
                        if (point[0].multiply(table.getTable()[k][0]).plus(point[1].multiply(table.getTable()[k][1])).moreThen(table.getTable()[k][2])) {
                            inArea = false;
                        }
                    }
                    for (int k = 0; k < 4; k++) {
                        if (point[0].multiply(restricts[k][0]).plus(point[1].multiply(restricts[k][1])).moreThen(restricts[k][2])) {
                            inArea = false;
                        }
                    }
                    if (inArea) {
                        points.add(point);
                    }
                }
            }
        }
        for (int i = 0; i < 3; i++) {
            for (int j = i + 1; j < 4; j++) {
                if (isIntersect(restricts[i][0], restricts[i][1], restricts[j][0], restricts[j][1])) {
                    boolean inArea = true;
                    Fraction[] point = findPoint(restricts[i][0],
                            restricts[i][1],
                            restricts[i][2],
                            restricts[j][0],
                            restricts[j][1],
                            restricts[j][2]);
                    for (int k = 0; k < table.getM(); k++) {
                        if (point[0].multiply(table.getTable()[k][0]).plus(point[1].multiply(table.getTable()[k][1])).moreThen(table.getTable()[k][2])) {
                            inArea = false;
                        }
                    }
                    for (int k = 0; k < 4; k++) {
                        if (point[0].multiply(restricts[k][0]).plus(point[1].multiply(restricts[k][1])).moreThen(restricts[k][2])) {
                            inArea = false;
                        }
                    }
                    if (inArea) {
                        points.add(point);
                    }
                }
            }
        }
        for (int i = 0; i < points.size() - 2; i++) {
            for (int j = i + 1; j < points.size() - 1; j++) {
                for (int k = j + 1; k < points.size(); k++) {
                    Polygon triangle = new Polygon(
                            120 + points.get(i)[0].divide(scale).toDecimal() * cell, 600 - points.get(i)[1].divide(scale).toDecimal()* cell,
                            120 + points.get(j)[0].divide(scale).toDecimal()* cell, 600 - points.get(j)[1].divide(scale).toDecimal()* cell,
                            120 + points.get(k)[0].divide(scale).toDecimal()* cell, 600 - points.get(k)[1].divide(scale).toDecimal()* cell
                    );
                    triangle.setFill(Color.LIGHTBLUE);
                    graphic.getChildren().add(triangle);
                }
            }
        }
    }

    public void drawGrid() {
        for (int i = -2; i <= 10; i++) {
            graphic.getChildren().add(getLine(Fraction.one(),Fraction.zero(), scale.multiply(new Fraction(i,1)), Color.GRAY));
            graphic.getChildren().add(getLine(Fraction.zero(),Fraction.one(), scale.multiply(new Fraction(i,1)), Color.GRAY));
        }
        graphic.getChildren().add(getLine(Fraction.one(),Fraction.zero(),Fraction.zero(), Color.BLACK));
        graphic.getChildren().add(getLine(Fraction.zero(),Fraction.one(),Fraction.zero(), Color.BLACK));

        Text x1Text = new Text("x" + (table.getColX()[0] > 9 ? "\u2081": "") + (char) ('\u2080' + (table.getColX()[0] % 10)));
        x1Text.setFont(new Font(18));
        x1Text.setX(size + 3);
        x1Text.setY(centerX2);
        graphic.getChildren().add(x1Text);

        Text x2Text = new Text("x" + (table.getColX()[1] > 9 ? "\u2081": "") + (char) ('\u2080' + (table.getColX()[1] % 10)));
        x2Text.setFont(new Font(18));
        x2Text.setX(centerX1 + 2);
        x2Text.setY(-3);
        graphic.getChildren().add(x2Text);

        Text scaleText1 = new Text(scale.getFrString(isDecimal));
        scaleText1.setFont(new Font(14));
        scaleText1.setX(centerX1 + cell + 3);
        scaleText1.setY(centerX2 + 17);
        graphic.getChildren().add(scaleText1);

        Text scaleText2 = new Text(scale.getFrString(isDecimal));
        scaleText2.setFont(new Font(14));
        scaleText2.setX(centerX1 - 20);
        scaleText2.setY(centerX2 - cell - 5);
        graphic.getChildren().add(scaleText2);
    }

    public void drawLines() {
        for (int i = 0; i < table.getM(); i++) {
            graphic.getChildren().add(getLine(table.getTable()[i][0], table.getTable()[i][1], table.getTable()[i][2], Color.BLUE));
        }
    }

    public void drawAnswer() {
        graphic.getChildren().add(getLine(table.getTable()[table.getM()][0],
                table.getTable()[table.getM()][1],
                ansX1.multiply(table.getTable()[table.getM()][0]).plus(ansX2.multiply(table.getTable()[table.getM()][1])),
                Color.GREEN));
        graphic.getChildren().add(getCircle(ansX1, ansX2, 3, Color.RED));
        graphic.getChildren().add(getNormalVector(ansX1, ansX2,
                table.getTable()[table.getM()][0].negative(),
                table.getTable()[table.getM()][1].negative(),
                Color.RED));
    }

    public void zoomIn() {
        if (!scale.lessThen(new Fraction(1, Integer.MAX_VALUE))) {
            scale = scale.divide(new Fraction(2,1));
            draw();
        }
    }

    public void zoomOut() {
        if (!scale.moreThen(new Fraction( Integer.MAX_VALUE, 1))) {
            scale = scale.multiply(new Fraction(2,1));
            draw();
        }
    }

    public void changeDecimal() {
        isDecimal = !isDecimal;
    }
}