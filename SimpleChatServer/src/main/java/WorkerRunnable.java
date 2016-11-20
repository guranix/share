import java.io.*;
import java.net.Socket;

/**
 * Created by guran on 9/15/16.
 */

public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    private BufferedReader lin;
    private PrintWriter lout;

    public WorkerRunnable(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {

        try {
            lin = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            lout = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);
            while (true){
                String line = lin.readLine();
                if (line.equals("Bue.")){break;}
                lout.println(line);
            }
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try{
//            System.out.println("Just connected to "
//            + clientSocket.getRemoteSocketAddress());
//            DataInputStream in =
//                    new DataInputStream(clientSocket.getInputStream());
//            String message = in.readUTF();
////            String message = channelRead();
//            System.out.println(message);
//            if(!message.equals("Blue.")) {
//                DataOutputStream out =
//                        new DataOutputStream(clientSocket.getOutputStream());
//                out.writeUTF(message);
//                out.close();
//            }else clientSocket.close();
//            in.close();
//        }catch (IOException io){
//            io.printStackTrace();
//        }
    }



//    private String channelRead() {
//        ByteBuffer buffer = ByteBuffer.allocate(256);
//        buffer.clear();
//        try {
//            clientSocket.getChannel().read(buffer);
//            if (buffer.hasRemaining()) {
//                buffer.flip();
//                byte[] bytes = new byte[buffer.limit()];
//                buffer.get(bytes, 0, bytes.length);
//                return new String(bytes, Charset.forName("UTF-8"));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}