public class PlayerUser {
    private int id; // unique id for user
    private String username; // username associated with user
    private boolean isGuest; // true if user is a guest
    private Lobby connectedLobby; // lobby that the user is currently connected to

    public PlayerUser(String Username, boolean isGuest) {
        // constructor for PlayerUser
        // creates a user with specified username and the value of isGuest
        // provided user has a valid name
        // connectedLobby will be initaily null
        this.id = id;
        this.username = username;
        this.isGuest = false;
        // Link to an instance of lobby.
        this.connectedLobby = Lobby();
    }

    public int getId() {
        // returns the unique id of PlayerUser
        return id;
    }

    public String getUsername() {
        //returns player username
        return username;
    }

    public boolean isGuest() {
        // returns whether the playerUser is a guest or not
        return isGuest;
    }

    public void connectToLobby(Lobby lobby) {
        // Connects user to specified Lobby
    }
}