package com.example.emotionmusicapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class MediaVisualizerView extends View {
    private byte[] mediaBytes;
    private float[] mediaPoints;
    private Rect mediaRect = new Rect();
    private Paint mediaForePaint = new Paint();

    public MediaVisualizerView(Context context) {
        super(context);

        initMediaVisualizerView();
    }

    public MediaVisualizerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        initMediaVisualizerView();
    }

    public MediaVisualizerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initMediaVisualizerView();
    }

    private void initMediaVisualizerView() {
        mediaBytes = null;
        mediaForePaint.setStrokeWidth(1f);
        mediaForePaint.setAntiAlias(true);
        mediaForePaint.setColor(Color.rgb(0, 128, 255));
    }

    public void updateMediaVisualizerView(byte[] mBytes) {
        this.mediaBytes = mBytes;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mediaBytes == null) {
            return;
        }
        if (mediaPoints == null || mediaPoints.length < mediaBytes.length * 4) {
            mediaPoints = new float[mediaBytes.length * 4];
        }

        mediaRect.set(0, 0, getWidth(), getHeight());

        for (int i = 0; i < mediaBytes.length - 1; i++) {
            mediaPoints[i * 4] = mediaRect.width() * i / (mediaBytes.length - 1);
            mediaPoints[i * 4 + 1] = mediaRect.height() / 2
                    + ((byte) (mediaBytes[i] + 128)) * (mediaRect.height() / 2) / 128;
            mediaPoints[i * 4 + 2] = mediaRect.width() * (i + 1) / (mediaBytes.length - 1);
            mediaPoints[i * 4 + 3] = mediaRect.height() / 2
                    + ((byte) (mediaBytes[i + 1] + 128)) * (mediaRect.height() / 2)
                    / 128;
        }
        canvas.drawLines(mediaPoints, mediaForePaint);
    }
}
