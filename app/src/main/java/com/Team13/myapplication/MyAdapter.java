package com.Team13.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private ArrayList<Card> hand;
    boolean showCard;

    public MyAdapter(ArrayList<Card> hand, boolean showCard) {
        this.hand = hand;
        this.showCard = showCard;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView cardImage;
        public ViewHolder(View v) {
            super(v);
            this.cardImage = v.findViewById(R.id.cardImage);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);

        return new ViewHolder(v);
    }


    public void onBindViewHolder(ViewHolder holder, int position) {

        final Card card = hand.get(position);

        holder.cardImage.setImageDrawable(card.getCardImage(showCard));


    }

    @Override
    public int getItemCount() {
        return hand.size();
    }
}
