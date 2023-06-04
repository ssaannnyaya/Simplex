package ru.ac.uniyar.Simplex;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import ru.ac.uniyar.Simplex.Utils.SimplexTable;
import java.io.File;
import java.io.IOException;

public class ApplicationController {
    private SimplexView simplexView;
    private GraphicMethodView graphicMethodView;
    private SimplexCreatingView simplexCreatingView;
    private BorderPane root;
    private TabPane simplexTabs;
    private Tab simplexTab;
    private Tab graphicTab;

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
        if (root.getCenter() == null || !root.getCenter().equals(simplexTabs)) {
            clearPane();
            if (simplexView == null || simplexView.isEmpty()) {
                return;
            }
            simplexTabs = new TabPane();
            simplexTab = new Tab("Симплекс метод");
            simplexTab.setClosable(false);
            graphicTab = new Tab("Графический метод");
            graphicTab.setClosable(false);
            simplexTabs.getTabs().addAll(simplexTab, graphicTab);
            root.setCenter(simplexTabs);
        }

        simplexTab.setContent(simplexView.getPane());

        graphicTab.setContent(graphicMethodView.getPane());

        createDecimalButton();
    }

    public void createDecimalButton() {
        Button decimalButton = new Button();
        decimalButton.setText(simplexView.isDecimal()? "Обыкновенные дроби": "Десятичные дроби");
        decimalButton.setOnAction(event -> {
            simplexView.changeDecimal();
            graphicMethodView.changeDecimal();
            decimalButton.setText(simplexView.isDecimal()? "Обыкновенные дроби": "Десятичные дроби");
            showSimplex();
        });
        root.setRight(decimalButton);
    }

    public void createMenus() {
        MenuBar menuBar = new MenuBar();
        Menu mainMenu = new Menu("Файл");
        menuBar.getMenus().add(mainMenu);
        mainMenu.getItems().addAll(createFileReadingMenu(), createFileSavingMenu(), createNewTuskMenu());
        Menu helpMenu = new Menu("Справка");
        MenuItem helpItem = new MenuItem("Справка");
        helpItem.setOnAction(event -> {
            showHelp();
        });
        helpMenu.getItems().add(helpItem);
        menuBar.getMenus().add(helpMenu);
        root.setTop(menuBar);
    }

    public MenuItem createFileReadingMenu(){
        MenuItem menuItem = new MenuItem("Загрузить");
        menuItem.setOnAction((ActionEvent t) -> {
            loadFromFile();
        });
        return menuItem;
    }

    public MenuItem createFileSavingMenu(){
        MenuItem menuItem = new MenuItem("Сохранить");
        menuItem.setOnAction((ActionEvent t) -> {
            saveToFile();
        });
        return menuItem;
    }

    public MenuItem createNewTuskMenu() {
        MenuItem menuItem = new MenuItem("Новая задача");
        menuItem.setOnAction((ActionEvent t) -> {
            newTusk();
        });
        return menuItem;
    }

    public void showHelp() {
        try {
            Stage helpStage = new Stage();
            helpStage.setTitle("Справка");
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("Help.fxml"));
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            helpStage.setScene(scene);
            helpStage.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("File not found");
            alert.setHeaderText("Файл не найден");
            alert.setContentText("Не обнаружен файл со справкой");
            alert.showAndWait();
        }

    }

    public void newTusk() {
        clearPane();
        simplexCreatingView = new SimplexCreatingView();
        root.setCenter(simplexCreatingView.getPane());
        createNewTuskButtons();
    }

    public void createNewTuskButtons(){
        Button accept = new Button("Принять");
        accept.setOnAction(event -> {
            newTuskAccept();
        });

        Button cancel = new Button("Отмена");
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
                alert.setHeaderText("Неверный файл");
                alert.setContentText("Файл не существует или имеет неверный формат");
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