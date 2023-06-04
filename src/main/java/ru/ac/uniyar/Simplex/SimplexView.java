package ru.ac.uniyar.Simplex;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import ru.ac.uniyar.Simplex.Utils.Fraction;
import ru.ac.uniyar.Simplex.Utils.SimplexTable;
import java.util.ArrayList;

public class SimplexView {
    private BorderPane root;
    private ArrayList<SimplexTable> simplexSteps;
    private int curStep;
    private int curRow;
    private int curCol;
    private boolean isDecimal;

    public SimplexView(SimplexTable task, boolean isDecimal){
        root = new BorderPane();
        simplexSteps = new ArrayList<>();
        simplexSteps.add(task.clone());
        curStep = 0;
        this.isDecimal = isDecimal;
        curCol = -1;
        curRow = -1;

        createCenter();
        createSolvingButtons();
    }

    public BorderPane getPane() {
        return root;
    }

    public void createCenter() {
        root.setCenter(new ScrollPane(getSolvingSteps()));
    }

    public void createSolvingButtons(){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10));
        root.setBottom(hBox);

        Button prevStepButton = new Button("Предыдущий шаг");
        Button nextStepButton = new Button("Следующий шаг");
        Button solveButton = new Button("Решить");

        prevStepButton.setDisable(isFirstStep());
        nextStepButton.setDisable(isLastStep() && !isTimeToDoMainTusk());
        solveButton.setDisable(isLastStep() && !isTimeToDoMainTusk());

        prevStepButton.setOnAction(event -> {
            prevStep();
            prevStepButton.setDisable(isFirstStep());
            nextStepButton.setDisable(isLastStep() && !isTimeToDoMainTusk());
            solveButton.setDisable(isLastStep() && !isTimeToDoMainTusk());
        });

        nextStepButton.setOnAction(event -> {
            nextStep();
            prevStepButton.setDisable(isFirstStep());
            nextStepButton.setDisable(isLastStep() && !isTimeToDoMainTusk());
            solveButton.setDisable(isLastStep() && !isTimeToDoMainTusk());
        });

        solveButton.setOnAction(event -> {
            while (!isLastStep() || isTimeToDoMainTusk()) {
                nextStep();
            }
            prevStepButton.setDisable(isFirstStep());
            nextStepButton.setDisable(isLastStep() && !isTimeToDoMainTusk());
            solveButton.setDisable(isLastStep() && !isTimeToDoMainTusk());
        });

        hBox.getChildren().addAll(prevStepButton, nextStepButton, solveButton);
    }

    public SimplexTable getProblem(){
        return simplexSteps.get(0);
    }

    public Text getFunction() {
        if (isEmpty()) {
            return new Text("");
        }
        Text function = new Text(simplexSteps.get(0).getFuncAsString(isDecimal));
        function.setFont(new Font(16));
        return function;
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
            int x = Math.abs(tusk.getColX()[j]);
            Label cellInHigh = new Label("x" + (x > 10 ? "\u2081": "") + ((char) ('\u2080' + (x % 10))));
            cellInHigh.setFont(new Font(16));

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

            int x = Math.abs(tusk.getRowX()[i]);
            Label cellInLeft = new Label("x" + (x > 10 ? "\u2081": "") + ((char) ('\u2080' + (x % 10))));
            cellInLeft.setFont(new Font(16));

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

        if (step == curStep && tusk.hasSolution() && !tusk.isSolved()) {
            ArrayList<Integer[]> elementsForStep = tusk.getPossibleElementsForStep();
            for (Integer[] element : elementsForStep) {
                int rowForStep = element[0];
                int colForStep = element[1];
                Rectangle rectangle = new Rectangle(width, high);
                rectangle.setFill(Color.LIGHTBLUE);
                rectangle.setOnMouseClicked(event -> {
                    curRow = element[0];
                    curCol = element[1];
                    createCenter();
                });

                Label cellForStep = new Label(tusk.getTable()[rowForStep][colForStep].getFrString(isDecimal));
                cellForStep.setOnMouseClicked(event -> {
                    curRow = element[0];
                    curCol = element[1];
                    createCenter();
                });
                GridPane.setHalignment(cellForStep, HPos.CENTER);
                GridPane.setValignment(cellForStep, VPos.CENTER);

                StackPane stackLeft = new StackPane();
                stackLeft.getChildren().addAll(rectangle, cellForStep);
                pane.add(stackLeft, colForStep + 1, rowForStep + 1);
            }
            {
                int colForStep;
                int rowForStep;
                if (curCol == -1 && curRow == -1) {
                    colForStep = tusk.colForSimplexStep();
                    rowForStep = tusk.rowForSimplexStep(colForStep);
                } else {
                    colForStep = curCol;
                    rowForStep = curRow;
                }
                Rectangle rectangle = new Rectangle(width, high);
                rectangle.setFill(Color.GREEN);

                Label cellForStep = new Label(tusk.getTable()[rowForStep][colForStep].getFrString(isDecimal));
                GridPane.setHalignment(cellForStep, HPos.CENTER);
                GridPane.setValignment(cellForStep, VPos.CENTER);

                StackPane stackLeft = new StackPane();
                stackLeft.getChildren().addAll(rectangle, cellForStep);
                pane.add(stackLeft, colForStep + 1, rowForStep + 1);
            }
        } else {
            int colForStep = getColForStep(step);
            int rowForStep = getRowForStep(step);
            if (colForStep != -1 && rowForStep != -1) {
                Rectangle rectangle = new Rectangle(width, high);
                rectangle.setFill(Color.CYAN);

                Label cellForStep = new Label(tusk.getTable()[rowForStep][colForStep].getFrString(isDecimal));
                GridPane.setHalignment(cellForStep, HPos.CENTER);
                GridPane.setValignment(cellForStep, VPos.CENTER);

                StackPane stackLeft = new StackPane();
                stackLeft.getChildren().addAll(rectangle, cellForStep);
                pane.add(stackLeft, colForStep + 1, rowForStep + 1);
            }
        }

        pane.setGridLinesVisible(true);
        pane.setPadding(new Insets(10));
        return pane;
    }

    public VBox getSolvingSteps() {
        VBox solvingSteps = new VBox();
        solvingSteps.getChildren().clear();
        solvingSteps.getChildren().add(getFunction());
        for (int i = 0; i <= curStep; i++) {
            if (isTimeToDoMainTusk(i - 1)) {
                solvingSteps.getChildren().add(new Text("Базис найден, переходим к основной задаче"));
            }
            solvingSteps.getChildren().add(getTable(i));
        }
        if (!isEmpty() && !isTimeToDoMainTusk()) {
            solvingSteps.getChildren().add(new Text(simplexSteps.get(curStep).getAnswerAsString(isDecimal)));
        }
        return solvingSteps;
    }

    public boolean isFirstStep(){
        return curStep == 0;
    }

    public boolean isLastStep(){
        return simplexSteps.get(curStep).isSolved() || !simplexSteps.get(curStep).hasSolution();
    }

    public boolean isTimeToDoMainTusk(){
        return curStep != 0 && simplexSteps.get(curStep - 1).hasAdditionalVars() && !simplexSteps.get(curStep).hasAdditionalVars();
    }

    public boolean isTimeToDoMainTusk(int step){
        return step > 0 && step <= curStep && simplexSteps.get(step - 1).hasAdditionalVars() && !simplexSteps.get(step).hasAdditionalVars();
    }

    public int getColForStep(int step) {
        if (step < 0 || step >= curStep || isTimeToDoMainTusk(step)) {
            return -1;
        }
        int[] colX = simplexSteps.get(step).getColX();
        int[] nextColX = simplexSteps.get(step + 1).getColX();
        int col = colX.length - 1;
        for (int j = 0; j < nextColX.length; j++) {
            if (colX[j] != nextColX[j]) {
                return j;
            }
        }
        return col;
    }

    public int getRowForStep(int step) {
        if (step < 0 || step >= curStep) {
            return -1;
        }
        int[] rowX = simplexSteps.get(step).getRowX();
        int[] nextRowX = simplexSteps.get(step + 1).getRowX();
        int row = rowX.length - 1;
        for (int i = 0; i < nextRowX.length; i++) {
            if (rowX[i] != nextRowX[i]) {
                return i;
            }
        }
        return row;
    }

    public void nextStep(){
        SimplexTable simplexTable = simplexSteps.get(curStep).clone();
        if (curStep != 0) {
            if (isTimeToDoMainTusk()) {
                simplexTable.toMainTask();
                simplexSteps.add(simplexTable);
                curStep++;
                curCol = -1;
                curRow = -1;
                createCenter();
                return;
            }
        }
        if (isLastStep()){
            return;
        }
        if (simplexSteps.get(curStep).hasAdditionalVars()) {
            if (curCol != -1 && curRow != -1) {
                simplexTable.simplexStep(curRow, curCol);
            } else {
                simplexTable.simplexStep();
            }
            int additionalVarColumn = simplexTable.findAdditionalVarColumn();
            if (additionalVarColumn != -1){
                simplexTable.removeCol(additionalVarColumn);
            }
            simplexSteps.add(simplexTable);
            curStep++;
            curCol = -1;
            curRow = -1;
            createCenter();
            return;
        }
        if (curCol != -1 && curRow != -1) {
            simplexTable.simplexStep(curRow, curCol);
        } else {
            simplexTable.simplexStep();
        }
        simplexSteps.add(simplexTable);
        curStep++;
        curCol = -1;
        curRow = -1;
        createCenter();
    }

    public void prevStep(){
        if (curStep != 0) {
            simplexSteps.remove(curStep);
            curStep--;
            curCol = -1;
            curRow = -1;
            createCenter();
        }
    }

    public void changeDecimal(){
        isDecimal = !isDecimal;
        createCenter();
    }

    public boolean isDecimal() {
        return isDecimal;
    }

    public boolean isEmpty(){
        return simplexSteps == null || simplexSteps.isEmpty();
    }
}