package ru.ac.uniyar.Simplex;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import ru.ac.uniyar.Simplex.Utils.Fraction;
import ru.ac.uniyar.Simplex.Utils.SimplexTable;

import java.util.ArrayList;

public class SimplexCreatingView {
    private BorderPane root;
    private Spinner<Integer> nSpinner;
    private Spinner<Integer> mSpinner;
    private TextField[] func;
    private TextField[][] table;
    private GridPane topPane;
    private GridPane centerPane;
    private Text kindOfTusk;

    private ToggleGroup minChooser;
    private RadioButton minButton;
    private RadioButton maxButton;

    private ToggleGroup basisChooser;
    private RadioButton artificialBasisButton;
    private RadioButton givenBasisButton;

    private ArrayList<CheckBox> varsCheckBoxes;
    private VBox varsCheckBoxesBox;

    private boolean isMinimisation;
    private boolean isGivenBasis;

    public SimplexCreatingView(){
        root = new BorderPane();

        nSpinner = new Spinner<>(1, 16, 4);
        nSpinner.setEditable(true);

        mSpinner = new Spinner<>(1, 16, 2);
        mSpinner.setEditable(true);

        isMinimisation = true;
        isGivenBasis = false;

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
        topPane = new GridPane();
        topPane.getRowConstraints().add(new RowConstraints(30));
        topPane.getRowConstraints().add(new RowConstraints(30));
        topPane.getColumnConstraints().add(new ColumnConstraints(120));
        topPane.getColumnConstraints().add(new ColumnConstraints(60));
        topPane.getColumnConstraints().add(new ColumnConstraints(50));
        topPane.getColumnConstraints().add(new ColumnConstraints(100));
        topPane.getColumnConstraints().add(new ColumnConstraints(50));
        topPane.getColumnConstraints().add(new ColumnConstraints(100));
        Text nText = new Text("Count of variables");
        Text mText = new Text("Count of restrictions");

        topPane.add(nText, 0, 0);
        topPane.add(nSpinner, 1, 0);
        topPane.add(mText, 0, 1);
        topPane.add(mSpinner, 1, 1);

        minChooser = new ToggleGroup();

        minButton = new RadioButton("minimisation");
        minButton.setOnAction(event -> {
            isMinimisation = true;
            centerPane.getChildren().remove(kindOfTusk);
            kindOfTusk.setText("-> min");
            centerPane.add(kindOfTusk, nSpinner.getValue() + 2, 1);

        });
        minButton.setSelected(isMinimisation);

        maxButton = new RadioButton("maximisation");
        maxButton.setOnAction(event -> {
            isMinimisation = false;
            centerPane.getChildren().remove(kindOfTusk);
            kindOfTusk.setText("-> max");
            centerPane.add(kindOfTusk, nSpinner.getValue() + 2, 1);

        });
        maxButton.setSelected(!isMinimisation);

        minButton.setToggleGroup(minChooser);
        maxButton.setToggleGroup(minChooser);

        topPane.add(minButton, 3, 0);
        topPane.add(maxButton, 3, 1);

        basisChooser = new ToggleGroup();

        artificialBasisButton = new RadioButton("artificial basis");
        artificialBasisButton.setOnAction(event -> {
            isGivenBasis = false;
            root.setRight(null);
        });
        artificialBasisButton.setSelected(!isGivenBasis);

        givenBasisButton = new RadioButton("given basis");
        givenBasisButton.setOnAction(event -> {
            isGivenBasis = true;
            createRight();
        });
        givenBasisButton.setSelected(isGivenBasis);

        artificialBasisButton.setToggleGroup(basisChooser);
        givenBasisButton.setToggleGroup(basisChooser);

        topPane.add(artificialBasisButton, 5, 0);
        topPane.add(givenBasisButton, 5, 1);

        root.setTop(topPane);
    }

    public void createCenter() {
        centerPane = new GridPane();

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
            centerPane.add(text, j + 1, 0);
            GridPane.setHalignment(text, HPos.CENTER);
            GridPane.setValignment(text, VPos.BOTTOM);

            centerPane.add(func[j], j + 1, 1);
            centerPane.getColumnConstraints().add(new ColumnConstraints(width));
        }
        {
            Text text = new Text("b");
            centerPane.add(text, nSpinner.getValue() + 1, 0);
            GridPane.setHalignment(text, HPos.CENTER);
            GridPane.setValignment(text, VPos.BOTTOM);

            centerPane.add(func[nSpinner.getValue()], nSpinner.getValue() + 1, 1);
            centerPane.getColumnConstraints().add(new ColumnConstraints(width));
        }
        {
            Text text = new Text("f(x)");
            centerPane.add(text, 0, 1);
            GridPane.setHalignment(text, HPos.RIGHT);
            GridPane.setValignment(text, VPos.CENTER);
            centerPane.getColumnConstraints().add(new ColumnConstraints(width));
        }
        {
            kindOfTusk = new Text("-> " + (isMinimisation? "min" : "max"));
            centerPane.add(kindOfTusk, nSpinner.getValue() + 2, 1);
            GridPane.setHalignment(kindOfTusk, HPos.CENTER);
            GridPane.setValignment(kindOfTusk, VPos.CENTER);
            centerPane.getColumnConstraints().add(new ColumnConstraints(width));
        }

        for (int i = 0; i < mSpinner.getValue(); i++) {
            centerPane.getRowConstraints().add(new RowConstraints(high));
            for (int j = 0; j <= nSpinner.getValue(); j++) {
                centerPane.add(table[i][j], j + 1, i + 2);
            }
        }
        root.setCenter(centerPane);
    }

    public void createRight() {
        varsCheckBoxes = new ArrayList<>();
        varsCheckBoxesBox = new VBox();
        varsCheckBoxesBox.setOnMouseEntered(event -> {
            varsCheckBoxesBox.setBorder(Border.EMPTY);
        });
        for (int j = 0; j < nSpinner.getValue(); j++) {
            CheckBox checkBox = new CheckBox("X" + (j + 1));
            checkBox.setOnAction(event -> {
                for (int i = 0; i < nSpinner.getValue(); i++) {
                    if (!varsCheckBoxes.get(i).isSelected()) {
                        varsCheckBoxes.get(i).setDisable(isAllVarsChosen());
                    }
                }
            });
            varsCheckBoxes.add(checkBox);
            varsCheckBoxesBox.getChildren().add(checkBox);
        }
        root.setRight(varsCheckBoxesBox);
    }

    public boolean isAllVarsChosen() {
        int count = 0;
        for (int j = 0; j < nSpinner.getValue(); j++) {
            if (varsCheckBoxes.get(j).isSelected()) {
                count++;
            }
        }
        return count == nSpinner.getValue() - mSpinner.getValue();
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

        if (isGivenBasis && !isAllVarsChosen()) {
            isOk = false;
            varsCheckBoxesBox.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, new CornerRadii(2), BorderWidths.DEFAULT)));
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
        SimplexTable simplexTable;
        if (isGivenBasis) {
            int[] vars = new int[nSpinner.getValue() - mSpinner.getValue()];
            int var = 0;
            for (int j = 0; j < nSpinner.getValue(); j++) {
                if (varsCheckBoxes.get(j).isSelected()) {
                    vars[var] = j + 1;
                    var++;
                }
            }
            simplexTable = new SimplexTable(n, m, fractionFunc, fractionTable, vars, isMinimisation);
        } else {
            simplexTable = new SimplexTable(n, m, fractionFunc, fractionTable, isMinimisation);
        }
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