package ru.ac.uniyar.Simplex.Utils;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class Graphic {
    private Group graphic;
    private SimplexTable table;
    private Fraction ansX1;
    private Fraction ansX2;
    private Fraction scale;
    private final int size = 720;
    private final int centerX1 = 120;
    private final int centerX2 = 600;
    private final int cell = 60;

    public Graphic(SimplexTable table, Fraction ansX1, Fraction ansX2) {
        graphic = new Group();
        this.table = table.clone();
        this.ansX1 = ansX1;
        this.ansX2 = ansX2;
        scale = new Fraction("1");
    }

    public void draw() {
        graphic.getChildren().clear();
        drawGrid();
    }

    public Group getPane() {
        return graphic;
    }

    public Line getLine(Fraction x1, Fraction x2, Fraction b, Color color) {
        if (x1.equals(Fraction.zero()) && x2.equals(Fraction.zero())) {
            Line line = new Line(centerX1, centerX2, centerX1, centerX2);
            line.setStroke(color);
            return line;
        }
        if (x1.equals(Fraction.zero())) {
            Line line = new Line(0,
                    centerX2 - b.divide(x2).divide(scale).toDecimal() * cell,
                    size,
                    centerX2 - b.divide(x2).divide(scale).toDecimal() * cell);
            line.setStroke(color);
            return line;
        }
        if (x2.equals(Fraction.zero())) {
            Line line = new Line(centerX1 + b.divide(x1).divide(scale).toDecimal() * cell,
                    0,
                    centerX1 + b.divide(x1).divide(scale).toDecimal() * cell,
                    size);
            line.setStroke(color);
            return line;
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

    public void drawGrid() {
        for (int i = -2; i <= 10; i++) {
            graphic.getChildren().add(getLine(Fraction.one(),Fraction.zero(), scale.multiply(new Fraction(i,1)), Color.GRAY));
            graphic.getChildren().add(getLine(Fraction.zero(),Fraction.one(), scale.multiply(new Fraction(i,1)), Color.GRAY));
        }
        graphic.getChildren().add(getLine(Fraction.one(),Fraction.zero(),Fraction.zero(), Color.BLACK));
        graphic.getChildren().add(getLine(Fraction.zero(),Fraction.one(),Fraction.zero(), Color.BLACK));
        graphic.getChildren().add(getLine(new Fraction("-1"),new Fraction("1"), new Fraction("2"), Color.BLUE));

    }

    public void zoomIn() {
        if (!scale.isInDanger()) {
            scale = scale.divide(new Fraction(2,1));
            draw();
        }
    }

    public void zoomOut() {
        if (!scale.isInDanger()) {
            scale = scale.multiply(new Fraction(2,1));
            draw();
        }
    }
}
