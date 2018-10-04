package de.noack.artificial.sl3.gui;

import de.noack.artificial.sl3.logic.LogicHandler;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    static int xPos = 0;
    static int yPos = 2;
    static Stage window;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        window = primaryStage;
        window.setTitle("Stock simulation Pt.2");
        createStockSizePromptWindow();
        window.setResizable(false);
        window.setFullScreen(false);
        window.show();
    }

    private void createStockSizePromptWindow() {

        Label label = createLabel("Enter maximum stock size (G):");

        TextField textField = createNumberTextField();
        Button button = new Button("Ok");
        button.setOnAction(e -> initializeMarket(Integer.valueOf(textField.getText())));

        VBox layout = new VBox(20);
        layout.getChildren().addAll(label, textField, button);

        window.setScene(new Scene(layout, 260, 100));
    }

    private void initializeMarket(Integer stockSize) {
        LogicHandler.getInstance().initializeMarket(stockSize);
        LogicHandler.getInstance().addItem("a", (int) Math.round(0.01 * stockSize), 3);
        LogicHandler.getInstance().addItem("b", (int) Math.round(0.02 * stockSize), 2);
        LogicHandler.getInstance().addItem("c", (int) Math.round(0.03 * stockSize), 1);
        LogicHandler.getInstance().addItem("d", (int) Math.round(0.04 * stockSize), 7);
        LogicHandler.getInstance().addItem("e", (int) Math.round(0.05 * stockSize), 3);
        LogicHandler.getInstance().addItem("f", (int) Math.round(0.06 * stockSize), 1);
        LogicHandler.getInstance().addItem("g", (int) Math.round(0.07 * stockSize), 6);
        LogicHandler.getInstance().addItem("h", (int) Math.round(0.08 * stockSize), 4);
        LogicHandler.getInstance().addItem("i", (int) Math.round(0.09 * stockSize), 3);
        LogicHandler.getInstance().addItem("j", (int) Math.round(0.1 * stockSize), 2);
        initMainWindow();
    }

    public static void initMainWindow() {
        Scene mainScene = new Scene(loadMainWindow(), 640, 320);
        window.setScene(mainScene);
    }

    private static GridPane loadMainWindow() {
        GridPane gridPane = new GridPane();

        LogicHandler.getInstance().addStockSizeToDisplay(gridPane);
        int column = 0;
        gridPane.add(new Label("Items:"), column++, 1);
        gridPane.add(new Label("Demand:"), column++, 1);
        gridPane.add(new Label("Delivery time:"), column++, 1);
        gridPane.add(new Label("Stock:"), column++, 1);
        gridPane.add(new Label("Threshold:"), column++, 1);
        gridPane.add(new Label("Recommendation:"), column++, 1);

        LogicHandler.getInstance().displayItemData(gridPane, xPos, yPos);
        return gridPane;
    }

    private Label createLabel(String labelText) {
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        return label;
    }

    private TextField createNumberTextField() {
        TextField numberTextField = new TextField();
        numberTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                numberTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        return numberTextField;
    }
}