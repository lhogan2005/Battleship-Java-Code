public abstract class Ability {
    protected int cost;

    public Ability(int cost){
        // Ability is created with specified cost
        this.cost = cost;
    }

    public int getCost(){
        // returns the cost of using Ability
        return cost;
    }

    public void use(Cell target, Grid grid) {
        effect(target, grid);
    }

    protected abstract void effect(Cell target, Grid grid);
        // applies Ability effect to target cell
        // Ability depends on subclass

    
}