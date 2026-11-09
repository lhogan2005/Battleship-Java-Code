package com.example.demo;

public class Cell {

    private int row; // row position of cell
    private int column; // column position of cell

    private CellStatus status; // current status of cell as defined in CellStatus enum

    private Ship ship; // reference to ship occupying this cell.

    public Cell(int row, int column){
        // constructor for Cell class
        // pre-condition: row and column are valid positions on Grid
        // post-condition: A cell is created at the specified position, status is initially BLANK
        // Status is initially BLANK
        // ship is initially null

        this.row = row;
        this.column = column;
        this.status = CellStatus.BLANK;
        this.ship = null;
    }

    public int getRow(){ 
        // returns row position of cell
        return row;
    }
    
    public int getColumn() {
        // returns column position  of cell
        return column;
    }
    
    public CellStatus getStatus() { 
        // returns current status of cell
        return status;
    }

    public boolean hasShip() {
        // returns whether or not the cell contains a ship
        return ship != null;
    
    }

    public void setShip(Ship ship) {
        // assigns ship to cell
        this.ship = ship;
    }

    public Ship getShip() {
        // returns ship occupying this cell
        return ship;
    }

    public void hit() {
        // updates cell status after hit
        if (hasShip()) {
            status = CellStatus.HIT;
        }
        else {
            status = CellStatus.MISS;
        }
    }


}