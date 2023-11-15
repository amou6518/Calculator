package calculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 12345;

    public static void main(String[] args) {
        // Create a thread pool to handle multiple clients concurrently
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            // Accept incoming client connections indefinitely
            while (true) {
                // Accept a client connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted connection from " + clientSocket.getInetAddress());

                // Submit the client handling task to the thread pool
                executorService.submit(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Shut down the thread pool when the server is done
            executorService.shutdown();
        }
    }
}

// Runnable task to handle a connected client
class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            // Read the arithmetic expression from the client
            String expression = reader.readLine();
            System.out.println("Received expression from client: " + expression);

            // Parse and calculate the expression, then send the result or an error message back to the client
            try {
                double result = calculateExpression(expression);
                writer.println("Result: " + result);
            } catch (ArithmeticException ex) {
                writer.println("Error: " + ex.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Helper method to parse and calculate the arithmetic expression
    private double calculateExpression(String expression) {
        // Implement expression parsing and calculation logic here
 
        String[] tokens = expression.split(" ");
        double operand1 = Double.parseDouble(tokens[0]);
        String operator = tokens[1];
        double operand2 = Double.parseDouble(tokens[2]);

        switch (operator) {
            case "+":
                return operand1 + operand2;
            case "-":
                return operand1 - operand2;
            case "*":
                return operand1 * operand2;
            case "/":
                if (operand2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return operand1 / operand2;
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
}
