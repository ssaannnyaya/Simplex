package ru.ac.uniyar.Simplex;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class ApplicationController {
    private SimplexView simplexView;
    private SimplexCreatingView simplexCreatingView;
    private BorderPane root;
    private ScrollPane scrollPane;

    public ApplicationController(){
        simplexView = new SimplexView();

        root = new BorderPane();

        scrollPane = new ScrollPane();
        scrollPane.setPadding(new Insets(15));

        createMenus();

        root.setCenter(scrollPane);

    }

    public void clearPane() {
        root.setBottom(null);
        scrollPane.setContent(null);
        root.setRight(null);
    }

    public BorderPane getRoot() {
        return root;
    }

    public void showSimplex() {
        clearPane();
        if (simplexView.isEmpty()) {
            return;
        }
        scrollPane.setContent(simplexView.getSolvingSteps());
        createSolvingButtons();
        createDecimalButton();
    }

    public void nextStep(){
        simplexView.nextStep();
        scrollPane.setContent(simplexView.getSolvingSteps());
    }

    public void prevStep(){
        simplexView.prevStep();
        scrollPane.setContent(simplexView.getSolvingSteps());
    }

    public void createSolvingButtons(){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10));
        root.setBottom(hBox);

        Button prevStepButton = new Button("Previous step");
        Button nextStepButton = new Button("Next step");
        Button solveButton = new Button("Solve");

        prevStepButton.setDisable(simplexView.isFirstStep());
        nextStepButton.setDisable(simplexView.isLastStep() && !simplexView.isTimeToDoMainTusk());
        solveButton.setDisable(simplexView.isLastStep() && !simplexView.isTimeToDoMainTusk());

        prevStepButton.setOnAction(event -> {
            prevStep();
            prevStepButton.setDisable(simplexView.isFirstStep());
            nextStepButton.setDisable(simplexView.isLastStep() && !simplexView.isTimeToDoMainTusk());
            solveButton.setDisable(simplexView.isLastStep() && !simplexView.isTimeToDoMainTusk());
        });

        nextStepButton.setOnAction(event -> {
            nextStep();
            prevStepButton.setDisable(simplexView.isFirstStep());
            nextStepButton.setDisable(simplexView.isLastStep() && !simplexView.isTimeToDoMainTusk());
            solveButton.setDisable(simplexView.isLastStep() && !simplexView.isTimeToDoMainTusk());
        });

        solveButton.setOnAction(event -> {
            while (!simplexView.isLastStep() || simplexView.isTimeToDoMainTusk()) {
                nextStep();
            }
            prevStepButton.setDisable(simplexView.isFirstStep());
            nextStepButton.setDisable(simplexView.isLastStep() && !simplexView.isTimeToDoMainTusk());
            solveButton.setDisable(simplexView.isLastStep() && !simplexView.isTimeToDoMainTusk());
        });

        hBox.getChildren().addAll(prevStepButton, nextStepButton, solveButton);
    }

    public void createDecimalButton() {
        Button decimalButton = new Button();
        decimalButton.setText(simplexView.isDecimal()? "Normal fractions": "Decimal fractions");
        decimalButton.setOnAction(event -> {
            simplexView.changeDecimal();
            decimalButton.setText(simplexView.isDecimal()? "Normal fractions": "Decimal fractions");
            scrollPane.setContent(simplexView.getSolvingSteps());
        });
        root.setRight(decimalButton);
    }

    public void createMenus() {
        MenuBar menuBar = new MenuBar();
        Menu mainMenu = new Menu("File");
        menuBar.getMenus().add(mainMenu);
        mainMenu.getItems().addAll(createFileReadingMenu(), createFileSavingMenu(), createNewTuskMenu());
        root.setTop(menuBar);
    }

    public MenuItem createFileReadingMenu(){
        MenuItem menuItem = new MenuItem("Load from file");
        menuItem.setOnAction((ActionEvent t) -> {
            loadFromFile();
        });
        return menuItem;
    }

    public MenuItem createFileSavingMenu(){
        MenuItem menuItem = new MenuItem("Save to file");
        menuItem.setOnAction((ActionEvent t) -> {
            saveToFile();
        });
        return menuItem;
    }

    public MenuItem createNewTuskMenu() {
        MenuItem menuItem = new MenuItem("New tusk");
        menuItem.setOnAction((ActionEvent t) -> {
            newTusk();
        });
        return menuItem;
    }

    public void newTusk() {
        clearPane();
        simplexCreatingView = new SimplexCreatingView();
        scrollPane.setContent(simplexCreatingView.getPane());
        createNewTuskButtons();
    }

    public void createNewTuskButtons(){
        Button accept = new Button("accept");
        accept.setOnAction(event -> {
            newTuskAccept();
        });

        Button cancel = new Button("cancel");
        cancel.setOnAction(event -> {
            newTuskCancel();
        });

        HBox hBox = new HBox();
        hBox.getChildren().addAll(cancel, accept);
        hBox.setPadding(new Insets(10));

        root.setBottom(hBox);
    }

    public void newTuskAccept() {
        if (simplexCreatingView.isTableOk()) {
            clearPane();
            simplexView = simplexCreatingView.getTusk();
            showSimplex();
        }
    }

    public void newTuskCancel() {
        clearPane();
        showSimplex();
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
                    showSimplex();
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
