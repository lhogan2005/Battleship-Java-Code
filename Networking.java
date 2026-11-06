// Deals with data streams.
// Information given out and given in is done via io.
import java.io.*;
import java.net.*;
import java.util.*;

public class Networking {

    // Current implentation. To be devided and fixed.
    private int port;
    private ServerSocket serverSocket;
    // Old implementation.
    private List<PlayerUser> connectedPlayers; // Players currently connected

    public Networking(int port) throws IOException {
        this.port = port;
        this.serverSocket = new ServerSocket(port);
        // Constructor for Networking
        // connectedPlayers is initially empty
    }

    public void start() throws IOException {
        System.out.println("Peer started on port " + port);
        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    public void connectToPeer(String host, int port) {
        try (Socket socket = new Socket(host, port);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("Hello from Peer on port " + this.port);
            String response = in.readLine();
            System.out.println("Received from another Peer: " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Testing main class.
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Peer <port>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        try {
            Networking peer = new Networking(port);

            if (args.length == 3) {
                peer.connectToPeer(args[1], Integer.parseInt(args[2]));
            }
            peer.start();
            } catch (IOException e) {
            e.printStackTrace();
            }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            String message;
            while ((message = in.readLine()) != null) {
                // This will be where we send and recieve our packets, and set what our packet is
                System.out.println("Received: " + message);
                out.println(message); //echo back
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
            }
        }
    }
}

//     public void connectPlayer(PlayerUser player){
//         // Connects specified player to Network
//         // Provided player is Valid and not already connected
//     }

//     public void disconnectPlayer(PlayerUser player) {
//         // disconnects specified player from the network
//         //provided player is valid and is connected
//     }

//     public void sendPacket(PlayerUser player, String packet) {
//         // sends a packet from backend to the specified 
//     }


//     // Change return type.
//     public void receivePacket(PlayerUser player, String message) {
//         // Recieves message for specified user
//         //determines what is required from request
//         // routes request logic to appropriate system
//     }
// }