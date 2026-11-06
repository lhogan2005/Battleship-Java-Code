public class Bomb extends Ability {
    
    public Bomb() {
        // Bomb constructor
        //initialised with cost, inherited from superclass
        super(3);
    }
    
    @Override
    protected void effect(Cell target, Grid grid){
        //Bomb implementation
        int column = target.getColumn();
        int row = target.getRow();

        for (int r = row - 1; r < row + 1; r++) {
            grid.hitCell(grid.getCell(r, column));
        }

        for (int c = column - 1; c < column + 1; c++) {
            if (c != column) {
                grid.hitCell(grid.getCell(row, c));
            }
        }
    }
}