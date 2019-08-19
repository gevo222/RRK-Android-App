package com.example.roadragekiller;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StatsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);
        TextView userRating = findViewById(R.id.userRatingText);
        TextView warnings = findViewById(R.id.warningsText);
        TextView topSpeed = findViewById(R.id.topSpeedText);
        String currentRating = MainActivity.setUserRating(MainActivity.warningTimePercentage);
        TextView avgSpeed = findViewById(R.id.avgSpeedText);

        userRating.setText(currentRating);
        warnings.setText(MainActivity.df2.format(MainActivity.warningTimePercentage * 100) + "%");
        if (SettingsActivity.metric) {
            topSpeed.setText(MainActivity.df2.format(MainActivity.topSpeed * 3.6) + " kmh");
            avgSpeed.setText(MainActivity.df2.format(MainActivity.avgSpeed * 3.6) + " kmh");
        }
        else {
            topSpeed.setText(MainActivity.df2.format(MainActivity.topSpeed * 2.23694) + " mph");
            avgSpeed.setText(MainActivity.df2.format(MainActivity.avgSpeed * 2.23694) + " mph");
        }

    }
}
