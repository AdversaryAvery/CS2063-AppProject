package com.Team13.myapplication;

import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class Player {
    protected Boolean isDone;
    protected ArrayList<Card> hand;
    private Rank rank;
    protected int turnDecision;

    public Rank rankHand(ArrayList<Card> hand) {
        int highest = 0;
        int hRank = 0;
//      Sort hand in descending order
        Collections.sort(hand);
        highest = hand.get(0).getValue();

        if (checkStrFlush(hand)) { hRank = 6; }
        else if (checkToK(hand)) {hRank = 5; }
        else if (checkStraight(hand)) {hRank = 4; }
        else if (checkFlush(hand)) {hRank = 3; }
        else if (checkPair(hand)) {hRank = 2; }
        else { hRank = 1; } // Highest Card

        Rank playerRank = new Rank();
        playerRank.setHandRank(hRank);
        playerRank.setHighestCard(highest);

        return playerRank;
    }

    public void roundStart(int choices) {
        this.isDone = false;
        this.turnDecision = 0;
    }

    public void endTurn(){
        this.isDone = true;
    }

    private Boolean checkPair(ArrayList<Card> hand) {
        Boolean isPair = false;
        Map<Integer, Integer> valueAndCount = new HashMap<>();

//        Build hash table of each value and their count
        for (Card i : hand) {
            Integer count = valueAndCount.get(i.getValue());
            if (count == null) {
                valueAndCount.put(i.getValue(), 1);
            } else {
                valueAndCount.put(i.getValue(), ++count);
            }
        }
//        Build Set of mappings in HashTable
        Set<Map.Entry<Integer, Integer>> entrySet = valueAndCount.entrySet();
//        Check if any of the values occur at least 3 times
        for (Map.Entry<Integer, Integer> entry : entrySet) {
            if (entry.getValue() >= 2) {
                isPair = true;
            }
        }

        return isPair;
    }

    private Boolean checkFlush(ArrayList<Card> hand) {
        Boolean isFlush = true;
        char handSuit = hand.get(0).getSuit();

        for (int i = 1; i < hand.size(); i ++) {
            if (hand.get(i).getSuit() != handSuit) {
                isFlush = false;
                break;
            }
        }

        return isFlush;
    }

    private Boolean checkStraight(ArrayList<Card> hand) {
        Boolean isStraight = true;
        int prevValue = hand.get(0).getValue();

        for (int i = 1; i < hand.size(); i ++) {
            if (hand.get(i).getValue() != prevValue-1) {
                isStraight= false;
                break;
            }
            prevValue = hand.get(i).getValue();
        }

        return isStraight;
    }

    private Boolean checkToK(ArrayList<Card> hand) {
        Boolean isToK = false;
        Map<Integer, Integer> valueAndCount = new HashMap<>();

//        Build hash table of each value and their count
        for (Card i : hand) {
            Integer count = valueAndCount.get(i.getValue());
            if (count == null) {
                valueAndCount.put(i.getValue(), 1);
            } else {
                valueAndCount.put(i.getValue(), ++count);
            }
        }
//        Build Set of mappings in HashTable
        Set<Map.Entry<Integer, Integer>> entrySet = valueAndCount.entrySet();
//        Check if any of the values occur at least 3 times
        for (Map.Entry<Integer, Integer> entry : entrySet) {
            if (entry.getValue() >= 3) {
                isToK = true;
            }
        }

        return isToK;
    }

    private Boolean checkStrFlush(ArrayList<Card> hand) {
        Boolean isStrFlush = true;
        char suit = hand.get(0).getSuit();
        int prevValue = hand.get(0).getValue();

        for (int i = 1; i < hand.size(); i ++) {
            if (hand.get(i).getSuit() != suit || hand.get(i).getValue() != prevValue-1) {
                isStrFlush= false;
                break;
            }
            prevValue = hand.get(i).getValue();
        }

        return isStrFlush;
    }

    public void addCardToHand(Card card){
        this.hand.add(card);
    }

    public Player() {
        this.isDone = false;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public int getTurnDecision() {
        return turnDecision;
    }

    public void setTurnDecision(int turnDecision) {
        this.turnDecision = turnDecision;
    }
}
