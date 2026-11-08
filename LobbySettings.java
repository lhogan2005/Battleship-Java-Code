import java.util.List;

public class LobbySettings {
    private int gridSize;

    private List<ShipType> shipTypes; // Shiptypes included in the Game

    private List<AbilityType> abilityTypes; // Abilities available in the Game

    public LobbySettings(int gridSize, List<ShipType> shipTypes, List<AbilityType> abilityTypes) {
        // constructor for LobbySettings
        // Created with default settings

        this.gridSize = gridSize;
        this.shipTypes = shipTypes;
        this.abilityTypes = abilityTypes;
    }

    public void updateSettings(String setting, String value) {
        // updates specified Lobby setting
        // provided setting and value are valid

        if (setting.toLowerCase().equals("gridsize")) {
            updateGridSize(value);
        }
        else if (setting.toLowerCase().equals("shiptype")) {
            updateShipTypes(value);
        }
        else if (setting.toLowerCase().equals("abilities")) {
            updateAbilities(value);
        }
    }

    private void updateGridSize(String value){
        // updates grid to specified size
        // provided value is valid

        // convert string to int
            int valueInt = Integer.parseInt(value);
        
        // grid smaller than 5 wouldn't allow all ships to be placed
        if (valueInt >= 5) {
            this.gridSize = valueInt;
        }
    }

    private void updateShipTypes(String value) {
        // updates the shipType available in the Game
        //provided value is valid
        // We are making the assumption that uupdates come as "Carrier,Submarine,Destroyer"

        // split list by comma
        String[] types = value.split(",");

        for (String type : types) {
            //converts String to ShipType enum
            ShipType shipType = shipType.valueOf(type.toUpperCase());

            // add enabled shiptypes
            shipTypes.add(shipType);
        }

    }

    private void updateAbilities(String value) {
        //updates the Abilities available in the Game
        //provided value is a is one or more valid abilities
        // We are making the assumption that updates come as "Shot,Bomb,Radar"

        String[] abilityNames = value.split(",");
        for (String abilityName : abilityNames) {
 
            //Converts String to ShipType enum
            AbilityType abilityTypes = AbilityType.valueOf(abilityName.toUpperCase());
            // add enabled abilities
            abilityTypes.add(abilityType);
        }
    }

    public int getGridSize() {
        return gridSize;
    }

    public List<ShipType> getShipTypes() {
        return shipTypes;
    }

    public List<AbilityType> getAbilityTypes() {
        return abilityTypes;
    }

}