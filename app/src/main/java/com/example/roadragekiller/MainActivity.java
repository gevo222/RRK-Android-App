package com.example.roadragekiller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.text.DecimalFormat;

public class MainActivity extends Activity implements LocationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // checks if we have location permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }

        final TextView text_meters = findViewById(R.id.userSpeed);
        final TextView text_mph = findViewById(R.id.userSpeed_mph);

        text_meters.setVisibility(View.INVISIBLE);
        text_mph.setVisibility(View.INVISIBLE);

        // Start button
        final Button startButton = findViewById(R.id.startButton);
        final Button stopButton = findViewById(R.id.stopButton);
        Button stats = findViewById(R.id.button_stats);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // When user clicks start button do this
            public void onClick(View view) {


                if (SettingsActivity.metric) {
                    text_meters.setVisibility(View.VISIBLE);
                } else {
                    text_mph.setVisibility(View.VISIBLE);
                }
                // speed pops up
                startButton.setVisibility(View.INVISIBLE);  // button goes away
                stopButton.setVisibility(View.VISIBLE);
                onLocationChanged(null);                    // calls the current speed tracker
            }
        });


        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // When user clicks stop button do this
            public void onClick(View view) {
                // speed pops up
                text_mph.setVisibility(View.INVISIBLE);
                text_meters.setVisibility(View.INVISIBLE);
                stopButton.setVisibility(View.INVISIBLE);
                startButton.setVisibility(View.VISIBLE);
            }
        });
        //SettingsActivity not created yet. Uncommented after

        final Button settingsButton = findViewById(R.id.button_settings);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // When user clicks start button do this
            public void onClick(View view) {
                TextView test = findViewById(R.id.button_settings);
                text_meters.setVisibility(View.INVISIBLE);
                text_mph.setVisibility(View.INVISIBLE);
                stopButton.setVisibility(View.INVISIBLE);
                startButton.setVisibility(View.VISIBLE);
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
                //test.setVisibility(View.VISIBLE);           // speed pops up
                //settingsButton.setVisibility(View.INVISIBLE);  // button goes away

                //onLocationChanged(null);                    // calls the current speed tracker
            }
        });

        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,StatsActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    public void onLocationChanged(Location location) {
        TextView meters = findViewById(R.id.userSpeed);
        TextView mph = findViewById(R.id.userSpeed_mph);
        DecimalFormat df2 = new DecimalFormat("#.##");

        if (location == null) {
            // txt.setText("-.- m/s");
        } else if (SettingsActivity.metric) {
            float nCurrentSpeed = location.getSpeed();

            meters.setText(df2.format(nCurrentSpeed*3.6) + " km/h");
        } else {
            float nCurrentSpeed = location.getSpeed();

            mph.setText(df2.format(nCurrentSpeed * 2.23694 )+ " mph");
        }
    }



    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {



    }


    @Override
    public void onProviderDisabled(String s) {

    }
}

