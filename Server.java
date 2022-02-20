import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class Server {

    public static void main(String[] args) throws Exception {
        Integer port = Integer.parseInt(args[0]);
        LinkedHashMap<byte[],String> postHistory = new LinkedHashMap<>(); // Keeps Track of all posts
        ServerSocket ss = new ServerSocket(port);
        System.out.println("Waiting for incoming connections...");


        while(true){
            Socket s = ss.accept();
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            /*
            Connection Received Send post history
             */
            if(!postHistory.isEmpty())
                for(byte[] key: postHistory.keySet() ){
                    dos.writeUTF(postHistory.get(key));
                    dos.writeInt(key.length);
                    dos.write(key);

                }

            String sender = "dummy";
            String date = "now";
            String itext; // Incoming Text
            byte[] msgText;
            try {
                while (( itext = dis.readUTF()) != null) {
                    // The first message is always the userID
                        sender = itext;
                        date = dis.readUTF();
                        msgText = dis.readAllBytes();
                        // User Message
                        String msgOutput =
                                "**************************\n" +
                                "Sender: " + sender + "\n" +
                                "Date: " + date + "\n" +
                                "Message: " + msgText +
                                "\n**************************";

                        System.out.println(msgOutput);
//                        postHistory.add(msgOutput);
                    // This message is stored in postHistory and will be sent to the next client,
                    // msgText is stored separately to make it easier to access it and decipher.
                    String messageToSend =
                            "**************************\n" +
                            "Sender: " + sender + "\n" +
                            "Date: " + date + "\n" +
                            "Message: ";
                        postHistory.put(msgText,messageToSend);


                }
            }
            catch(IOException e) {
                System.err.println("User Disconnected");
            }
        }
    }
}
