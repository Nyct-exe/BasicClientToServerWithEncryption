import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class Server {

    public static void main(String[] args) throws Exception {
        Integer port = Integer.parseInt(args[0]);
        ArrayList<String> postHistory = new ArrayList<String>(); // Keeps Track of all posts
        ServerSocket ss = new ServerSocket(port);
        System.out.println("Waiting for incoming connections...");


        while(true){
            Socket s = ss.accept();
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());

            /*
            Connection Received Send all appropriate data
             */
            if(!postHistory.isEmpty())
                for(String i: postHistory ){
                    dos.writeUTF(i);
                }

            //TODO: Decrypt messages



            boolean firstMessage = true;
            String sender = "dummy";
            String itext; // Incoming Text
            try {
                while (( itext = dis.readUTF()) != null) {
                    // The first message is always the userID
                    if(firstMessage == true){
                        sender = itext;
                        firstMessage = false;
                    } else {
                        // User Message
                        String msg =
                                "**************************\n" +
                                "Sender: " + sender + "\n" +
                                "Date: " + new java.util.Date() + "\n" +
                                "Message: " + itext +
                                "\n**************************";

                        System.out.println(msg);
                        postHistory.add(msg);
                    }

                }
            }
            catch(IOException e) {
                System.err.println("User Disconnected");
            }
        }
    }
}
