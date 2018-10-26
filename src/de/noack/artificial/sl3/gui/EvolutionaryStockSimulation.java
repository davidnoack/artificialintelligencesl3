package de.noack.artificial.sl3.gui;

import de.noack.artificial.sl3.logic.LogicHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Die Oberfläche EvolutionaryStockSimulation erlaubt die Eingabe einer Lagergröße
 * und initialisiert daraufhin die Simulation der Aufgabe
 */
public class EvolutionaryStockSimulation extends Application {

    // Aktuelles Fenster
    static Stage window;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Referenz zur Stage
        window = primaryStage;
        window.setTitle("Stock simulation Pt.2");
        // Erstellung eines Eingabefensters für die Lagergröße
        createStockSizePromptWindow();

        window.setOnCloseRequest(t -> {
            Platform.exit();
            System.exit(0);
        });
        window.setResizable(false);
        window.setFullScreen(false);
        window.show();
    }

    /**
     * Die Methode createStockSizePromptWindow erstellt ein Fenster mit Label
     * zur Eingabeaufforderung und einem Textfeld, welches Zahlenwerte zur Eingabe
     * erlaubt. Bei Bestätigung wird der Markt initialisiert und das Hauptfenster
     * geladen
     */
    private void createStockSizePromptWindow() {

        Label label = createLabel("Enter maximum stock size (G):");

        TextField textField = createNumberTextField();
        Button button = new Button("Ok");
        button.setOnAction(e -> initializeMarket(Integer.valueOf(textField.getText())));

        VBox layout = new VBox(20);
        layout.getChildren().addAll(label, textField, button);

        window.setScene(new Scene(layout, 260, 100));
    }

    /**
     * Erstellt anhand der Lagergröße die in der Aufgabenstellung angefragten Waren mit ihrer
     * zur Lagergröße relativen Größe
     *
     * @param stockSize
     */
    private void initializeMarket(Integer stockSize) {
        // Erstellung des Markts mit Lager
        LogicHandler.getInstance().initializeMarket(stockSize);

        // Erstellung der Waren
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

        // Initialisierung des Hauptfensters
        initMainWindow();
    }

    /**
     * Initialisieren des Hauptfensters
     */
    public static void initMainWindow() {
        Scene mainScene = new Scene(loadMainWindow(), 1600, 320);
        window.setScene(mainScene);
    }

    /**
     * Lädt das Hauptfenster und liest die Warendaten aus
     *
     * @return
     */
    private static GridPane loadMainWindow() {
        GridPane gridPane = new GridPane();

        LogicHandler.getInstance().addHeaderToDisplay(gridPane);
        int column = 0;
        gridPane.add(new Label("Items:"), column++, 2);
        gridPane.add(new Label("|"), column++, 2);
        gridPane.add(new Label("Size:"), column++, 2);
        gridPane.add(new Label("|"), column++, 2);
        gridPane.add(new Label("Delivery time:"), column++, 2);
        gridPane.add(new Label("|"), column++, 2);
        gridPane.add(new Label("Units in Stock:"), column++, 2);
        gridPane.add(new Label("|"), column++, 2);
        gridPane.add(new Label("Threshold:"), column++, 2);
        gridPane.add(new Label("|"), column++, 2);
        gridPane.add(new Label("Recommendation:"), column++, 2);

        LogicHandler.getInstance().displayItemData(gridPane, 0, 3);
        return gridPane;
    }

    /**
     * Erstellt ein Label mit Arial Bold 14
     *
     * @param labelText
     * @return befülltes und formatiertes Label
     */
    private Label createLabel(String labelText) {
        Label label = new Label(labelText);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        return label;
    }

    /**
     * Erstellt ein Textfeld, bei welchem die nicht numerischen Ziffern
     * herausgefiltert werden.
     *
     * @return numerisches Textfeld
     */
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