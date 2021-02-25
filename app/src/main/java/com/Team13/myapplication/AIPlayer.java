package com.Team13.myapplication;

import java.util.ArrayList;

public class AIPlayer extends Player {
    private int randomSeed;
    private int uBound = 100;
    private int lBound = 1;

    public AIPlayer() {    }

    @Override
    public void roundStart(int min, int max) {
//        Generates a random nuber between 1 - 100
        randomSeed = (int)(Math.random() * (uBound - lBound + 1) + lBound);
//        Set AI Player's choice
        this.turnDecision = (randomSeed % (max - min + 1)) - max;
//        End AI Player's turn
        this.isDone= true;
    }
}
