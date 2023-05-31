import java.io.*;
import java.net.*;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Server {
    public static void main(String[] args) {
        try {

            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("Laukiama gavimo");

            // patvirtinam kvietima
            Socket socket = serverSocket.accept();
            System.out.println("Patvirtinta");

            // Sukuriam objekta kuris perims duomenis
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // Perskaitomi gauti duomenys
            String receivedData = reader.readLine();
            String[] dataParts = receivedData.split("\\|");
            String receivedPublicKey = dataParts[0];
            String receivedMessage = dataParts[1];
            String receivedDigitalSignature = dataParts[2];

            System.out.println("Received Public Key: " + receivedPublicKey);
            System.out.println("Received Message: " + new String(Base64.getDecoder().decode(receivedMessage)));
            System.out.println("Received Digital Signature: " + receivedDigitalSignature);

            reader.close();
            inputStream.close();
            socket.close();
            serverSocket.close();

            // Issiunciam duomenis i 3 aplikacija
            sendToSignatureVerifier(receivedPublicKey, receivedMessage, receivedDigitalSignature);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendToSignatureVerifier(String publicKey, String message, String digitalSignature) {
        try {

            Socket socket = new Socket("localhost", 8081);
            System.out.println("Connected to SignatureVerifier application.");


            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);

            String dataToSend = publicKey + "|" + message + "|" + digitalSignature;

            writer.println(dataToSend);

            writer.close();
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
