package com.example.demo;

public class Radar extends Ability {
    
    public Radar() {
        // Radar constructor
        //initialised with cost, inherited from superclass
        super(2);
    }
    
    @Override
    protected void effect(Cell target, Grid grid) {
        // radar implementation
        // Given cell and grid. Grid should be size x, y. Cell should be some x and y coordinate.
        int row = target.getRow();
        int column = target.getColumn();
        int shipNum = 0;

        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = column - 1; c <= column + 1; c++) {
                // reveal grid info on each square.
                if (grid.getCell(r, c).hasShip()) {
                    shipNum++;
                }
            }
        }
        System.out.println(shipNum);
        return;
    }
}
