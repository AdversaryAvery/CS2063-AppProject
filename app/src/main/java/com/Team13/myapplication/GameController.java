package com.Team13.myapplication;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Collections;

public class GameController {
    private int roundNum;
    private float time;
    private int sumOfTurns;

    private ArrayList<Card> wheel;
    private ArrayList<Card> deck;

    private ArrayList<Player> allPlayers;


    public void sumAllDecisions(ArrayList<Object> allPlayers){
        int numberOfTurns = 0;
        for(Object player: allPlayers){
            //numberOfTurns += player.decision;
        }
    }

    public void assignCards2Wheel(){
        int i = 0;
        while(i < allPlayers.size()){
            this.wheel.add(this.deck.get(0));
            this.deck.remove(0);
            i++;
        }
    }

    public ArrayList<Card> makeDeck(Resources res){
        ArrayList<Card> tempDeck = new ArrayList<Card>();

        Drawable cardBack =  ResourcesCompat.getDrawable(res,R.drawable.card_red_back,null);

        tempDeck.add(new Card(14,'s', ResourcesCompat.getDrawable(res,R.drawable.card_as,null),cardBack));
        tempDeck.add(new Card(2,'s', ResourcesCompat.getDrawable(res,R.drawable.card_2s,null),cardBack));
        tempDeck.add(new Card(3,'s', ResourcesCompat.getDrawable(res,R.drawable.card_3s,null),cardBack));
        tempDeck.add(new Card(4,'s', ResourcesCompat.getDrawable(res,R.drawable.card_4s,null),cardBack));
        tempDeck.add(new Card(5,'s', ResourcesCompat.getDrawable(res,R.drawable.card_5s,null),cardBack));
        tempDeck.add(new Card(6,'s', ResourcesCompat.getDrawable(res,R.drawable.card_6s,null),cardBack));
        tempDeck.add(new Card(7,'s', ResourcesCompat.getDrawable(res,R.drawable.card_7s,null),cardBack));
        tempDeck.add(new Card(8,'s', ResourcesCompat.getDrawable(res,R.drawable.card_8s,null),cardBack));
        tempDeck.add(new Card(9,'s', ResourcesCompat.getDrawable(res,R.drawable.card_9s,null),cardBack));
        tempDeck.add(new Card(10,'s', ResourcesCompat.getDrawable(res,R.drawable.card_10s,null),cardBack));
        tempDeck.add(new Card(11,'s', ResourcesCompat.getDrawable(res,R.drawable.card_js,null),cardBack));
        tempDeck.add(new Card(12,'s', ResourcesCompat.getDrawable(res,R.drawable.card_qs,null),cardBack));
        tempDeck.add(new Card(13,'s', ResourcesCompat.getDrawable(res,R.drawable.card_ks,null),cardBack));

        tempDeck.add(new Card(14,'c', ResourcesCompat.getDrawable(res,R.drawable.card_ac,null),cardBack));
        tempDeck.add(new Card(2,'c', ResourcesCompat.getDrawable(res,R.drawable.card_2c,null),cardBack));
        tempDeck.add(new Card(3,'c', ResourcesCompat.getDrawable(res,R.drawable.card_3c,null),cardBack));
        tempDeck.add(new Card(4,'c', ResourcesCompat.getDrawable(res,R.drawable.card_4c,null),cardBack));
        tempDeck.add(new Card(5,'c', ResourcesCompat.getDrawable(res,R.drawable.card_5c,null),cardBack));
        tempDeck.add(new Card(6,'c', ResourcesCompat.getDrawable(res,R.drawable.card_6c,null),cardBack));
        tempDeck.add(new Card(7,'c', ResourcesCompat.getDrawable(res,R.drawable.card_7c,null),cardBack));
        tempDeck.add(new Card(8,'c', ResourcesCompat.getDrawable(res,R.drawable.card_8c,null),cardBack));
        tempDeck.add(new Card(9,'c', ResourcesCompat.getDrawable(res,R.drawable.card_9c,null),cardBack));
        tempDeck.add(new Card(10,'c', ResourcesCompat.getDrawable(res,R.drawable.card_10c,null),cardBack));
        tempDeck.add(new Card(11,'c', ResourcesCompat.getDrawable(res,R.drawable.card_jc,null),cardBack));
        tempDeck.add(new Card(12,'c', ResourcesCompat.getDrawable(res,R.drawable.card_qc,null),cardBack));
        tempDeck.add(new Card(13,'c', ResourcesCompat.getDrawable(res,R.drawable.card_kc,null),cardBack));

        tempDeck.add(new Card(14,'h', ResourcesCompat.getDrawable(res,R.drawable.card_ah,null),cardBack));
        tempDeck.add(new Card(2,'h', ResourcesCompat.getDrawable(res,R.drawable.card_2h,null),cardBack));
        tempDeck.add(new Card(3,'h', ResourcesCompat.getDrawable(res,R.drawable.card_3h,null),cardBack));
        tempDeck.add(new Card(4,'h', ResourcesCompat.getDrawable(res,R.drawable.card_4h,null),cardBack));
        tempDeck.add(new Card(5,'h', ResourcesCompat.getDrawable(res,R.drawable.card_5h,null),cardBack));
        tempDeck.add(new Card(6,'h', ResourcesCompat.getDrawable(res,R.drawable.card_6h,null),cardBack));
        tempDeck.add(new Card(7,'h', ResourcesCompat.getDrawable(res,R.drawable.card_7h,null),cardBack));
        tempDeck.add(new Card(8,'h', ResourcesCompat.getDrawable(res,R.drawable.card_8h,null),cardBack));
        tempDeck.add(new Card(9,'h', ResourcesCompat.getDrawable(res,R.drawable.card_9h,null),cardBack));
        tempDeck.add(new Card(10,'h', ResourcesCompat.getDrawable(res,R.drawable.card_10h,null),cardBack));
        tempDeck.add(new Card(11,'h', ResourcesCompat.getDrawable(res,R.drawable.card_jh,null),cardBack));
        tempDeck.add(new Card(12,'h', ResourcesCompat.getDrawable(res,R.drawable.card_qh,null),cardBack));
        tempDeck.add(new Card(13,'h', ResourcesCompat.getDrawable(res,R.drawable.card_kh,null),cardBack));

        tempDeck.add(new Card(14,'d', ResourcesCompat.getDrawable(res,R.drawable.card_ad,null),cardBack));
        tempDeck.add(new Card(2,'d', ResourcesCompat.getDrawable(res,R.drawable.card_2d,null),cardBack));
        tempDeck.add(new Card(3,'d', ResourcesCompat.getDrawable(res,R.drawable.card_3d,null),cardBack));
        tempDeck.add(new Card(4,'d', ResourcesCompat.getDrawable(res,R.drawable.card_4d,null),cardBack));
        tempDeck.add(new Card(5,'d', ResourcesCompat.getDrawable(res,R.drawable.card_5d,null),cardBack));
        tempDeck.add(new Card(6,'d', ResourcesCompat.getDrawable(res,R.drawable.card_6d,null),cardBack));
        tempDeck.add(new Card(7,'d', ResourcesCompat.getDrawable(res,R.drawable.card_7d,null),cardBack));
        tempDeck.add(new Card(8,'d', ResourcesCompat.getDrawable(res,R.drawable.card_8d,null),cardBack));
        tempDeck.add(new Card(9,'d', ResourcesCompat.getDrawable(res,R.drawable.card_9d,null),cardBack));
        tempDeck.add(new Card(10,'d', ResourcesCompat.getDrawable(res,R.drawable.card_10d,null),cardBack));
        tempDeck.add(new Card(11,'d', ResourcesCompat.getDrawable(res,R.drawable.card_jd,null),cardBack));
        tempDeck.add(new Card(12,'d', ResourcesCompat.getDrawable(res,R.drawable.card_qd,null),cardBack));
        tempDeck.add(new Card(13,'d', ResourcesCompat.getDrawable(res,R.drawable.card_kd,null),cardBack));

        this.deck = tempDeck;
        return tempDeck;
    }

    public int getRoundNum() {
        return roundNum;
    }

    public void setRoundNum(int roundNum) {
        this.roundNum = roundNum;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public int getSumOfTurns() {
        return sumOfTurns;
    }

    public void setSumOfTurns(int sumOfTurns) {
        this.sumOfTurns = sumOfTurns;
    }

    public ArrayList<Card> getWheel() {
        return wheel;
    }

    public void setWheel(ArrayList<Card> wheel) {
        this.wheel = wheel;
    }

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public void setDeck(ArrayList<Card> deck) {
        this.deck = deck;
    }

    public ArrayList<Player> getAllPlayers() {
        return allPlayers;
    }

    public void setAllPlayers(ArrayList<Player> allPlayers) {
        this.allPlayers = allPlayers;
    }

    public GameController(Resources res){
        this.makeDeck(res);
        Collections.shuffle(this.deck);
    }
}
