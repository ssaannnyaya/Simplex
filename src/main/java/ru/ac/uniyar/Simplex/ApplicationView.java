package ru.ac.uniyar.Simplex;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
        Menu mainMenu = new Menu("Menu");
        menuBar.getMenus().add(mainMenu);
        mainMenu.getItems().addAll(createExitMenu(), createFileReadingMenu(), createFileSavingMenu());
        root.setTop(menuBar);

        root.setCenter(simplexView.getTable());

    }

    public BorderPane getRoot() {
        return root;
    }

    public void nextStep(){
        simplexView.nextStep();
        root.setCenter(simplexView.getTable());
    }

    public void prevStep(){
        simplexView.prevStep();
        root.setCenter(simplexView.getTable());
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

    public MenuItem createExitMenu(){
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction((ActionEvent t) -> {
            Platform.exit();
        });
        return exit;
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
            try {
                SimplexTable simplexTable = new SimplexTable(file.getPath());
                simplexView = new SimplexView(simplexTable, true, false);
                root.setCenter(simplexView.getTable());
                createButtonsPrevNext();
            } catch (NumberFormatException e) {
                e.printStackTrace();
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
