import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class Server {

    public static void main(String[] args) throws Exception {
        Integer port = Integer.parseInt(args[0]);

        ServerSocket ss = new ServerSocket(port);
        System.out.println("Waiting for incoming connections...");

        while(true){
            Socket s = ss.accept();
            DataInputStream dis = new DataInputStream(s.getInputStream());
            boolean firstMessage = true;
            String sender = "dummy";
            ArrayList postHistory = new ArrayList<String>(); // Keeps Track of all posts
            String itext; // Incoming Text
            try {
                while (( itext = dis.readUTF()) != null) {
                    // The first message is always the userID
                    if(firstMessage == true){
                        sender = itext;
                        firstMessage = false;
                    } else {
                        // User Message
                        String msg = "Sender: " + sender + "\n" +
                                "Date: " + new java.util.Date() + "\n" +
                                "Message: " + itext;

                        System.out.println(msg);
                        postHistory.add(msg);
                    }

                }
            }
            catch(IOException e) {
                System.err.println("Client closed its connection.");
            }
        }
    }
}
