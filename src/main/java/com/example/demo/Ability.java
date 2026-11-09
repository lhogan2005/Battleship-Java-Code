package com.example.demo;

public abstract class Ability {
    // Initalise a cost variable
    protected int cost;

    public Ability(int cost){
        // Ability is created with specified cost
        this.cost = cost;
    }

    public int getCost(){
        // returns the cost of using Ability
        return cost;
    }

    // Called with an ability selected, this will recognise which ability the user pressed and pass the values to the corresponding function.
    public void use(Cell target, Grid grid) {
        // Calls effect to resolve ability.
        effect(target, grid);
    }

    protected abstract void effect(Cell target, Grid grid);
        // applies Ability effect to target cell
        // Ability depends on subclass

    
}