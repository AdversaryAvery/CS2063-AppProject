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
    private int donationCard = 0;
    private int playerNumber;

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

    public void roundStart(int min, int max) {
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
        hand = new ArrayList<Card>();
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

    public Rank altRank(ArrayList<Card> hand){
        return altRank(hand,5);
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public Rank altRank(ArrayList<Card> hand, int cardsPerHand){
        /*
        * 10 - Royal Flush
        * 9 - Straight Flush
        * 8 - Four of a Kind
        * 7 - Full House
        * 6 - Flush
        * 5 - Straight
        * 4 - Three of a Kind
        * 3 - Two Pair
        * 2 - Pair
        * 1 - High Card
        * */
        Rank handRank = new Rank();

        int highestCard = 2;

        boolean flushPotential = true;
        boolean straightPotential = true;

        boolean hasPair = false;
        boolean hasTwoPair = false;
        boolean hasThree = false;
        boolean hasFour = false;
        boolean hasFlush = false;
        boolean hasStraight = false;

        int[] valueArray = new int[]{0,0,0,0,0,0,0,0,0,0,0,0,0};

        for(Card card: hand){
            valueArray[card.getValue() - 2]++;
        }

        for(int index = 0; index < 13; index++){
            int i = valueArray[index];

            if(i >= 1 && !hasPair && !hasThree && !hasFour){highestCard = index + 2;}

            if(i == 2 && !hasPair){
                hasPair = true;
                straightPotential = false;
                flushPotential = false;
                if(!hasThree){
                    highestCard = index + 2;
                }
            }
            else if (i == 2 && hasPair){
                hasTwoPair = true;
                highestCard = index + 2;
            }
            else if(i == 3){
                hasThree = true;
                straightPotential = false;
                flushPotential = false;
                highestCard = index + 2;
            }
            else if(i == 4){
                hasFour = true;
                straightPotential = false;
                flushPotential = false;
                highestCard = index + 2;
            }
        }

        if(straightPotential){
            int sequenceCount = 0;

            for(int i = -1; i< 13; i++){
                if(valueArray[(i + 13)%13] > 0){
                    sequenceCount++;
                }
                else{
                    sequenceCount = 0;
                }

                if(sequenceCount >= cardsPerHand){
                    hasStraight = true;
                    highestCard = i+2;
                }
            }
        }

        if(flushPotential){
            char suit = hand.get(0).getSuit();
            for(Card card: hand){
                if(card.getSuit() != suit){
                    flushPotential = false;
                }
            }
            hasFlush = flushPotential;
        }

        int rankInt = 1;
        if(cardsPerHand == 5) {
            if (hasFlush && hasStraight && highestCard == 14) {
                rankInt = 10;
            } // Royal Flush
            else if (hasFlush && hasStraight) {
                rankInt = 9;
            } // Straight Flush
            else if (hasFour) {
                rankInt = 8;
            } // Four of a Kind
            else if (hasThree && hasPair) {
                rankInt = 7;
            } // Full House
            else if (hasFlush) {
                rankInt = 6;
            } // Flush
            else if (hasStraight) {
                rankInt = 5;
            } // Straight
            else if (hasThree) {
                rankInt = 4;
            } // Three of a Kind
            else if (hasTwoPair) {
                rankInt = 3;
            } // Two Pair
            else if (hasPair) {
                rankInt = 2;
            } // Pair
            //else High Card
        }

        else{ // 3 Cards per hand
            if (hasFlush && hasStraight) {
                rankInt = 6;
            } // Straight Flush

            else if(hasThree){
                rankInt = 5;
            } // Three of a Kind

            else if(hasStraight){
                rankInt = 4;
            } // Straight
            else if(hasFlush){
                rankInt = 3;
            } //Flush
            else if(hasPair){
                rankInt = 2;
            } //Pair
             //else High Card
        }

        handRank.setHandRank(rankInt);
        handRank.setHighestCard(highestCard);
        this.rank = handRank;
        return handRank;
    }


    public Card getDonationCard(){
        Card tempCard = hand.get(donationCard);
        hand.remove(donationCard);
        return tempCard;
    }

    public void incrementDecision(int movesPerRound){
        if(turnDecision < movesPerRound) turnDecision++;
    }
    public void decrementDecision(int movesPerRound){
        if(turnDecision > -movesPerRound) turnDecision--;
    }
}
