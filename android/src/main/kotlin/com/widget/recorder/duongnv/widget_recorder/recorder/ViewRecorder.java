package com.widget.recorder.duongnv.widget_recorder.recorder;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.os.Looper;
import android.util.Size;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ViewRecorder extends SurfaceMediaRecorder {

    private IBitmap mRecordedView;

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
        public void onDraw(Canvas canvas) {
//            mRecordedView.setDrawingCacheEnabled(true);
            Bitmap bitmap = mRecordedView.getBitmap();

            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            int videoWidth = mVideoSize.getWidth();
            int videoHeight = mVideoSize.getHeight();
            Matrix matrix = getMatrix(bitmapWidth, bitmapHeight, videoWidth, videoHeight);
            canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR);
            canvas.drawBitmap(bitmap, matrix, null);

//            mRecordedView.setDrawingCacheEnabled(false);
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
            if (mRecordedView == null) {
                throw new IllegalStateException("recorded view is not initialized yet");
            }
            setWorkerLooper(Looper.getMainLooper());
            setVideoFrameDrawer(mVideoFrameDrawer);
        }

        super.start();
    }

    /**
     * Sets recorded view to be captured for video frame composition. Call this method before start().
     * You may change the recorded view with this method during recording.
     * <p>
     * //     * @param view the view to be captured
     */
    public void setRecordedView(@NonNull IBitmap iBitmap) throws IllegalStateException {
        mRecordedView = iBitmap;
    }

    public interface IBitmap {
        @Nullable
        Bitmap getBitmap();
    }
}
