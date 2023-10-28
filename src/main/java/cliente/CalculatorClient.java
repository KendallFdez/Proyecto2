package cliente;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Clase Principal para el cliente  se encarga de crear la aplicación de javafx y crear una conexion con el servidor
 * para inicializar envio y recepcion de datos
 */
public class CalculatorClient extends Application {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private TextField expressionField;
    private Label resultLabel;

    /**
     * Inizializador del cliente conecta con el servidor usando el host y el port
     */
    public CalculatorClient() {
        try {
            // Connect to the server
            socket = new Socket("localhost", 1234);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Se encarga de crear la UI,asigna la funcion de evaluar expresion al boton
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        // Create the expression input field
        expressionField = new TextField();
        expressionField.setPromptText("Enter an expression");

        // Create the evaluate button
        Button evaluateButton = new Button("Evaluate");
        evaluateButton.setOnAction(e -> evaluateExpression());

        // Create the result label
        resultLabel = new Label();

        // Create the layout
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(expressionField, evaluateButton, resultLabel);

        // Create the scene
        Scene scene = new Scene(layout, 300, 200);

        // Set the scene on the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle("Calculator");
        primaryStage.show();
    }

    /**
     * Funcion encargada de recoger la expresion y enviarla al servidor,una vez evaluada muestra el resultado
     */
    private void evaluateExpression() {
        String expression = expressionField.getText();

        // Replace ** with Math.pow
        expression = expression.replace("**", "Math.pow");

        out.println(expression);

        try {
            String result = in.readLine();
            resultLabel.setText(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cierra la conexion con el servidor
     */
    @Override
    public void stop() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Encargada de hacer la clase ejecutable
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
