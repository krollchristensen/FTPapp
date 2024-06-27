import java.io.*;
import java.net.*;

public class SimpleFTPServer {
    private static final int PORT = 5000; // Server port
    private static final String SERVER_DIRECTORY = "C:\\Temp\\Java\\Srv"; // Directory for files

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            // Ensure the server directory exists
            File dir = new File(SERVER_DIRECTORY);
            if (!dir.exists()) {
                dir.mkdir();
            }

            while (true) {
                // Accept a new client connection
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                // Handle the client in a new thread
                new ClientHandler(socket).start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

class ClientHandler extends Thread {
    private Socket socket;
    private static final String SERVER_DIRECTORY = "C:\\Temp\\Java\\Srv";

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (InputStream input = socket.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(input));
             OutputStream output = socket.getOutputStream();
             PrintWriter writer = new PrintWriter(output, true)) {

            // Read the requested file name from the client
            String fileName = reader.readLine();
            System.out.println("Client requested: " + fileName);

            // Send the file to the client
            sendFile(fileName, output);
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void sendFile(String fileName, OutputStream outputStream) throws IOException {
        File file = new File(SERVER_DIRECTORY, fileName);
        if (file.exists()) {
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                byte[] buffer = new byte[1024];
                int bytesRead;

                // Read the file and send it to the client in chunks
                while ((bytesRead = bis.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
                System.out.println("File sent to client: " + fileName);
            }
        } else {
            // Send an error message if the file does not exist
            PrintWriter writer = new PrintWriter(outputStream, true);
            writer.println("FILE NOT FOUND");
            System.out.println("File not found: " + fileName);
        }
    }
}
