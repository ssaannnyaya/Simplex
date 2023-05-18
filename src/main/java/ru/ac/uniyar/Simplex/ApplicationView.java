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

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10));
        root.setBottom(hBox);

        Button prevStepButton = new Button("Previous step");
        prevStepButton.setOnAction(event -> prevStep());
        hBox.getChildren().add(prevStepButton);

        Button nextStepButton = new Button("Next step");
        nextStepButton.setOnAction(event -> nextStep());
        hBox.getChildren().add(nextStepButton);

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


    public MenuItem createExitMenu(){
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction((ActionEvent t) -> {
            Platform.exit();
        });
        return exit;
    }


    public MenuItem createFileReadingMenu(){
        MenuItem exit = new MenuItem("Read from file");
        exit.setOnAction((ActionEvent t) -> {
            readFromFile();
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

    public void readFromFile(){
        FileChooser fileChooser = new FileChooser();
        Stage fileStage = new Stage();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));
        File file = fileChooser.showOpenDialog(fileStage);
        if (file != null) {
            try {
                SimplexTable simplexTable = new SimplexTable(file.getPath());
                simplexView = new SimplexView(simplexTable, true, false);
                root.setCenter(simplexView.getTable());
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
