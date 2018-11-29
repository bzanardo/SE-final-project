import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

public class ScrumToolServer {

    /**
     * The port that the server listens on.
     */
    private static final int PORT = 9001;

    private static HashMap<Integer, PrintWriter> userWritersMap = new HashMap<Integer, PrintWriter>();
    private static int userCount = 0;
    
    /**
     * The appplication main method, which just listens on a port and
     * spawns handler threads.
     */
    public static void main(String[] args) throws Exception {
        System.out.println("The scrum tool server is running.");
        ServerSocket listener = new ServerSocket(PORT);
        try {
            while (true) {
                new Handler(listener.accept(), ++userCount).start();
            }
        } finally {
            listener.close();
        }
    }

    /**
     * A handler thread class.  Handlers are spawned from the listening
     * loop and are responsible for a dealing with a single client
     * and broadcasting its messages.
     */
    private static class Handler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        
        private int uid;

        public Handler(Socket socket, int uid) {
            this.socket = socket;
            this.uid = uid;
            System.out.println("New server handler created");
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                
                synchronized(userWritersMap){
                	userWritersMap.put(this.uid, out);
                }
                	
                // Accept messages from this client and broadcast them.
                // Ignore other clients that cannot be broadcasted to.
                while (true) {
                    String input = in.readLine();
                    if (input == null) {
                        return;
                    }
                    
                    synchronized(userWritersMap){
	                    for (Integer uid : userWritersMap.keySet()) {
	                    	if(uid != this.uid){
	                    		userWritersMap.get(uid).println(input); // broadcast message exactly as it was sent
	                    	}
	                    }
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            } finally {
            	System.out.println("Try to close socket");
                if (out != null) {
                    userWritersMap.remove(this.uid);
                }
                try {
                    socket.close();
                    System.out.println("Closing user"+this.uid+" socket");
                } catch (IOException e) {
                }
            }
        }
    }
}