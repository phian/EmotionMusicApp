package com.example.emotionmusicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.taishi.library.Indicator;

import net.igenius.customcheckbox.CustomCheckBox;

import java.util.ArrayList;

public class CustomRecyclerViewAdapter extends RecyclerView.Adapter<CustomRecyclerViewAdapter.CustomRecyclerViewHolder> {
    private ArrayList<CustomRecyclerViewItem> customLists;

    public static class CustomRecyclerViewHolder extends RecyclerView.ViewHolder {
        public TextView songNameTV, singerNameTV;
        public CustomCheckBox deleteCB;
        public Indicator songIndicator;

        public CustomRecyclerViewHolder(@NonNull View itemView) {
            super(itemView);

            songNameTV = itemView.findViewById(R.id.songNameLVTV);
            singerNameTV = itemView.findViewById(R.id.singerNameLVTV);
            deleteCB = itemView.findViewById(R.id.deleteSongCB);
            songIndicator = itemView.findViewById(R.id.songIndicator);
        }
    }

    public CustomRecyclerViewAdapter(ArrayList<CustomRecyclerViewItem> customList) {
        this.customLists = customList;
    }

    @NonNull
    @Override
    public CustomRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_row, parent, false);

        CustomRecyclerViewHolder customHolder = new CustomRecyclerViewHolder(view);

        return customHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomRecyclerViewHolder holder, int position) {
        CustomRecyclerViewItem currentItem = customLists.get(position);

        holder.songNameTV.setText(currentItem.getSongName());
        holder.singerNameTV.setText(currentItem.getSingerName());
        holder.deleteCB.setChecked(currentItem.getDeleteCB().isChecked());
        holder.songIndicator = currentItem.getSongIndicator();
    }

    @Override
    public int getItemCount() {
        return customLists.size();
    }
}
