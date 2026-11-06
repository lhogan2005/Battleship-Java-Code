public class Networking {
    private List<PlayerUser> connectedPlayers; // Players currently connected

    public Networking() {
        // Constructor for Networking
        // connectedPlayers is initially empty
    }

    public void connectPlayer(PlayerUser player){
        // Connects specified player to Network
        // Provided player is Valid and not already connected
    }

    public void disconnectPlayer(PlayerUser player) {
        // disconnects specified player from the network
        //provided player is valid and is connected
    }

    public void sendPacket(PlayerUser player, String packet) {
        // sends a packet from backend to the specified 
    }

    public receivePacket(PlayerUser player, String message) {
        // Recieves message for specified user
        //determines what is required from request
        // routes request logic to appropriate system
    }
}