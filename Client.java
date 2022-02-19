import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

class Client {
    public static void main(String[] args) throws Exception {
        String host = args[0]; // posibily change to integer
        Integer port = Integer.parseInt(args[1]);
        String userid = args[2];

        try {
            Socket s = new Socket(host, port);
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
            DataInputStream dis = new DataInputStream(s.getInputStream());
            while(dis.available() > 0){
                System.out.println(dis.readUTF());
                System.out.println("**************************");
            }

            // Asking user if they want to post a message
            System.out.println("Would you like to post a message (y/n)? ");
            Scanner sc = new Scanner(System.in);
            String userText = sc.next();
            sc.close();
            if(userText.equals("y")  || userText.equals("yes")){

                // Creating a message
                dos.writeUTF(userid);
                /*
                Encrypt here
                 */
                dos.writeUTF("Happy me is");
            }
            else if(userText.equals("n") || userText.equals("no")){
                System.out.println("Connection Closed");
                s.close();
            } else {
                System.out.println("Unsupported action detected, Terminating connection");
                s.close();
            }

        } catch (Exception e) {
            System.err.println("Cannot connect to server.");
        }

    }
}
