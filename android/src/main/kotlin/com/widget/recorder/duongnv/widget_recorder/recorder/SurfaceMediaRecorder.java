package com.widget.recorder.duongnv.widget_recorder.recorder;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaRecorder;
import android.os.Looper;
import android.view.Surface;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class extends {@link MediaRecorder} and manages to compose each video frame for recording.
 * Two extra initialization steps before {@link #start()},
 * <pre>
 * {@link #setWorkerLooper(Looper)}
 * {@link #setVideoFrameDrawer(VideoFrameDrawer)}
 * </pre>
 * <p>
 * Also you can use it as same as {@link MediaRecorder} for other functions.
 *
 * <p> By the way, one more error type {@link #MEDIA_RECORDER_ERROR_SURFACE} is defined for surface error.
 * <p>
 * Created by z4hyoung on 2017/11/8.
 */

public class SurfaceMediaRecorder extends MediaRecorder {
    /**
     * Surface error during recording, In this case, the application must release the
     * MediaRecorder object and instantiate a new one.
     *
     * @see OnErrorListener
     */
    public static final int MEDIA_RECORDER_ERROR_SURFACE = 10000;
    /**
     * Surface error when getting for drawing into this {@link Surface}.
     *
     * @see OnErrorListener
     */
    public static final int MEDIA_RECORDER_ERROR_CODE_LOCK_CANVAS = 1;
    /**
     * Surface error when releasing and posting content to {@link Surface}.
     *
     * @see OnErrorListener
     */
    public static final int MEDIA_RECORDER_ERROR_CODE_UNLOCK_CANVAS = 2;

    /**
     * Interface defined for user to customize video frame composition
     */

    /**
     * default inter-frame gap
     */
    private static final long DEFAULT_INTERFRAME_GAP = 1000;
    private int mVideoSource;
    private OnErrorListener mOnErrorListener;
    private Surface mSurface;
    // if set, this class works same as MediaRecorder
    private Surface mInputSurface;
    private VideoFrameDrawer mVideoFrameDrawer;
    // indicate surface composing started or not
    private final AtomicBoolean mStarted = new AtomicBoolean(false);
    // indicate surface composing paused or not
    private final AtomicBoolean mPaused = new AtomicBoolean(false);

    private void handlerCanvasError(int errorCode) {
        try {
            stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mOnErrorListener != null) {
            mOnErrorListener.onError(SurfaceMediaRecorder.this, MEDIA_RECORDER_ERROR_SURFACE, errorCode);
        }
    }

    public void drawFrame(Bitmap bitmap) {
        if (!isRecording()) {
            return;
        }
        Integer errorCode = null;
        do {
            Canvas canvas;
            try {
                canvas = mSurface.lockCanvas(null);
            } catch (Exception e) {
                errorCode = MEDIA_RECORDER_ERROR_CODE_LOCK_CANVAS;
                e.printStackTrace();
                break;
            }
            mVideoFrameDrawer.onDraw(canvas, bitmap);
            try {
                mSurface.unlockCanvasAndPost(canvas);
            } catch (Exception e) {
                errorCode = MEDIA_RECORDER_ERROR_CODE_UNLOCK_CANVAS;
                e.printStackTrace();
                break;
            }
        } while (false);

        if (!isRecording()) {
            return;
        }

        if (errorCode != null) {
            handlerCanvasError(errorCode);
        }
    }

    @Override
    public void pause() throws IllegalStateException {
        if (isSurfaceAvailable()) {
            mPaused.set(true);
        }
        super.pause();
    }

    @Override
    public void reset() {
        localReset();
        super.reset();

    }

    @Override
    public void resume() throws IllegalStateException {
        super.resume();
        if (isSurfaceAvailable()) {
            mPaused.set(false);
        }
    }

    @Override
    public void setOnErrorListener(OnErrorListener l) {
        super.setOnErrorListener(l);
        mOnErrorListener = l;
    }

    @Override
    public void setInputSurface(@NonNull Surface surface) {
        super.setInputSurface(surface);
        mInputSurface = surface;
    }


    @Override
    public void setVideoSource(int video_source) throws IllegalStateException {
        super.setVideoSource(video_source);
        mVideoSource = video_source;
    }

    @Override
    public void start() throws IllegalStateException {
        if (isSurfaceAvailable()) {
            if (mVideoFrameDrawer == null) {
                throw new IllegalStateException("video frame drawer is not initialized yet");
            }
        }

        super.start();
        if (isSurfaceAvailable()) {
            mSurface = getSurface();
            mStarted.set(true);
        }
    }

    @Override
    public void stop() throws IllegalStateException {
        localReset();
        super.stop();
    }

    /**
     * Sets video frame drawer for composing.
     *
     * @param drawer the drawer to compose frame with {@link Canvas}
     * @throws IllegalStateException if it is called after {@link #start()}
     */
    public void setVideoFrameDrawer(@NonNull VideoFrameDrawer drawer) throws IllegalStateException {
        if (isRecording()) {
            throw new IllegalStateException("setVideoFrameDrawer called in an invalid state: Recording");
        }
        mVideoFrameDrawer = drawer;
    }

    /**
     * Sets worker looper in which composing task executed
     *
     * @param looper the looper for composing
     * @throws IllegalStateException if it is called after {@link #start()}
     */
    public void setWorkerLooper(@NonNull Looper looper) throws IllegalStateException {
        if (isRecording()) {
            throw new IllegalStateException("setWorkerLooper called in an invalid state: Recording");
        }
    }

    /**
     * Returns whether Surface is editable
     *
     * @return true if surface editable
     */
    protected boolean isSurfaceAvailable() {
        return (mVideoSource == VideoSource.SURFACE) && (mInputSurface == null);
    }

    private boolean isRecording() {
        return (mStarted.get() && !mPaused.get());
    }

    private void localReset() {
        if (isSurfaceAvailable()) {
            mStarted.compareAndSet(true, false);
            mPaused.compareAndSet(true, false);
        }
        mInputSurface = null;
        mOnErrorListener = null;
        mVideoFrameDrawer = null;
    }
}
