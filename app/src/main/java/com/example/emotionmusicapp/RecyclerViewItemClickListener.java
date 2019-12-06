package com.example.emotionmusicapp;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// class for song list item click listener
public class RecyclerViewItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener songListItemClickListener;

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    GestureDetector songClickGesture;

    public RecyclerViewItemClickListener(Context context, OnItemClickListener listener) {
        songListItemClickListener = listener;

        songClickGesture = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }
        });
    }


    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(), e.getY());

        if (childView != null && songListItemClickListener != null && songClickGesture.onTouchEvent(e)) {
            songListItemClickListener.OnItemClick(childView, rv.getChildAdapterPosition(childView));
        }

        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
