import java.util.ArrayList;

public class Grid {

    private Cell[][] cells;
    private int size;

    public Grid(int size) {

        this.size = size;
        // constructor for Grid
        cells = new Cell[size][size];

        for (int row = 0; row < size; row++){
            for (int column = 0; column < size; column++) {
                cells[row][column] = new Cell(row, column);
            }
        }
    }

    public Cell getCell(int row, int column) {
        // returns the cell at specified Grid position
        //provided row and column are valid
        return cells[row][column];
    }

    public void hitCell(Cell cell) { 
        // declares attack on a Cell on Grid
        //provided cell has not already been attacked
        //cell status will be changed to HIT or MISS

        if (cell.getStatus() == CellStatus.HIT  || cell.getStatus() == CellStatus.MISS) {
            return;
        }

        cell.hit();

        if (cell.hasShip()) {
            cell.getShip().hitCell(cell);
        }

    }

    public void hitRadius(Cell origin, int radius) {
        // Attacks cells within a radious of specified origin Cell
        int originrow = origin.getRow();
        int origincolumn = origin.getColumn();

        for (int row = originrow - radius; row <= originrow + radius; row++) {
            for (int column = origincolumn - radius; column <= origincolumn + radius; column++) {
                
                if (row >= 0  && row < cells.length && column >= 0 && column < cells[row].length) {
                    Cell radiusCell = getCell(row, column);
                    hitCell(radiusCell);
                }
            }
        }




    }

    public Ship placeShip(ShipType type, int row, int column, boolean orientation) { // false = horizontal, true = vertical
        // allows a ship of specified type to be placed on Grid
        // Determines what cells the ship will occupy based off of where it was placed, its size and orientation
        // provided type is valid, position is within the grid
        // the ship cannot exist outside the grid
        // ship be placed on any cells already occupied by another ship
        //A ship is created and placed on the calculated ceslls
        // placed ship is returned 
        // if invalid no ship is placed
        // each cell that a ship is occupied is given reference to the ship that occupies it for retrieval later on hit


        //false = horizontal
        // true = vertical


        int shipSize = type.getSize(); // size of ship to be placed
        ArrayList<Cell> shipCells = new ArrayList<Cell>(); //temp collection of occuptied cells by ship
        int currentRow;
        int currentColumn;

        for (int i = 0; i < shipSize; i++) {
            if (!orientation) { // Horizontal, keep row unchaged, move one column for each section of ship
                currentRow = row;
                currentColumn = column + i;
            }
            else { // vertical, keerp column unchanged, move one row for each section
                currentRow = row + i;
                currentColumn = column;
            }

            // check cell is inside grid
            if (currentRow < 0 || currentRow >= cells.length || currentColumn < 0  || currentColumn >= cells[currentRow].length) {
                return null;
            }


            // get cell object
            Cell currentCell = getCell(currentRow, currentColumn);
            
            if (currentCell.hasShip()) {
                return null;
            }

            shipCells.add(currentCell);

        }
        
        // create the actual ship
        Ship newShip = new Ship(shipCells, type);

        //pass reference to each occupied cell
        for (Cell cell : shipCells) {
            cell.setShip(newShip);
        }

        //returns placed ship to be used later
        return newShip;


    }

    public int getSize() {
        return size;
    }
    
}