import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import java.io.*;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;
import java.util.Scanner;

class Client {
    public static void main(String[] args) throws Exception {
        String host = args[0];
        Integer port = Integer.parseInt(args[1]);
        String userid = args[2];
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        try {
            Socket s = new Socket(host, port);
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            ObjectInputStream obis = new ObjectInputStream(new FileInputStream(userid+".prv"));
            PrivateKey privateKey = (PrivateKey)obis.readObject();
            obis.close();

            //Reads server post history and tries to decode and decrypt the messages

            while(dis.available() > 0){
                System.out.print(dis.readUTF());
                String msgText = dis.readUTF();
                try{
                    Base64.Decoder decoder = Base64.getDecoder();
                    byte[] msgEncrypted = decoder.decode(msgText);

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

            System.out.println("Would you like to post a message (y/n)? ");
            Scanner sc = new Scanner(System.in);
            String userDecision = sc.next();
            if(userDecision.equals("y")  || userDecision.equals("yes")){

                String date = String.valueOf(new java.util.Date());

                System.out.println("Enter the recipient userid (type \"all\" for posting without encryption):");
                String recipient = sc.next();
                System.out.println("Enter your message:");
                sc.nextLine();
                String userMessage = sc.nextLine();
                sc.close();

                if(!recipient.equalsIgnoreCase("all")){
                    ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(recipient+".pub"));
                    PublicKey publicKey = (PublicKey)objectInputStream.readObject();
                    objectInputStream.close();

                    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                    byte[] encryptText = cipher.doFinal(userMessage.getBytes("UTF8"));

                    Base64.Encoder encoder = Base64.getEncoder();
                    userMessage = encoder.encodeToString(encryptText);


                }

                dos.writeUTF(userid);
                dos.writeUTF(date);
                dos.writeUTF(userMessage);

                Signature signature = Signature.getInstance("SHA1withRSA");
                signature.initSign(privateKey);
                signature.update(userid.getBytes());
                signature.update(date.getBytes());
                signature.update(userMessage.getBytes());
                byte[] signedSignature = signature.sign();

                dos.writeInt(signedSignature.length);
                dos.write(signedSignature);


            }
            else if(userDecision.equals("n") || userDecision.equals("no")){
                System.out.println("Connection Closed");
                s.close();
            } else {
                System.out.println("Unsupported action detected, Terminating connection");
                s.close();
            }

            dis.close();
            dos.close();

        }catch (FileNotFoundException fileNotFoundException){
          System.err.println("Missing Keys");
        } catch (Exception e) {
            System.err.println("Cannot connect to server.");
        }

    }
}
