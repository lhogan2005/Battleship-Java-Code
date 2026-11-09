package com.example.demo;

public class Missile extends Ability {

    public Missile() {
        //Missle constructor
        //initialised with cost, inherited from superclass
        super(4);
    }

    @Override
    protected void effect(Cell target, Grid grid){
        //Missle implementation
        // This will use the cell getColumn to save the column. This will be called later.
        int column = target.getColumn();
        // Initialise a current cell variable to be called later.
        Cell currCell;

        // Creates loop iterating over the row
        for (int row = 0; row < grid.getSize(); row++) {
            // saves the current cell to currCell
            currCell = grid.getCell(row, column);
            boolean wasBlank = currCell.getStatus() == CellStatus.BLANK;
            
            grid.hitCell(currCell);

            // Checks if the cell is hit
            if (wasBlank && currCell.hasShip()) {
                // If hit we exit the for loop
                break;
            }
        }
    }
}