package ru.ac.uniyar.Simplex;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class SimplexCreatingView {
    private BorderPane root;
    private Spinner<Integer> nSpinner;
    private Spinner<Integer> mSpinner;
    private TextField[] func;
    private TextField[][] table;
    ToggleGroup minChooser;
    RadioButton minButton;
    RadioButton maxButton;
    private boolean isMinimisation;

    public SimplexCreatingView(){
        root = new BorderPane();

        nSpinner = new Spinner<>(1, 16, 4);
        nSpinner.setEditable(true);

        mSpinner = new Spinner<>(1, 16, 2);
        mSpinner.setEditable(true);

        isMinimisation = true;

        createCenter();
        createTop();
        nSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            createCenter();
            createTop();
        });
        mSpinner.valueProperty().addListener((observable, oldValue, newValue) -> {
            createCenter();
            createTop();
        });
    }

    public void createTop() {
        GridPane pane = new GridPane();
        pane.getRowConstraints().add(new RowConstraints(30));
        pane.getRowConstraints().add(new RowConstraints(30));
        pane.getColumnConstraints().add(new ColumnConstraints(120));
        pane.getColumnConstraints().add(new ColumnConstraints(60));
        pane.getColumnConstraints().add(new ColumnConstraints(50));
        pane.getColumnConstraints().add(new ColumnConstraints(100));
        Text nText = new Text("Count of variables");
        Text mText = new Text("Count of restrictions");

        pane.add(nText, 0, 0);
        pane.add(nSpinner, 1, 0);
        pane.add(mText, 0, 1);
        pane.add(mSpinner, 1, 1);

        minChooser = new ToggleGroup();
        minButton = new RadioButton("minimisation");
        minButton.setOnAction(event -> {
            isMinimisation = true;
            createCenter();
            createTop();
        });
        minButton.setSelected(isMinimisation);

        maxButton = new RadioButton("maximisation");
        maxButton.setOnAction(event -> {
            isMinimisation = false;
            createCenter();
            createTop();
        });
        maxButton.setSelected(!isMinimisation);

        minButton.setToggleGroup(minChooser);
        maxButton.setToggleGroup(minChooser);

        pane.add(minButton, 3, 0);
        pane.add(maxButton, 3, 1);

        root.setTop(pane);
    }

    public void createCenter() {
        GridPane pane = new GridPane();

        func = new TextField[nSpinner.getValue() + 1];
        for (int i = 0; i <= nSpinner.getValue(); i++) {
            func[i] = new TextField();
            int I = i;
            func[i].textProperty().addListener((observable, oldValue, newValue) -> {
                validate(func[I]);
            });
        }

        table = new TextField[mSpinner.getValue() + 1][nSpinner.getValue() + 1];
        for (int j = 0; j <= nSpinner.getValue(); j++) {
            for (int i = 0; i <= mSpinner.getValue(); i++) {
                table[i][j] = new TextField();
                int I = i;
                int J = j;
                table[i][j].textProperty().addListener((observable, oldValue, newValue) -> {
                    validate(table[I][J]);
                });
            }
        }

        int high = 30;
        int width = 60;

        for (int j = 0; j < nSpinner.getValue(); j++) {
            Text text = new Text("X" + (j + 1));
            pane.add(text, j + 1, 0);
            GridPane.setHalignment(text, HPos.CENTER);
            GridPane.setValignment(text, VPos.BOTTOM);

            pane.add(func[j], j + 1, 1);
            pane.getColumnConstraints().add(new ColumnConstraints(width));
        }
        {
            Text text = new Text("b");
            pane.add(text, nSpinner.getValue() + 1, 0);
            GridPane.setHalignment(text, HPos.CENTER);
            GridPane.setValignment(text, VPos.BOTTOM);

            pane.add(func[nSpinner.getValue()], nSpinner.getValue() + 1, 1);
            pane.getColumnConstraints().add(new ColumnConstraints(width));
        }
        {
            Text text = new Text("f(x)");
            pane.add(text, 0, 1);
            GridPane.setHalignment(text, HPos.RIGHT);
            GridPane.setValignment(text, VPos.CENTER);
            pane.getColumnConstraints().add(new ColumnConstraints(width));
        }
        {
            Text text = new Text("-> " + (isMinimisation? "min" : "max"));
            pane.add(text, nSpinner.getValue() + 2, 1);
            GridPane.setHalignment(text, HPos.CENTER);
            GridPane.setValignment(text, VPos.CENTER);
            pane.getColumnConstraints().add(new ColumnConstraints(width));
        }

        for (int i = 0; i < mSpinner.getValue(); i++) {
            pane.getRowConstraints().add(new RowConstraints(high));
            for (int j = 0; j <= nSpinner.getValue(); j++) {
                pane.add(table[i][j], j + 1, i + 2);
            }
        }
        root.setCenter(pane);
    }

    public boolean isTableOk() {
        boolean isOk = true;

        for (int j = 0; j < func.length; j++) {
            try {
                new Fraction(func[j].getText());
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                isOk = false;
                func[j].setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(2), BorderWidths.DEFAULT)));
            }
        }

        for (int j = 0; j <= nSpinner.getValue(); j++) {
            for (int i = 0; i < mSpinner.getValue(); i++) {
                try {
                    new Fraction(table[i][j].getText());
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    isOk = false;
                    table[i][j].setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(2), BorderWidths.DEFAULT)));
                }
            }
        }

        return isOk;
    }

    public BorderPane getPane() {
        return root;
    }

    public SimplexView getTusk() {
        int n = nSpinner.getValue();
        int m = mSpinner.getValue();
        Fraction[] fractionFunc = new Fraction[n + 1];
        Fraction[][] fractionTable = new Fraction[m + 1][n + 1];
        for (int j = 0; j < func.length; j++) {
            fractionFunc[j] = new Fraction(func[j].getText());
        }

        for (int j = 0; j <= nSpinner.getValue(); j++) {
            for (int i = 0; i < mSpinner.getValue(); i++) {
                fractionTable[i][j] = new Fraction(table[i][j].getText());
            }
        }
        SimplexTable simplexTable = new SimplexTable(n, m, fractionFunc, fractionTable, isMinimisation);
        return new SimplexView(simplexTable, false);
    }

    public void validate(TextField textField) {
        boolean isOk = true;
        try {
            new Fraction(textField.getText());
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            textField.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(2), BorderWidths.DEFAULT)));
            isOk = false;
        }
        if (isOk) {
            textField.setBorder(Border.EMPTY);
        }
    }

}
