public class LobbySettings {
    private int gridSize;

    private List<ShipType> shiptypes; // Shiptypes included in the Game

    private List<Ability> abilities; // Abilities available in the Game

    public LobbySettings(){
        // constructor for LobbySettings
        // Created with default settings
    }

    public void updateSettings(String setting, String value) {
        // updates specified Lobby setting
        // provided setting and value are valid
    }

    private void updateGridSize(String value){
        // updates grid to specified size
        // provided value is valid
    }

    private void updateShipTypes(String value) {
        // updates the shipType available in the Game
        //provided value is valid
    }

    private void updateAbilities(String value) {
        //updates the Abilities available in the Game
        //provided value is a is one or more valid abilities
    }

    public int getGridSize() {
        return gridSize;
    }

}