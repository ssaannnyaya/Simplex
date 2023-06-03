package ru.ac.uniyar.Simplex;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.ac.uniyar.Simplex.Utils.SimplexTable;

import java.io.File;

public class ApplicationController {
    private SimplexView simplexView;
    private GraphicMethodView graphicMethodView;
    private SimplexCreatingView simplexCreatingView;
    private BorderPane root;

    public ApplicationController(){
        root = new BorderPane();

        createMenus();
    }

    public void clearPane() {
        root.setBottom(null);
        root.setCenter(null);
        root.setRight(null);
    }

    public BorderPane getRoot() {
        return root;
    }

    public void showSimplex() {
        clearPane();
        if (simplexView == null || simplexView.isEmpty()) {
            return;
        }

        TabPane simplexTabs = new TabPane();

        Tab simplexTab = new Tab("Симплекс метод");
        simplexTab.setContent(simplexView.getPane());

        Tab graphicTab = new Tab("Графический метод");
        graphicTab.setContent(graphicMethodView.getPane());

        simplexTabs.getTabs().addAll(simplexTab, graphicTab);

        root.setCenter(simplexTabs);
        createDecimalButton();
    }

    public void createDecimalButton() {
        Button decimalButton = new Button();
        decimalButton.setText(simplexView.isDecimal()? "Normal fractions": "Decimal fractions");
        decimalButton.setOnAction(event -> {
            simplexView.changeDecimal();
            graphicMethodView.changeDecimal();
            decimalButton.setText(simplexView.isDecimal()? "Normal fractions": "Decimal fractions");
            showSimplex();
        });
        root.setRight(decimalButton);
    }

    public void createMenus() {
        MenuBar menuBar = new MenuBar();
        Menu mainMenu = new Menu("Файл");
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
        root.setCenter(simplexCreatingView.getPane());
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
            simplexView = new SimplexView(simplexCreatingView.getTusk(), false);
            graphicMethodView = new GraphicMethodView(simplexCreatingView.getTusk(), false);
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
                    graphicMethodView = new GraphicMethodView(simplexTable, false);
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
        if (simplexView == null || simplexView.isEmpty()) {
            return;
        }
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