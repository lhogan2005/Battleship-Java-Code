package com.example.demo;

import java.util.List;

public class Ship{
    
    private List<Cell> position;
    private ShipType type;
    private boolean isSunk;

    public Ship(List<Cell> position, ShipType type) {
        // constructor for Ship
        this.position = position;
        this.type = type;
        this.isSunk = false;
    }

    public List<Cell> getPosition() { 
        // return the cells that the Ship occupies
        return position;
    }
    
    public ShipType getShipType() { 
        // returns the type of Ship
        return type;
    }
    public boolean getSunk() { 
        // returns whether the Ship is sunk or not
        return isSunk;
    }
    public void hitCell(Cell cell) { 
        // records a hit on a cell occupied by Ship and checks whether the ship has been sunk
        for (Cell shipCell : position) {
            if (shipCell.getStatus() != CellStatus.HIT) {
                isSunk = false;
                return;
            }
            isSunk = true;
        }
    }

}

