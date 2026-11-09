package com.example.demo;

public class Hit extends Ability {
    
    public Hit() {
        // Hit Constructor
        //initialised with cost, inherited from superclass
        super(0);
    }
    
    
    @Override
    protected void effect(Cell target, Grid grid){
        //Implements basic hit on target cell
        grid.hitCell(target);
    }
}