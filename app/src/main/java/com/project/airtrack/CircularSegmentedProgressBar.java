package com.project.airtrack;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * CircularSegmentedProgressBar is a custom view that displays a circular progress bar
 * divided into segments. The progress is represented by coloring a specific number
 * of these segments.
 * The view is customizable in terms of segment count, segment colors, stroke width,
 * and the gap angle between segments. It supports setting the progress dynamically
 * and redraws itself accordingly.
 */
public class CircularSegmentedProgressBar extends View {

    private final int segments = 9;
    private int progress = 3;
    private final int segmentsNotDrawn = 3;
    private int[] segmentColorsInactive;
    private int[] segmentColorsActive;
    private final float strokeWidth = 50f;
    private final float gapAngle = 2f;

    private Paint paint;
    private RectF rectF;

    public CircularSegmentedProgressBar(Context context) {
        super(context);
        init();
    }

    public CircularSegmentedProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularSegmentedProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // Initialization method to set up paint and segment colors
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.BUTT);
        paint.setStrokeWidth(strokeWidth);

        // Define segment colors
        segmentColorsInactive = new int[]{
                ContextCompat.getColor(getContext(), R.color.gray)
        };

        segmentColorsActive = new int[]{
                ContextCompat.getColor(getContext(), R.color.green),
                ContextCompat.getColor(getContext(), R.color.yellow),
                ContextCompat.getColor(getContext(), R.color.orange),
                ContextCompat.getColor(getContext(), R.color.red),
                ContextCompat.getColor(getContext(), R.color.purple),
                ContextCompat.getColor(getContext(), R.color.brown)
        };

        rectF = new RectF();
    }

    // Updates the progress and redraws the view
    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        // The initial starting angle for the first segment. It draws the segment counterclockwise
        float startAngle = 150f;
        // Calculate the angle each segment will cover
        float sweepAngle = (360f - (segments * gapAngle)) / segments;

        int segmentsToDraw = Math.max(0, segments - segmentsNotDrawn);

        // Set color based on whether the segment is active or not
        for (int i = 0; i < segmentsToDraw; i++) {
            if (i < progress) {
                paint.setColor(segmentColorsActive[i % segmentColorsActive.length]);
            } else {
                paint.setColor(segmentColorsInactive[0]);
            }
            // Ensure full opacity for both active and inactive segments
            paint.setAlpha(255);
            canvas.drawArc(rectF, startAngle, sweepAngle, false, paint);
            // Move to the start angle of the next segment
            startAngle += (sweepAngle + gapAngle);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Adjust the drawing rectangle based on the view size
        float size = Math.min(w, h);
        rectF.set(strokeWidth / 2, strokeWidth / 2, size - strokeWidth / 2, size - strokeWidth / 2);
    }
}