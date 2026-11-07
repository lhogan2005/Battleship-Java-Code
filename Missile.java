public class Missile extends Ability {

    public Missile() {
        //Missle constructor
        //initialised with cost, inherited from superclass
        super(4);
    }

    @Override
    protected void effect(Cell target, Grid grid){
        //Missle implementation
        int column = target.getColumn();
        Cell currCell;

        for (int row = 0; row < grid.getSize(); row++) {
            currCell = grid.getCell(row, column);
            if (currCell.getStatus() == CellStatus.HIT) {
                break;
            } else {
                currCell = grid.getCell(row, column);
                currCell.hit();
            }
        }
    }
}