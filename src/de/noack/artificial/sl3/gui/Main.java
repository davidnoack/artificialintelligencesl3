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
        window.setTitle("Stock simulation Pt.2");
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
            TextField stockTextField = createNumberTextField();
            TextField deliveryTimeTextField = createNumberTextField();

            Button button = new Button("Submit");

            Scene scene = createItemPromptWindow(nameTextField, stockTextField, deliveryTimeTextField, button);

            button.setOnAction(e -> addItem(nameTextField.getText(), Integer.valueOf(stockTextField.getText()), Integer.valueOf(deliveryTimeTextField.getText()), scene));

            createItemPrompts.add(scene);
        }
        window.setScene(createItemPrompts.get(0));
    }

    private void addItem(String name, Integer stock, Integer deliveryTime, Scene scene) {
        LogicHandler.getInstance().addItem(name, stock, deliveryTime);
        nextWindow(scene);
    }

    private Scene createItemPromptWindow(TextField nameTextField, TextField stockTextField, TextField deliveryTimeTextField,
                                         Button button) {

        Label nameLabel = createLabel("Enter item name:");
        Label stockLabel = createLabel("Enter initial stock count:");
        Label deliveryTimeLabel = createLabel("Enter delivery time:");

        VBox layout = new VBox(20);
        layout.getChildren().addAll(nameLabel, nameTextField, stockLabel, stockTextField, deliveryTimeLabel, deliveryTimeTextField, button);

        Scene scene = new Scene(layout, 300, 270);

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
        gridPane.add(new Label("Deliverytime:"), 2, 1);
        gridPane.add(new Label("Stock:"), 3, 1);
        gridPane.add(new Label("Recommendation:"), 4, 1);

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