package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class SignatureVerifier {
    public static void main(String[] args) {
        try {

            ServerSocket serverSocket = new ServerSocket(8081);
            System.out.println("Waiting for connection...");

            //Patvirtinam kvietima
            Socket socket = serverSocket.accept();
            System.out.println("Connection established.");

            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            // perskaitoma gauta informacija
            String receivedData = reader.readLine();
            String[] dataParts = receivedData.split("\\|");
            String receivedPublicKey = dataParts[0];
            String receivedMessage = dataParts[1];
            String receivedDigitalSignature = dataParts[2];
            System.out.println("Received Digital Signature: " + receivedDigitalSignature);

            // Patvirtinamas skaitmeninis parasas
            PublicKey publicKey = getPublicKeyFromString(receivedPublicKey);
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(Base64.getDecoder().decode(receivedMessage.getBytes()));
            boolean isSignatureValid = signature.verify(Base64.getDecoder().decode(receivedDigitalSignature));


            System.out.println("Digital Signature is " + (isSignatureValid ? "valid" : "invalid"));


            reader.close();
            inputStream.close();
            socket.close();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PublicKey getPublicKeyFromString(String publicKeyString) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(publicKeySpec);
    }
}
