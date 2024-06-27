import java.io.*;
import java.net.*;

public class SimpleFTPClient {
    private static final String SERVER_ADDRESS = "localhost"; // Server address
    private static final int SERVER_PORT = 5000; // Server port
    private static final String CLIENT_DIRECTORY = "C:\\Temp\\Java\\klient"; // Directory to save files

    public static void main(String[] args) {
        // Ensure the client directory exists
        File dir = new File(CLIENT_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdir();
        }

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
             BufferedReader serverReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter serverWriter = new PrintWriter(socket.getOutputStream(), true)) {

            // Ask the user for the file name to download
            System.out.print("Enter the file name to download: ");
            String fileName = consoleReader.readLine();
            serverWriter.println(fileName); // Send file name to server

            // Download the file from the server
            downloadFile(fileName, socket.getInputStream());
        } catch (IOException ex) {
            System.out.println("Client exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void downloadFile(String fileName, InputStream inputStream) throws IOException {
        File file = new File(CLIENT_DIRECTORY, fileName);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            // Read the file from the input stream and save it to the client directory
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.flush();
            System.out.println("File downloaded: " + fileName);
        }
    }
}
