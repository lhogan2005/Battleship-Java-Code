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
            // Checks if the cell is hit
            if (currCell.getStatus() == CellStatus.HIT) {
                // If hit we exit the for loop
                break;
            } else {
                // If not we set the cell to hit.
                currCell = grid.getCell(row, column);
                currCell.hit();
            }
        }
    }
}