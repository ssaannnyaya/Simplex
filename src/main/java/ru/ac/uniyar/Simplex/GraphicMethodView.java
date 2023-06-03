package ru.ac.uniyar.Simplex;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import ru.ac.uniyar.Simplex.Utils.Fraction;
import ru.ac.uniyar.Simplex.Utils.Graphic;
import ru.ac.uniyar.Simplex.Utils.SimplexTable;

public class GraphicMethodView {
    private BorderPane root;
    private SimplexTable simplexTable;
    private SimplexTable simplexSolution;
    private Graphic graphic;
    private boolean isDecimal;

    public GraphicMethodView(SimplexTable simplexTable, boolean isDecimal) {
        root = new BorderPane();
        this.simplexTable = simplexTable;
        this.isDecimal = isDecimal;

        removeAdditionalVars();
        simplexSolution = simplexTable.clone();
        findSolution();

        graphic = new Graphic(simplexTable.clone(), Fraction.zero(), Fraction.zero());

        createRight();
        createCenter();
        createBottom();
    }

    public BorderPane getPane() {
        return root;
    }

    public void removeAdditionalVars() {
        if (!simplexTable.hasAdditionalVars()) {
            return;
        }
        while (simplexTable.hasSolution() && !simplexTable.isSolved()) {
            simplexTable.simplexStep();
            if (simplexTable.findAdditionalVarColumn() != -1) {
                simplexTable.removeCol(simplexTable.findAdditionalVarColumn());
            }
        }
        if (!simplexTable.hasAdditionalVars()) {
            simplexTable.toMainTask();
        }
    }

    public void findSolution() {
        while (!simplexSolution.isSolved() && simplexSolution.hasSolution()){
            simplexSolution.simplexStep();
        }
    }

    public void createRight() {
        root.setRight(getFuncWithRestrictions());
    }

    public void createCenter() {
        graphic.draw();
        root.setCenter(graphic.getPane());
    }

    public void createBottom() {
        HBox bottom = new HBox();
        Button zoomIn = new Button("+");
        zoomIn.setOnAction(event -> {
            graphic.zoomIn();
            createCenter();
        });

        Button zoomOut = new Button("-");
        zoomOut.setOnAction(event -> {
            graphic.zoomOut();
            createCenter();
        });

        bottom.getChildren().addAll(zoomOut, zoomIn);
        root.setBottom(bottom);
    }

    public void changeDecimal() {
        isDecimal = !isDecimal;
        createRight();
    }

    public VBox getFuncWithRestrictions() {
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(15));
        if (simplexTable.hasAdditionalVars()) {
            Text text = new Text("Система несовместна");
            vBox.getChildren().add(text);
            return vBox;
        }
        if (simplexTable.getN() != 2) {
            Text text = new Text("Невозможно решить в двумерном пространстве");
            vBox.getChildren().add(text);
            return vBox;
        }
        vBox.getChildren().add(new Text("Функция:"));
        Fraction[] func = new Fraction[3];
        int x1, x2;
        if (simplexTable.getColX()[0] < simplexTable.getColX()[1]) {
            func[0] = simplexTable.getTable()[simplexTable.getM()][0].negative();
            x1 = simplexTable.getColX()[0];
            func[1] = simplexTable.getTable()[simplexTable.getM()][1].negative();
            x2 = simplexTable.getColX()[1];
        } else {
            func[0] = simplexTable.getTable()[simplexTable.getM()][1].negative();
            x1 = simplexTable.getColX()[1];
            func[1] = simplexTable.getTable()[simplexTable.getM()][0].negative();
            x2 = simplexTable.getColX()[0];
        }
        func[2] = simplexTable.getTable()[simplexTable.getM()][2];
        StringBuilder funcStr = new StringBuilder();
        funcStr.append("f(X) = ");
        if (!func[0].equals(Fraction.zero())) {
            if (func[0].equals(Fraction.one().negative())) {
                funcStr.append("-");
            } else {
                if (!func[0].equals(Fraction.one())) {
                    funcStr.append(func[0].getFrString(isDecimal));
                }
            }
            funcStr.append("x").append(x1 > 9 ? "\u2081": "").append((char) ('\u2080' + (x1 % 10)));
        }
        if (func[1].moreThen(Fraction.zero())) {
            if (!func[0].equals(Fraction.zero())) {
                funcStr.append("+");
            }
            if (!func[1].equals(Fraction.one())) {
                funcStr.append(func[1].getFrString(isDecimal));
            }
            funcStr.append("x").append(x2 > 9 ? "\u2081": "").append((char) ('\u2080' + (x2 % 10)));
        }
        if (func[1].lessThen(Fraction.zero())) {
            if (func[1].equals(Fraction.one().negative())) {
                funcStr.append("-");
            } else {
                funcStr.append(func[1].getFrString(isDecimal));
            }
            funcStr.append("x").append(x2 > 9 ? "\u2081": "").append((char) ('\u2080' + (x2 % 10)));
        }
        if (func[2].moreThen(Fraction.zero())) {
            if (!func[1].equals(Fraction.zero()) || !func[0].equals(Fraction.zero())) {
                funcStr.append("+");
            }
            funcStr.append(func[2].getFrString(isDecimal));
        }
        Text funcText = new Text(funcStr.toString());
        funcText.setFont(new Font(16));
        vBox.getChildren().add(funcText);

        vBox.getChildren().add(new Text("\nОграничения:"));

        for (int i = 0; i < simplexTable.getM(); i++) {
            Fraction[] restrict = new Fraction[3];
            if (x1 < x2) {
                restrict[0] = simplexTable.getTable()[i][0];
                restrict[1] = simplexTable.getTable()[i][1];
            } else {
                restrict[0] = simplexTable.getTable()[i][1];
                restrict[1] = simplexTable.getTable()[i][0];
            }
            restrict[2] = simplexTable.getTable()[i][2].negative();
            StringBuilder restrictStr = new StringBuilder();
            if (!restrict[0].equals(Fraction.zero())) {
                if (restrict[0].equals(Fraction.one().negative())) {
                    restrictStr.append("-");
                } else {
                    if (!restrict[0].equals(Fraction.one())) {
                        restrictStr.append(restrict[0].getFrString(isDecimal));
                    }
                }
                restrictStr.append("x").append(x1 > 9 ? "\u2081": "").append((char) ('\u2080' + (x1 % 10)));
            }
            if (restrict[1].moreThen(Fraction.zero())) {
                if (!restrict[0].equals(Fraction.zero())) {
                    restrictStr.append("+");
                }
                if (!restrict[1].equals(Fraction.one())) {
                    restrictStr.append(restrict[1].getFrString(isDecimal));
                }
                restrictStr.append("x").append(x2 > 9 ? "\u2081": "").append((char) ('\u2080' + (x2 % 10)));
            }
            if (restrict[1].lessThen(Fraction.zero())) {
                if (restrict[1].equals(Fraction.one().negative())) {
                    restrictStr.append("-");
                } else {
                    restrictStr.append(restrict[1].getFrString(isDecimal));
                }
                restrictStr.append("x").append(x2 > 9 ? "\u2081": "").append((char) ('\u2080' + (x2 % 10)));
            }

            restrictStr.append(" \u2264 ");
            restrictStr.append(restrict[2].getFrString(isDecimal));

            Text restrictText = new Text(restrictStr.toString());
            restrictText.setFont(new Font(16));
            vBox.getChildren().add(restrictText);
        }

        vBox.getChildren().add(new Text("\nВектор нормали:"));
        String normalStr = "n = (" +
                simplexTable.getTable()[simplexTable.getM()][0].negative().getFrString(isDecimal) + "; " +
                simplexTable.getTable()[simplexTable.getM()][1].negative().getFrString(isDecimal) + ")";

        vBox.getChildren().add(new Text(normalStr));

        vBox.getChildren().add(new Text("\nОтвет:"));
        vBox.getChildren().add(new Text(simplexSolution.getAnswerAsString(isDecimal)));

        return vBox;
    }



}
