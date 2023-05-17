package ru.ac.uniyar.Simplex;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Simplex method");

        MenuBar menuBar = new MenuBar();
        Menu mainMenu = new Menu("Menu");
        menuBar.getMenus().add(mainMenu);
        mainMenu.getItems().addAll(createExitMenu());

        BorderPane root = new BorderPane();
        root.setTop(menuBar);

        int n = 5;
        int m = 3;
        Fraction[] func = new Fraction[n + 1];
        func[0] = new Fraction("-1");
        func[1] = new Fraction("-1");
        func[2] = new Fraction("-1");
        func[3] = new Fraction("-1");
        func[4] = new Fraction("-1");
        func[5] = new Fraction("0");
        Fraction[][] table = new Fraction[m + 1][n + 1];
        table[0][0] = new Fraction("1");
        table[0][1] = new Fraction("1");
        table[0][2] = new Fraction("2");
        table[0][3] = new Fraction("0");
        table[0][4] = new Fraction("0");
        table[0][5] = new Fraction("6");
        table[1][0] = new Fraction("0");
        table[1][1] = new Fraction("2");
        table[1][2] = new Fraction("2");
        table[1][3] = new Fraction("-1");
        table[1][4] = new Fraction("1");
        table[1][5] = new Fraction("6");
        table[2][0] = new Fraction("1");
        table[2][1] = new Fraction("-1");
        table[2][2] = new Fraction("6");
        table[2][3] = new Fraction("1");
        table[2][4] = new Fraction("1");
        table[2][5] = new Fraction("12");
        SimplexTable simplexTable = new SimplexTable(n, m, func, table);

        SimplexView simplexView = new SimplexView(simplexTable, true, false);

        root.setCenter(simplexView.getTable());

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setPadding(new Insets(10));
        root.setBottom(hBox);

        Button prevStepButton = new Button("Previous step");
        prevStepButton.setOnAction(event -> prevStep(root, simplexView));
        hBox.getChildren().add(prevStepButton);

        Button nextStepButton = new Button("Next step");
        nextStepButton.setOnAction(event -> nextStep(root, simplexView));
        hBox.getChildren().add(nextStepButton);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public MenuItem createExitMenu(){
        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction((ActionEvent t) -> {
            Platform.exit();
        });
        return exit;
    }

    public void nextStep(BorderPane root, SimplexView simplexView){
        simplexView.nextStep();
        root.setCenter(simplexView.getTable());
    }

    public void prevStep(BorderPane root, SimplexView simplexView){
        simplexView.prevStep();
        root.setCenter(simplexView.getTable());
    }

    public static void main(String[] args) {
        launch();
    }
}
