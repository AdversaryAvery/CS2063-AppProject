package com.Team13.myapplication;

import android.graphics.drawable.Drawable;

public class Card implements Comparable<Card> {
    private Boolean isFaceUp;
    private int value;
    private char suit;
    private Drawable faceUpCard;
    private Drawable faceDownCard;

    public Boolean getFaceUp() {
        return isFaceUp;
    }

    public void setFaceUp(Boolean faceUp) {
        isFaceUp = faceUp;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public char getSuit() {
        return suit;
    }

    public void setSuit(char suit) {
        this.suit = suit;
    }

    public Drawable getFaceUpCard() {
        return faceUpCard;
    }

    public void setFaceUpCard(Drawable faceUpCard) {
        this.faceUpCard = faceUpCard;
    }

    public Drawable getFaceDownCard() {
        return faceDownCard;
    }

    public void setFaceDownCard(Drawable faceDownCard) {
        this.faceDownCard = faceDownCard;
    }

    @Override
    public int compareTo(Card other) {
        if(this.getValue() > other.getValue()) {
            return -1;
        }
        return 1 ;
    }

    public Card(int value, char suit, Drawable faceUpCard, Drawable faceDownCard) {
        this.value = value;
        this.suit = suit;
        this.faceUpCard = faceUpCard;
        this.faceDownCard = faceDownCard;
        this.isFaceUp = true;
    }
}
