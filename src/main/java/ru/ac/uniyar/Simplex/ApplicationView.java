package ru.ac.uniyar.Simplex;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ApplicationView {
    private SimplexView simplexView;
    private BorderPane root;

    public ApplicationView(){
        simplexView = new SimplexView();
        root = new BorderPane();

        MenuBar menuBar = new MenuBar();
        Menu mainMenu = new Menu("File");
        menuBar.getMenus().add(mainMenu);
        mainMenu.getItems().addAll(createFileReadingMenu(), createFileSavingMenu());
        root.setTop(menuBar);

        VBox center = new VBox();
        center.getChildren().addAll(simplexView.getFunction(), simplexView.getTable());
        root.setCenter(center);

        createDecimalButton();


    }

    public BorderPane getRoot() {
        return root;
    }

    public void nextStep(){
        simplexView.nextStep();
        VBox center = new VBox();
        center.getChildren().addAll(simplexView.getFunction(), simplexView.getTable());
        root.setCenter(center);
    }

    public void prevStep(){
        simplexView.prevStep();
        VBox center = new VBox();
        center.getChildren().addAll(simplexView.getFunction(), simplexView.getTable());
        root.setCenter(center);
    }

    public void createButtonsPrevNext(){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10));
        root.setBottom(hBox);

        Button prevStepButton = new Button("Previous step");
        Button nextStepButton = new Button("Next step");

        prevStepButton.setDisable(simplexView.isFirstStep());
        nextStepButton.setDisable(simplexView.isLastStep() && !simplexView.isTimeToDoMainTusk());

        prevStepButton.setOnAction(event -> {
            prevStep();
            prevStepButton.setDisable(simplexView.isFirstStep());
            nextStepButton.setDisable(simplexView.isLastStep() && !simplexView.isTimeToDoMainTusk());
        });
        hBox.getChildren().add(prevStepButton);

        nextStepButton.setOnAction(event -> {
            nextStep();
            prevStepButton.setDisable(simplexView.isFirstStep());
            nextStepButton.setDisable(simplexView.isLastStep() && !simplexView.isTimeToDoMainTusk());
        });
        hBox.getChildren().add(nextStepButton);
    }

    public void createDecimalButton() {
        Button decimalButton = new Button();
        decimalButton.setText(simplexView.isDecimal()? "Normal fractions": "Decimal fractions");
        decimalButton.setOnAction(event -> {
            simplexView.changeDecimal();
            decimalButton.setText(simplexView.isDecimal()? "Normal fractions": "Decimal fractions");
            VBox center = new VBox();
            center.getChildren().addAll(simplexView.getFunction(), simplexView.getTable());
            root.setCenter(center);
        });
        root.setRight(decimalButton);
    }

    public MenuItem createFileReadingMenu(){
        MenuItem exit = new MenuItem("Load from file");
        exit.setOnAction((ActionEvent t) -> {
            loadFromFile();
        });
        return exit;
    }

    public MenuItem createFileSavingMenu(){
        MenuItem exit = new MenuItem("Save to file");
        exit.setOnAction((ActionEvent t) -> {
            saveToFile();
        });
        return exit;
    }

    public void loadFromFile(){
        FileChooser fileChooser = new FileChooser();
        Stage fileStage = new Stage();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        File file = fileChooser.showOpenDialog(fileStage);
        if (file != null) {
            if (SimplexTable.isOkFile(file.getPath())) {
                try {
                    SimplexTable simplexTable = new SimplexTable(file.getPath());
                    simplexView = new SimplexView(simplexTable, false);
                    VBox center = new VBox();
                    center.getChildren().addAll(simplexView.getFunction(), simplexView.getTable());
                    root.setCenter(center);
                    createButtonsPrevNext();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid file");
                alert.setHeaderText("Invalid file");
                alert.setContentText("File not exists or has invalid format");
                alert.showAndWait();
            }
        }
    }

    public void saveToFile(){
        FileChooser fileChooser = new FileChooser();
        Stage fileStage = new Stage();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        File file = fileChooser.showSaveDialog(fileStage);
        if (file != null) {
            try {
                simplexView.getProblem().writeToFile(file.getPath());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }


}
