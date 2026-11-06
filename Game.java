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

        // Use if cases to check for valid lobby, player1 and player2
        // 2 player instances. Use given players
        // Two grid instaces. Create new grids.
        // Trun either a int 1 or 0 to show player 1 or 2. Or if we wish to keep track of player turn count set it to an increasing tick.

        this.lobby = lobby;
        
        // Trying to pass in PlayerUser, might need to change to create a new instance of PlayerData for player 1 and 2.
        this.player1 = PlayerUser player1;
        this.player2 = PlayerUser player2;
        this.grid1 = new Grid grid1;
        this.grid2 = new Grid grid2;
        
        // If turn is even its player 1 if odd its player 2
        this.turn = 0;
    }

    public void confirmPlacement(PlayerData player) {
        // This should take in a player. Call place ships until no ships left to place (use for loop).
        
        // Should trigger a is ready.
        player.setReady();

        if (player1.isReady() && player2.isReady()) {
            state = GameState.BATTLE;
        }
    }

    // public void endTurn(PlayerData player) {
    //     // ends current player turn
    //     // provided player is current player


    // }

    public void nextTurn() {
        // internally switch turn
        turn++;
    }

    public void endGame(PlayerData loser) {
        // ends game when a player has lost all ships
        // Checks to make sure that the ship count is actually 0.
        if (loser.getShipCount() == 0) {
            state = GameState.END;
        }
    }

}

