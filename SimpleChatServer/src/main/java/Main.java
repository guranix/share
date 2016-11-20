/**
 * Created by guran on 9/15/16.
 */
public class Main {

    public static void main(String[] args) {

        ThreadPooledServer server = new ThreadPooledServer(5050);
        new Thread(server).start();

    }
}