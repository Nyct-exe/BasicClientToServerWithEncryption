import java.io.DataOutputStream;
import java.net.Socket;

class Client {
    public static void main(String[] args) throws Exception {
        String host = args[0]; // posibily change to integer
        Integer port = Integer.parseInt(args[1]);
        String userid = args[2];

        try {
            Socket s = new Socket(host, port);
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            // Creating a message

            dos.writeUTF(userid);
            /*
            Encrypt here
             */
            dos.writeUTF("Happy me is");

        } catch (Exception e) {
            System.err.println("Cannot connect to server.");
        }

    }
}
