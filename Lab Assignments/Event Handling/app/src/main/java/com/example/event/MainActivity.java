package com.example.event;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private ToggleButton toggleButton;
    private TextView toggleStatus;
    private SeekBar seekBar;
    private TextView seekBarStatus;
    private Button popupButton;
    private TextView popupStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        toggleButton = findViewById(R.id.toggleButton);
        toggleStatus = findViewById(R.id.toggleStatus);
        seekBar = findViewById(R.id.seekBar);
        seekBarStatus = findViewById(R.id.seekBarStatus);
        popupButton = findViewById(R.id.popupButton);
        popupStatus = findViewById(R.id.popupStatus);

        // ToggleButton Event Handling
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String status = "Toggle Status: " + (isChecked ? "ON" : "OFF");
            toggleStatus.setText(status);
            Toast.makeText(MainActivity.this, status, Toast.LENGTH_SHORT).show();
        });

        // SeekBar Event Handling
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarStatus.setText("SeekBar Value: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optional: Action when user starts touching the bar
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(MainActivity.this, "SeekBar stopped at: " + seekBar.getProgress(), Toast.LENGTH_SHORT).show();
            }
        });

        // PopupMenu Event Handling
        popupButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, popupButton);
            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(item -> {
                String selection = "Popup Selection: " + item.getTitle();
                popupStatus.setText(selection);
                Toast.makeText(MainActivity.this, "Selected: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            });

            popup.show();
        });
    }
}