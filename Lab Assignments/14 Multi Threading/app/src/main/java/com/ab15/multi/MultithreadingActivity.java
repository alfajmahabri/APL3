package com.ab15.multi;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MultithreadingActivity extends AppCompatActivity {

    TextView tvResult, tvProgress;
    Button btnStartThread, btnProgressThread;
    ProgressBar progressBar;

    Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multithreading);

        tvResult = findViewById(R.id.tvResult);
        tvProgress = findViewById(R.id.tvProgress);
        btnStartThread = findViewById(R.id.btnStartThread);
        btnProgressThread = findViewById(R.id.btnProgressThread);
        progressBar = findViewById(R.id.progressBar);


        btnStartThread.setOnClickListener(v -> {
            tvResult.setText("Thread Started...");

            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                handler.post(() ->
                        tvResult.setText("Task Completed!")
                );

            }).start();
        });

        btnProgressThread.setOnClickListener(v -> {

            new Thread(() -> {
                for (int i = 0; i <= 100; i++) {
                    int progress = i;

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    handler.post(() -> {
                        progressBar.setProgress(progress);
                        tvProgress.setText("Progress: " + progress + "%");
                    });
                }
            }).start();
        });
    }
}