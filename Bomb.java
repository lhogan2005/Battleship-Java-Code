public class Bomb extends Ability {
    
    public Bomb() {
        // Bomb constructor
        //initialised with cost, inherited from superclass
        super(3);
    }
    
    @Override
    protected void effect(Cell target, Grid grid){
        //Bomb implementation

        // Gets column selected
        int column = target.getColumn();

        // Gets row selected
        int row = target.getRow();

        // Itterates from our row -1 to our row + 1 and hits each cell.
        for (int r = row - 1; r <= row + 1; r++) {
            grid.hitCell(grid.getCell(r, column));
        }

        // Iterates from our column -1 to our column + 1 and hits each cell.
        for (int c = column - 1; c <= column + 1; c++) {
            // Ensures we don't hit the central cell twice.
            if (c != column) {
                grid.hitCell(grid.getCell(row, c));
            }
        }
    }
}