public class Lobby {
    private int id;
    private String code;
    private Game game;
    private LobbySettings settings;
    private PlayerUser hostPlayer;
    private PlayerUser oppPlayer;

    public Lobby(int id, PlayerUser hostPlayer, LobbySettings settings, String code) {

        this.id = id;
        this.code = code;
        this.hostPlayer = hostPlayer;
        this.settings = settings;

        this.oppPlayer = null;
        this.game = null;

        hostPlayer.setConnectedLobby(this);

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

    public void updateSettings(String setting, String value) {
        // updates Lobby settings
        // Call settings and apply some class function
        settings.updateSettings(setting, value);
    }

    public void addOpponent(PlayerUser opponent) {
        // adds opponent to the lobby

        // check to make sure player is null so connected player can't be overwritten
        if (oppPlayer == null) {
            oppPlayer = opponent;
            oppPlayer.setConnectedLobby(this);
        }
    }

    public void startGame() {
        // transitions from lobby into Game
        // Calls game and uses some function.


        if (hostPlayer != null && oppPlayer != null) {
            game = new Game(this, hostPlayer, oppPlayer);
        }
    }

    public void closeLobby(PlayerUser user) {
        // close lobby instance
        // Calls game and uses some function

        // check so only host can close lobby
        if (user != hostPlayer) {
            return;
        }

        //removes reference to this lobby by PlayerUser
        hostPlayer.setConnectedLobby(null);
        oppPlayer.setConnectedLobby(null);

        // clears the lobby state 
        oppPlayer = null;
        game = null;
    }
        
}