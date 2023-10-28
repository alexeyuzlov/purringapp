package com.example.purringapp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;

public class MainActivity extends AppCompatActivity {
    MediaPlayer mediaPlayer1, mediaPlayer2, mediaPlayer3, mediaPlayer4, mediaPlayer5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int pointCount = event.getPointerCount();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // A pressed gesture has started, the motion contains the initial starting location.
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                // A non-primary pointer has gone down.
                break;
            case MotionEvent.ACTION_MOVE:
                // A change has happened during a press gesture (between ACTION_DOWN and ACTION_UP).
                break;
            case MotionEvent.ACTION_POINTER_UP:
                // A non-primary pointer has gone up.
                break;
            case MotionEvent.ACTION_UP:
                // A pressed gesture has finished.
                break;
            case MotionEvent.ACTION_CANCEL:
                // The current gesture has been aborted.
                break;
        }

        return super.onTouchEvent(event);
    }
}