package com.example.demo;

public enum ShipType {
    BATTLESHIP(4),
    DESTROYER(3),
    SUBMARINE(3),
    PATROLBOAT(2),
    CARRIER(5);

    private final int size;

    ShipType(int size) {
        this.size = size;
    }

    public int getSize() { 
        // returns the size of ship type
        return size;
    }
}