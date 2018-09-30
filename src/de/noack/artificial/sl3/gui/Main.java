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
    static List<Scene> createItemPrompts = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        window = primaryStage;
        window.setTitle("Stock simulation");
        window.setScene(createItemCountPromptWindow());
        window.setResizable(false);
        window.setFullScreen(false);
        window.show();
    }

    private Scene createItemCountPromptWindow() {

        Label label = new Label("Enter item count (max. 10):");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        TextField textField = new TextField();
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (Integer.valueOf(textField.getText()) > 10) {
                textField.setText("10");
            }
        });

        Button button = new Button("Ok");
        button.setOnAction(e -> commitCount(Integer.valueOf(textField.getText())));

        VBox layout = new VBox(20);
        layout.getChildren().addAll(label, textField, button);

        return new Scene(layout, 300, 100);
    }

    private void commitCount(int count) {
        for (int i = 0; i < count; i++) {
            TextField nameTextField = new TextField();

            TextField stockTextField = new TextField();
            stockTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    stockTextField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });

            Button button = new Button("Submit");

            Scene scene = createItemPromptWindow(nameTextField, stockTextField, button);

            button.setOnAction(e -> addItem(nameTextField.getText(), Integer.valueOf(stockTextField.getText()), scene));

            createItemPrompts.add(scene);
        }
        window.setScene(createItemPrompts.get(0));
    }

    private void addItem(String name, Integer stock, Scene scene) {
        LogicHandler.getInstance().addItem(name, stock);
        nextWindow(scene);
    }

    private Scene createItemPromptWindow(TextField nameTextField, TextField stockTextField,
                                         Button button) {

        Label nameLabel = new Label("Enter item name:");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label stockLabel = new Label("Enter initial stock count:");
        stockLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        VBox layout = new VBox(20);
        layout.getChildren().addAll(nameLabel, nameTextField, stockLabel, stockTextField, button);

        Scene scene = new Scene(layout, 300, 200);

        return scene;
    }

    private void nextWindow(Scene scene) {
        if (createItemPrompts.indexOf(scene) == createItemPrompts.size() - 1) {
            initMainWindow();
        } else window.setScene(createItemPrompts.get(createItemPrompts.indexOf(scene) + 1));
    }

    public static void initMainWindow() {
        Scene mainScene = new Scene(loadMainWindow(), 640, 320);
        window.setScene(mainScene);
    }

    private static GridPane loadMainWindow() {
        GridPane gridPane = new GridPane();

        LogicHandler.getInstance().addStockSizeToDisplay(gridPane);
        gridPane.add(new Label("Items:"), 0, 1);
        gridPane.add(new Label("Demand:"), 1, 1);
        gridPane.add(new Label("Stock:"), 2, 1);
        gridPane.add(new Label("Recommendation:"), 3, 1);

        LogicHandler.getInstance().displayItemData(gridPane, xPos, yPos);
        return gridPane;
    }
}