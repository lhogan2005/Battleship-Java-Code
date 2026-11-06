public class Lobby {
    private int id;
    private String code;
    private Game game;
    private LobbySettings settings;
    private PlayerUser hostPlayer;
    private PlayerUser oppPlayer;

    public lobby(PlayerUser hostPlayer) {

    }

    public int getId() {
        return id;
    }

    public String getCode() {
        // In game instance?
        return code;
    }

    public LobbySettings getSettings() {
        return settings;
    }

    public void updateSettings(String settings, String Value) {
        // updates Lobby settings
        // Call settings and apply some class function
    }

    public void addOpponent(playerUser opponent) {
        // adds opponent to the lobby
        oppPlayer = opponent;
    }

    public void startGame() {
        // transitions from lobby into Game
        // Calls game and uses some function.
    }

    public void closeLobby() {
        // close lobby instance
        // Calls game and uses some function
    }
        
}