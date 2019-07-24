package com.example.roadragekiller;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button settingsButton = findViewById(R.id.button_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // When user clicks start button do this
            public void onClick(View view) {
                TextView test = findViewById(R.id.button_settings);
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
                //test.setVisibility(View.VISIBLE);           // speed pops up
                //settingsButton.setVisibility(View.INVISIBLE);  // button goes away

                //onLocationChanged(null);                    // calls the current speed tracker
            }
        });
    }

}
