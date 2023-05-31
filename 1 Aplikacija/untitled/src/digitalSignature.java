import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.*;
import java.util.Base64;

public class digitalSignature {
    public static void main(String[] args) {
        try {

            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048); // rakto didys 2048 bitai
            KeyPair keyPair = keyGen.generateKeyPair(); // sugeneruojami public ir private raktai
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();


            String message = "Sveiki visi";
            System.out.println("Tekstas: " + message);

            // Sugeneruojamas skaitmeninis parasas
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(message.getBytes()); // atnaujina paraso pranesimo baitus
            byte[] digitalSignature = signature.sign(); // sugeneruojamas skaitmeninis raktas

            // Paverciam i Stringus
            String publicKeyString = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            String messageString = Base64.getEncoder().encodeToString(message.getBytes());
            String digitalSignatureString = Base64.getEncoder().encodeToString(digitalSignature);

            // Issiunciam duomenis
            sendToAnotherApplication(publicKeyString, messageString, digitalSignatureString);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendToAnotherApplication(String publicKey, String message, String digitalSignature) {
        try {

            Socket socket = new Socket("localhost", 8080);

            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream);

            // issiunciami duomenis kaip vienas didelis String
            String data = publicKey + "|" + message + "|" + digitalSignature;
            writer.println(data);
            writer.flush();

            writer.close();
            outputStream.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
