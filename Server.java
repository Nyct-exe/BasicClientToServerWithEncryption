import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.security.Signature;
import java.util.LinkedHashMap;

class Server {

    public static void main(String[] args) throws Exception {
        Integer port = Integer.parseInt(args[0]);
        LinkedHashMap <String, String> postHistory = new LinkedHashMap <>(); // Keeps Track of all posts
        ServerSocket ss = new ServerSocket(port);
        System.out.println("Waiting for incoming connections...");


        while (true) {
            Socket s = ss.accept();
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            if (!postHistory.isEmpty())
                for (String key : postHistory.keySet()) {
                    dos.writeUTF(postHistory.get(key));
                    dos.writeUTF(key);
                }

            String sender = "dummy";
            String date = "now";
            String itext;
            String msgText;
            try {
                while ((itext = dis.readUTF()) != null) {
                    sender = itext;
                    date = dis.readUTF();
                    msgText = dis.readUTF();
                    String msgOutput =
                            "**************************\n" +
                                    "Sender: " + sender + "\n" +
                                    "Date: " + date + "\n" +
                                    "Message: " + msgText +
                                    "\n**************************";

                    System.out.println(msgOutput);
                    // This message is stored in postHistory and will be sent to the next client,
                    // msgText is stored separately to make it easier to access it and decipher.
                    String messageToSend =
                            "**************************\n" +
                                    "Sender: " + sender + "\n" +
                                    "Date: " + date + "\n" +
                                    "Message: ";

                    ObjectInputStream readKey = new ObjectInputStream(new FileInputStream(sender + ".pub"));
                    PublicKey publicKey = (PublicKey) readKey.readObject();
                    readKey.close();
                    int lenght = dis.readInt();
                    if (lenght > 0) {
                        byte[] signature = new byte[lenght];
                        dis.readFully(signature, 0, signature.length);

                        Signature sig = Signature.getInstance("SHA1withRSA");
                        sig.initVerify(publicKey);
                        sig.update(sender.getBytes());
                        sig.update(date.getBytes());
                        sig.update(msgText.getBytes());
                        if(sig.verify(signature)){
                            System.out.println("Signature Verified");
                            postHistory.put(msgText, messageToSend);
                        } else {
                            System.out.println("Invalid Signature");
                        }

                    }
                }
            } catch (IOException e) {
                System.err.println("User Disconnected");
            }
        }
    }
}
