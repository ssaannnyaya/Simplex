package ru.ac.uniyar.Simplex;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class SimplexView {
    private ArrayList<SimplexTable> simplexSteps;
    private int curStep;
    private boolean isDecimal;

    public SimplexView(){
        simplexSteps = new ArrayList<>();
    }

    public SimplexView(SimplexTable task, boolean isDecimal){
        simplexSteps = new ArrayList<>();
        simplexSteps.add(task.clone());
        curStep = 0;
        this.isDecimal = isDecimal;
    }

    public SimplexTable getProblem(){
        return simplexSteps.get(0);
    }

    public Text getFunction() {
        if (isEmpty()) {
            return new Text("");
        }
        return new Text(getFuncAsString());
    }

    public GridPane getTable(int step){
        if (isEmpty() || step < 0 || step >= simplexSteps.size()) {
            return new GridPane();
        }

        int high = 30;
        int width = 60;

        SimplexTable tusk = simplexSteps.get(step);

        GridPane pane = new GridPane();

        pane.getColumnConstraints().add(new ColumnConstraints(width));
        pane.getRowConstraints().add(new RowConstraints(high));

        Rectangle rectangle00 = new Rectangle(width, high);
        rectangle00.setFill(Color.LIGHTGRAY);

        Label cell00 = new Label("X");
        GridPane.setHalignment(cell00, HPos.CENTER);
        GridPane.setValignment(cell00, VPos.CENTER);

        StackPane stack00 = new StackPane();
        stack00.getChildren().addAll(rectangle00, cell00);
        pane.add(stack00, 0, 0);

        for (int j = 0; j < tusk.getN(); j++) {
            pane.getColumnConstraints().add(new ColumnConstraints(width));

            Rectangle rectangleHigh = new Rectangle(width, high);
            rectangleHigh.setFill(Color.LIGHTGRAY);

            Label cellInHigh = new Label("x" + (tusk.getColX()[j]));
            GridPane.setHalignment(cellInHigh, HPos.CENTER);
            GridPane.setValignment(cellInHigh, VPos.CENTER);

            StackPane stackHigh = new StackPane();
            stackHigh.getChildren().addAll(rectangleHigh, cellInHigh);
            pane.add(stackHigh, j + 1, 0);
        }

        {
            pane.getColumnConstraints().add(new ColumnConstraints(width));

            Rectangle rectangleHigh = new Rectangle(width, high);
            rectangleHigh.setFill(Color.LIGHTGRAY);

            Label cellInHigh = new Label("b");
            GridPane.setHalignment(cellInHigh, HPos.CENTER);
            GridPane.setValignment(cellInHigh, VPos.CENTER);

            StackPane stackHigh = new StackPane();
            stackHigh.getChildren().addAll(rectangleHigh, cellInHigh);
            pane.add(stackHigh, tusk.getN() + 1, 0);
        }

        for (int i = 0; i < tusk.getM(); i++) {
            pane.getRowConstraints().add(new RowConstraints(high));

            Rectangle rectangleLeft = new Rectangle(width, high);
            rectangleLeft.setFill(Color.LIGHTGRAY);

            Label cellInLeft = new Label("x" + (tusk.getRowX()[i]));
            GridPane.setHalignment(cellInLeft, HPos.CENTER);
            GridPane.setValignment(cellInLeft, VPos.CENTER);

            StackPane stackLeft = new StackPane();
            stackLeft.getChildren().addAll(rectangleLeft, cellInLeft);
            pane.add(stackLeft, 0, i + 1);
        }

        {
            pane.getRowConstraints().add(new RowConstraints(high));

            Rectangle rectangleLeft = new Rectangle(width, high);
            rectangleLeft.setFill(Color.LIGHTGRAY);

            Label cellInLeft = new Label("f(x)");
            GridPane.setHalignment(cellInLeft, HPos.CENTER);
            GridPane.setValignment(cellInLeft, VPos.CENTER);

            StackPane stackLeft = new StackPane();
            stackLeft.getChildren().addAll(rectangleLeft, cellInLeft);
            pane.add(stackLeft, 0, tusk.getM() + 1);
        }

        for (int j = 0; j <= tusk.getN(); j++) {
            for (int i = 0; i <= tusk.getM(); i++) {

                Label cell = new Label(tusk.getTable()[i][j].getFrString(isDecimal));
                GridPane.setHalignment(cell, HPos.CENTER);
                GridPane.setValignment(cell, VPos.CENTER);

                pane.add(cell, j + 1, i + 1);
            }
        }

        pane.setGridLinesVisible(true);
        pane.setPadding(new Insets(10));
        return pane;
    }

    public VBox getSolvingSteps() {
        VBox vBox = new VBox();
        vBox.getChildren().add(getFunction());
        for (int i = 0; i <= curStep; i++) {
            if (isTimeToDoMainTusk(i - 1)) {
                vBox.getChildren().add(new Text("The basis is found, we pass to the main tusk"));
            }
            vBox.getChildren().add(getTable(i));
        }
        return vBox;
    }

    public boolean isFirstStep(){
        return curStep == 0;
    }

    public boolean isLastStep(){
        return simplexSteps.get(curStep).isSolved() || !simplexSteps.get(curStep).hasSolution();
    }

    public boolean isTimeToDoMainTusk(){
        return simplexSteps.get(curStep - 1).hasAdditionalVars() && !simplexSteps.get(curStep).hasAdditionalVars();
    }

    public boolean isTimeToDoMainTusk(int step){
        return step > 0 && step <= curStep && simplexSteps.get(step - 1).hasAdditionalVars() && !simplexSteps.get(step).hasAdditionalVars();
    }

    public void nextStep(){
        SimplexTable simplexTable = simplexSteps.get(curStep).clone();
        if (curStep != 0) {
            if (isTimeToDoMainTusk()) {
                simplexTable.toMainTask();
                simplexSteps.add(simplexTable);
                curStep++;
                return;
            }
        }
        if (isLastStep()){
            return;
        }
        if (simplexSteps.get(curStep).hasAdditionalVars()) {
            simplexTable.simplexStep();
            int additionalVarColumn = simplexTable.findAdditionalVarColumn();
            if (additionalVarColumn != -1){
                simplexTable.removeCol(additionalVarColumn);
            }
            simplexSteps.add(simplexTable);
            curStep++;
            return;
        }
        simplexTable.simplexStep();
        simplexSteps.add(simplexTable);
        curStep++;
    }

    public void prevStep(){
        if (curStep != 0) {
            simplexSteps.remove(curStep);
            curStep--;
        }
    }

    public void changeDecimal(){
        isDecimal = !isDecimal;
    }

    public boolean isDecimal() {
        return isDecimal;
    }

    public String getFuncAsString() {
        StringBuilder str = new StringBuilder();
        Fraction[] func = simplexSteps.get(0).getFunc();
        if (!func[0].equals(Fraction.zero())) {
            if (func[0].equals(Fraction.one().negative())) {
                str.append("-");
            }
            if (!func[0].equals(Fraction.one())) {
                str.append(func[0].getFrString(isDecimal));
            }
            str.append("X1");
        }
        for (int i = 1; i < func.length-1; i++) {
            if (func[i].moreThen(Fraction.zero())) {
                if (!func[i-1].equals(Fraction.zero())) {
                    str.append("+");
                }
                if (!func[i].equals(Fraction.one())) {
                    str.append(func[i].getFrString(isDecimal));
                }
                str.append("X").append(i + 1);
            }
            if (func[i].lessThen(Fraction.zero())) {
                if (func[i].equals(Fraction.one().negative())) {
                    str.append("-");
                } else {
                    str.append(func[i].getFrString(isDecimal));
                }
                str.append("X").append(i + 1);
            }
        }
        if (func[func.length - 1].moreThen(Fraction.zero())) {
            if (func.length > 1 && !func[func.length-2].equals(Fraction.zero())) {
                str.append("+");
            }
            str.append(func[func.length - 1].getFrString(isDecimal));
        }
        if (func[func.length - 1].lessThen(Fraction.zero())) {
            str.append(func[func.length - 1].getFrString(isDecimal));
        }
        str.append("-->");
        str.append(simplexSteps.get(0).isMinimisation ? "min" : "max");
        return str.toString();
    }

    public boolean isEmpty(){
        return simplexSteps == null || simplexSteps.isEmpty();
    }
}
