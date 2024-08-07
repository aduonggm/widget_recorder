package com.widget.recorder.duongnv.widget_recorder.recorder;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.os.Looper;
import android.util.Log;
import android.util.Size;

import androidx.annotation.Nullable;

public class ViewRecorder extends SurfaceMediaRecorder {


    private Size mVideoSize;

    private final VideoFrameDrawer mVideoFrameDrawer = new VideoFrameDrawer() {
        private Matrix getMatrix(int bw, int bh, int vw, int vh) {
            Matrix matrix = new Matrix();
            float scale, scaleX = 1, scaleY = 1, transX, transY;

            if (bw > vw) {
                scaleX = ((float) vw) / bw;
            }
            if (bh > vh) {
                scaleY = ((float) vh) / bh;
            }
            scale = (Math.min(scaleX, scaleY));
            transX = (vw - bw * scale) / 2;
            transY = (vh - bh * scale) / 2;

            matrix.postScale(scale, scale);
            matrix.postTranslate(transX, transY);

            return matrix;
        }

        @Override
        public void onDraw(Canvas canvas, Bitmap bitmap) {
            if (bitmap == null) return;
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            int videoWidth = mVideoSize.getWidth();
            int videoHeight = mVideoSize.getHeight();
            Matrix matrix = getMatrix(bitmapWidth, bitmapHeight, videoWidth, videoHeight);
            canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
            Log.d("duongnv", "onDraw:  bitmap is recycle" + bitmap.isRecycled());
            canvas.drawBitmap(bitmap, matrix, null);
            bitmap.recycle();
        }
    };

    @Override
    public void setVideoSize(int width, int height) throws IllegalStateException {
        super.setVideoSize(width, height);
        mVideoSize = new Size(width, height);
    }

    @Override
    public void start() throws IllegalStateException {
        if (isSurfaceAvailable()) {
            if (mVideoSize == null) {
                throw new IllegalStateException("video size is not initialized yet");
            }

            setWorkerLooper(Looper.getMainLooper());
            setVideoFrameDrawer(mVideoFrameDrawer);
        }

        super.start();
    }
}
