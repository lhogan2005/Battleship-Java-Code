public class Game {
    private Lobby lobby;
    private PlayerData player1;
    private PlayerData player2;
    private Grid grid1;
    private Grid grid2;
    private int turn;
    private GameState state;

    public Game(Lobby lobby, PlayerUser player1, PlayerUser player2) {
        // Constructor for Game
        // provided lobby, player1, player2 are valid
        // Lobby haas valid gameSettings

        // two grids are created using LobbySetting
        // two PlayerData objects are created
        // Each player is assigned a grid
        // GameState set to Place to start
        // Initial Turn assigned
    }

    public void confirmPlacement(PlayerData player) {
        //
    }

    public void endTurn(PlayerData player) {
        // ends current player turn
        // provided player is current player
    }

    public void nextTurn() {
        // internally switch turn
    }

    public void endGame(PlayerData loser) {
        // ends game when a player has lost all ships
    }

}

