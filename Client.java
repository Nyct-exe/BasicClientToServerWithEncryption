import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import java.io.*;
import java.net.Socket;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Scanner;

class Client {
    public static void main(String[] args) throws Exception {
        String host = args[0]; // posibily change to integer
        Integer port = Integer.parseInt(args[1]);
        String userid = args[2];
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        try {
            Socket s = new Socket(host, port);
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            // Reads The private Key
            ObjectInputStream obis = new ObjectInputStream(new FileInputStream(userid+".prv"));
            PrivateKey privateKey = (PrivateKey)obis.readObject();
            obis.close();

            // Read and Decrypt
            while(dis.available() > 0){
                System.out.print(dis.readUTF());
                String msgText = dis.readUTF();
                // Decode Base64
                Base64.Decoder decoder = Base64.getDecoder();
                byte[] msgEncrypted = decoder.decode(msgText);
                // Decrypt
                try{
                    cipher.init(Cipher.DECRYPT_MODE,privateKey);
                    String decipheredMsg = new String(cipher.doFinal(msgEncrypted),"UTF8");
                    System.out.print(decipheredMsg);
                } catch (BadPaddingException badPaddingException){
                    System.out.print(msgText);
                } catch (IllegalArgumentException illegalArgumentException){
                    System.out.print(msgText);
                }
                System.out.println("\n**************************");
            }

            // Asking user if they want to post a message
            System.out.println("Would you like to post a message (y/n)? ");
            Scanner sc = new Scanner(System.in);
            String userDecision = sc.next();
            if(userDecision.equals("y")  || userDecision.equals("yes")){

                // Taking User Input
                System.out.println("Enter the recipient userid (type \"all\" for posting without encryption):");
                String recipient = sc.next();
                System.out.println("Enter your message:");
                sc.nextLine();
                String userMessage = sc.nextLine();
                sc.close();

                // Getting the public key of the recipient
                ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(recipient+".pub"));
                PublicKey publicKey = (PublicKey)objectInputStream.readObject();
                objectInputStream.close();

                // Creating a message
                dos.writeUTF(userid);
                dos.writeUTF(String.valueOf(new java.util.Date()));

                // Encrypting the message
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                byte[] encryptText = cipher.doFinal(userMessage.getBytes("UTF8"));

                //BASE64 Coversion
                Base64.Encoder encoder = Base64.getEncoder();
                String encodedText = encoder.encodeToString(encryptText);
                dos.writeUTF(encodedText);


                // TODO: Send signature
            }
            else if(userDecision.equals("n") || userDecision.equals("no")){
                System.out.println("Connection Closed");
                s.close();
            } else {
                System.out.println("Unsupported action detected, Terminating connection");
                s.close();
            }

        } catch (Exception e) {
            System.err.println("Cannot connect to server.");
        }
        // TODO: Expection for missing public key

    }
}
