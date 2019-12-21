package com.example.emotionmusicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taishi.library.Indicator;

import java.util.ArrayList;

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.CustomRecyclerViewHolder> {
    private ArrayList<CustomRecyclerViewItem> customLists;
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClicked(int position);
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public static class CustomRecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView songNameTV, singerNameTV;
        public Indicator songIndicator;
        public ImageButton removeSongButton;

        public CustomRecyclerViewHolder(@NonNull View itemView, final OnItemClickListener itemClickListener) {
            super(itemView);

            songNameTV = itemView.findViewById(R.id.songNameLVTV);
            singerNameTV = itemView.findViewById(R.id.singerNameLVTV);
            removeSongButton = itemView.findViewById(R.id.removeSongButton);
            songIndicator = itemView.findViewById(R.id.songIndicator);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        int itemPosition = getAdapterPosition();

                        if (itemPosition != RecyclerView.NO_POSITION) {
                            itemClickListener.onItemClicked(itemPosition);
                        }
                    }
                }
            });
        }
    }

    public CustomRecyclerViewAdapter(ArrayList<CustomRecyclerViewItem> customList) {
        this.customLists = customList;
    }

    @NonNull
    @Override
    public CustomRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_row, parent, false);

        return new CustomRecyclerViewHolder(view, this.itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomRecyclerViewHolder holder, int position) {
        CustomRecyclerViewItem currentItem = customLists.get(position);

        holder.songNameTV.setText(currentItem.getSongName());
        holder.singerNameTV.setText(currentItem.getSingerName());
        holder.removeSongButton.setImageResource(R.drawable.remove_song_ic);
        holder.songIndicator = currentItem.getSongIndicator();
    }

    @Override
    public int getItemCount() {
        return customLists.size();
    }
}
