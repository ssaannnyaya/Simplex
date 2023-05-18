package ru.ac.uniyar.Simplex;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class SimplexView {
    private ArrayList<SimplexTable> simplexSteps;
    private int curStep;
    private boolean isMinimisation;
    private boolean isDecimal;

    public SimplexView(){
        simplexSteps = new ArrayList<>();
    }

    public SimplexView(SimplexTable task, boolean isMinimisation, boolean isDecimal){
        simplexSteps = new ArrayList<>();
        simplexSteps.add(task.clone());
        curStep = 0;
        this.isMinimisation = isMinimisation;
        this.isDecimal = isDecimal;
    }

    public SimplexTable getProblem(){
        return simplexSteps.get(0);
    }

    public GridPane getTable(){
        if (simplexSteps.isEmpty()) {
            GridPane pane = new GridPane();
            return pane;
        }

        int high = 20;
        int width = 40;

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

        for (int j = 0; j < simplexSteps.get(curStep).getN(); j++) {
            pane.getColumnConstraints().add(new ColumnConstraints(width));

            Rectangle rectangleHigh = new Rectangle(width, high);
            rectangleHigh.setFill(Color.LIGHTGRAY);

            Label cellInHigh = new Label("x" + (simplexSteps.get(curStep).getColX()[j]));
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
            pane.add(stackHigh, simplexSteps.get(curStep).getN() + 1, 0);
        }

        for (int i = 0; i < simplexSteps.get(curStep).getM(); i++) {
            pane.getRowConstraints().add(new RowConstraints(high));

            Rectangle rectangleLeft = new Rectangle(width, high);
            rectangleLeft.setFill(Color.LIGHTGRAY);

            Label cellInLeft = new Label("x" + (simplexSteps.get(curStep).getRowX()[i]));
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
            pane.add(stackLeft, 0, simplexSteps.get(curStep).getM() + 1);
        }

        for (int j = 0; j <= simplexSteps.get(curStep).getN(); j++) {
            for (int i = 0; i <= simplexSteps.get(curStep).getM(); i++) {
                Rectangle rectangle = new Rectangle(width, high);
                rectangle.setFill(Color.LIGHTGRAY);

                Label cell = new Label((simplexSteps.get(curStep).getTable()[i][j]).toString());
                GridPane.setHalignment(cell, HPos.CENTER);
                GridPane.setValignment(cell, VPos.CENTER);

                StackPane stack = new StackPane();
                stack.getChildren().addAll(rectangle, cell);
                pane.add(stack, j + 1, i + 1);
            }
        }

        pane.setGridLinesVisible(true);
        pane.setPadding(new Insets(10));
        return pane;
    }

    public void nextStep(){
        SimplexTable simplexTable = simplexSteps.get(curStep).clone();
        if (curStep != 0) {
            if (simplexSteps.get(curStep - 1).hasAdditionalVars() && !simplexSteps.get(curStep).hasAdditionalVars()) {
                simplexTable.toMainTask();
                simplexSteps.add(simplexTable);
                curStep++;
                return;
            }
        }
        if (simplexSteps.get(curStep).isSolved() || !simplexSteps.get(curStep).hasSolution()){
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

}
