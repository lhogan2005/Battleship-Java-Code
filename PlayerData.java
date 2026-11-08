import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    private int id; // unique id for each Player
    private Grid selfGrid; // Player's own grid
    private Grid oppGrid; // Opponents Grid
    private List<Ship> ships; //Ships that belong to this Player
    private List<Ability> abilities; // Abilities for this Player
    private int points; // points vailable for Player
    private boolean ready;

    public PlayerData(int id, Grid selfGrid, Grid oppGrid)  {
        //constructor for PlayerData
        //PlayerData is created with specific id and Grids
        // ships list is empty
        // abilities list is empty
        // shipCount is 0
        // points are 0

        this.id = id;
        // This is the grid for placing ships 
        this.selfGrid = selfGrid;

        // This is the grid the player selects to shoot an opponent.
        this.oppGrid = oppGrid;

        this.ships = new ArrayList<>();
        this.abilities = new ArrayList<>();
        this.points = 0;
        this.ready = false;
    }

    public int getId() {
        // return Player id
        return this.id;
    }

    public int getShipCount() {
        //returns the number of Ships currently remaining
        
        int shipCount = 0;

        for (Ship ship: ships) {
            if (!ship.getSunk()) {
                shipCount ++;
            }
        }

        return shipCount;
    }

    public boolean useAbility(Ability ability, Cell cell) {
        // Uses an selected Ability on specified cell
        // Player must have enough points to use selected ability

        // Call the ability class that we select. Pass in the cell.
        if (points < ability.getCost()) {
            return;
        }
        points -= ability.getCost();
        ability.use(cell, oppGrid);

    }

    public int getPoints() {
        // returns the player's current points
        return points;
    }

    public void gainPoints(int amount){
        // adds specified points to Player

        points += amount;
    } 

    public void addShip(Ship ship){
        // adds ship object to ships list
        // increase shipCount

        ships.add(ship);
    }

    public void setReady() {
        this.ready = true;
    }

    public boolean isReady() {
        return ready;
    }

    public void addAbility(Ability ability) {
        abilities.add(ability);
    }

    public Grid getSelfGrid() {
        return selfGrid;
    }

    public Ability getAbility(AbilityType type) {
        for (Ability ability : abilities) {
            if (type == AbilityType.SHOT && ability instanceof Shot){
                return ability;
            }
            else if (type == AbilityType.BOMB && ability instanceof Bomb){
                return ability;
            }
            else if (type == AbilityType.RADAR && ability instanceof Radar) {
                return ability;
            }
            else if (type == AbilityType.MISSLE && ability instanceof Missile) {
                return ability;
            }

            return null;
        }
    }

}