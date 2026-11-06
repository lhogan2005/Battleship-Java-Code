public class PlayerData {
    private int id; // unique id for each Player
    private Grid selfGrid; // Player's own grid
    private Grid oppGrid; // Opponents Grid
    private List<Ship> ships; //Ships that belong to this Player
    private List<Ability> abilities; // Abilities for this Player
    private int points; // points vailable for Player

    public PlayerData(int id, Grid selfGrid, Grid oppGrid)  {
        //constructor for PlayerData
        //PlayerData is created with specific id and Grids
        // ships list is empty
        // abilities list is empty
        // shipCount is 0
        // points are 0
    }

    public int getId() {
        // return Player id
    }

    public int getShipCount() {
        //returns the number of Ships currently remaining
    }

    public void useAbility(Ability ability, Cell cell) {
        // Uses an selected Ability on specified cell
        // Player must have enough points to use selected ability
    }

    public int getPoints() {
        // returns the player's current points
    }

    public void gainPoints(int amount){
        // adds specified points to Player
    } 

    public void addShip(Ship ship){
        // adds ship object to ships list
        // increase shipCount
    }


}