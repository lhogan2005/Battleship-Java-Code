public enum ShipType {
    Battleship(4),
    Destroyer(3),
    Submarine(3),
    PatrolBoat(2),
    Carrier(5);

    private final int size;

    ShipType(int size) {
        this.size = size;
    }

    public int getSize() { 
        // returns the size of ship type
        return size;
    }
}