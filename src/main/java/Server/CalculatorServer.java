package Server;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import java.io.*;
import java.net.*;

/**
 * Clase principal encargda de conectar con el servidor y recibir la informacion de la expresion
 */
public class CalculatorServer {
    private static final int PORT = 1234;

    /**
     * Conecta con el client usando el port y crear una clienthandler para cada cliente, ademas crea un ciclo de recepción
     * @param args
     */
    public static void main(String[] args) {
        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server started on port " + PORT);

            while (true) {
                // Wait for a client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Create a new client handler thread
                Thread clientThread = new Thread(new ClientHandler(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

/**
 *Inizializador de conexion con cada cliente
 */
class ClientHandler implements Runnable {
    private Socket clientSocket;

    /**
     * Constructor
     * @param clientSocket la conexion con el servidor
     */
    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * Se encarga de recibir constantemente expresiones de los clientes y evaluarlos para enviarlos de vuelta nuevamente
     */
    @Override
    public void run() {
        try {
            // Get the input and output streams for the client socket
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Read the expression from the client
            String expression = in.readLine();

            try {
                // Create a new Rhino context
                Context rhinoContext = Context.enter();

                // Create a new Rhino scope
                Scriptable scope = rhinoContext.initStandardObjects();

                // Evaluate the expression
                Object result = rhinoContext.evaluateString(scope, expression, "CalculatorServer", 1, null);

                // Send the result back to the client
                out.println("Result: " + result);
            } catch (Exception e) {
                // If there's an error in the expression, send an error message back to the client
                out.println("Error: " + e.getMessage());
            }

            // Close the client socket
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
