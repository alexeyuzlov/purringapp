package com.example.purringapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.Manifest;

public class MainActivity extends AppCompatActivity {
    MediaPlayer[] mediaPlayers = new MediaPlayer[5];
    TextView timerText;
    ImageView intensityImage;

    private long touchDuration = 0;
    private int currentSound = -1;
    private int currentVibrationIntensity = -1;

    Vibrator vibrator;

    long[][] vibrationPatterns = {
        {1000, 200, 1000, 200, 1000},
        {800, 400, 800, 400, 800},
        {600, 600, 600, 600, 600},
        {400, 800, 400, 800, 400},
        {200, 1000, 200, 1000, 200}
    };


    Handler handler = new Handler();
    Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            touchDuration += 100; // Инкрементируем каждые 100 мс
            int selectedSound = getSelectedSound(touchDuration); // Выбираем звук

            playPurr(touchDuration); // Проигрываем звук
            updateUI(touchDuration, selectedSound + 1); // Обновляем UI
            handler.postDelayed(this, 100); // Запускаем Runnable снова через 100 мс
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.VIBRATE}, 1);
        }

        for (int i = 0; i < 5; i++) {
            final MediaPlayer mediaPlayer = MediaPlayer.create(this, getResources().getIdentifier("_00" + (i + 1), "raw", getPackageName()));
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayers[i] = mediaPlayer;
        }

        timerText = findViewById(R.id.timerText);
        intensityImage = findViewById(R.id.intensityImage);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        Button stopButton = findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onStopButtonPressed();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        onStopButtonPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                handler.post(updateTask); // Запускаем обработчик
                break;
            case MotionEvent.ACTION_UP:
                handler.removeCallbacks(updateTask); // Останавливаем обработчик
                touchDuration = 0; // Сбрасываем длительность
                break;
        }
        return super.onTouchEvent(event);
    }

    public void updateUI(long touchDuration, long intensityLevel) {
        timerText.setText(touchDuration + "мс");

        int drawableResourceId = getResources().getIdentifier("_00" + intensityLevel, "drawable", getPackageName());
        intensityImage.setImageResource(drawableResourceId);
    }

    public void playPurr(long touchDuration) {
        try {
            int selectedSound = getSelectedSound(touchDuration);

            if (currentVibrationIntensity != selectedSound) {
                currentVibrationIntensity = selectedSound;

                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(vibrationPatterns[selectedSound], 0);
                }
            }

            if (currentSound != selectedSound) {
                if (currentSound != -1 && mediaPlayers[currentSound].isPlaying()) {
                    mediaPlayers[currentSound].stop();
                    mediaPlayers[currentSound].prepare();
                }

                currentSound = selectedSound;
                mediaPlayers[selectedSound].start();
            }

            updateUI(touchDuration, selectedSound + 1);
        } catch (Exception e) {
            timerText.setText("Ошибка воспроизведения: " + e.getMessage());
        }
    }

    public int getSelectedSound(long touchDuration) {
        int soundIndex = (int) (touchDuration / 1000);
        return Math.min(soundIndex, 4);
    }

    private void onStopButtonPressed() {
        // Останавливаем Handler и задачу
        handler.removeCallbacks(updateTask);

        if (currentSound != -1 && mediaPlayers[currentSound].isPlaying()) {
            mediaPlayers[currentSound].stop();
            try {
                mediaPlayers[currentSound].prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Останавливаем вибрацию
        if (vibrator.hasVibrator()) {
            vibrator.cancel();
        }

        // Сбрасываем длительность прикосновения
        touchDuration = 0;
        currentSound = -1;
        currentVibrationIntensity = -1;
    }
}
