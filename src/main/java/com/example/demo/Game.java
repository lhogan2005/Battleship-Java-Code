package com.example.demo;

public class Game {
    private Lobby lobby;
    private PlayerData player1;
    private PlayerData player2;
    private Grid grid1;
    private Grid grid2;
    private int turn;
    private GameState state;

    private PlayerData winner;

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
    
        // Change this to be correct type for grid size.
        int size = lobby.getSettings().getGridSize();

        this.grid1 = new Grid(size);
        this.grid2 = new Grid(size);

        // Trying to pass in PlayerUser, might need to change to create a new instance of PlayerData for player 1 and 2.
        // Not sure how to create instances of player 1 and 2.
        this.player1 = new PlayerData(1, grid1, grid2);
        this.player2 = new PlayerData(2, grid2, grid1);

        
        // If turn is even its player 1 if odd its player 2
        this.turn = 0;

        this.state = GameState.PLACE;


        for (AbilityType ability : lobby.getSettings().getAbilityTypes()) {
            this.player1.addAbility(convertAbility(ability));
            this.player2.addAbility(convertAbility(ability));
        }
    }


    public PlayerData getPlayer1() {
        return player1;
    }

    public PlayerData getPlayer2() {
        return player2;
    }


    public PlayerData winner() {
        return winner;
    }

    public GameState getState() {
        return state;
    }

    private PlayerData getOpponentPlayer(PlayerData player) {
        if(player == player1) {
            return player2;
        }
        else {
            return player1;
        }
    }



        //Allow ships to actually be placed in game instance
    public void placeShip(PlayerData player, ShipType type, int row, int column, boolean orientation){
        
        if (state == GameState.PLACE && lobby.getSettings().getShipTypes().contains(type)) {
            Ship ship = player.getSelfGrid().placeShip(type, row, column, orientation);
            
            if (ship != null) {
                player.addShip(ship);
            }
        }
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


    public PlayerData getPlayerForCurrentTurn() {

        if (turn % 2 == 0) {
            return player1;
        }
        else {
            return player2;
        }


    }

    public void nextTurn() {
        // internally switch turn
        turn++;
    }

    public void endGame(PlayerData loser) {
        // ends game when a player has lost all ships
        // Checks to make sure that the ship count is actually 0.
        if (loser.getShipCount() == 0) {
            winner = getOpponentPlayer(loser);
            state = GameState.END;
        }
    }

    //function to convert AbilityType Enum to useable ability objects.
    private Ability convertAbility(AbilityType type) {

        if (type == AbilityType.SHOT) {
            return new Shot();
        }
        else if (type == AbilityType.BOMB) {
            return new Bomb();
        }
        else if (type == AbilityType.RADAR) {
            return new Radar();
        }
        else if (type == AbilityType.MISSILE) {
            return new Missile();
        }

        else {
            return null;
        }
    }

    public boolean useAbility(PlayerData player, AbilityType type, int row, int column) {
        if (state != GameState.BATTLE) {
            return false;
        }

        if (player != getPlayerForCurrentTurn()){
            return false;
        }

        Ability ability = player.getAbility(type);

        if (ability == null) {
            return false;
        }

        PlayerData opp = getOpponentPlayer(player);
         Grid oppGrid = opp.getSelfGrid();

        Cell target = oppGrid.getCell(row, column);
        
        if (player.getPoints() < ability.getCost()) {
            return false;
        }
        player.useAbility(ability, target);

        player.gainPoints(1);

        if (opp.getShipCount() == 0) {
            endGame(opp);
        }
        else {
            nextTurn();
        }

        return true;
    }

}