package com.widget.recorder.duongnv.widget_recorder.recorder;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public interface VideoFrameDrawer {
        /**
         * Called when video frame is composing
         *
         * @param canvas the canvas on which content will be drawn
         */
        void onDraw(Canvas canvas, Bitmap bitmap);
    }
