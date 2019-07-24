package com.example.roadragekiller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

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


        // Start button
        final Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // When user clicks start button do this
            public void onClick(View view) {

                TextView test = findViewById(R.id.userSpeed);


                test.setVisibility(View.VISIBLE);           // speed pops up
                startButton.setVisibility(View.INVISIBLE);  // button goes away

                onLocationChanged(null);                    // calls the current speed tracker
            }
        });



    }

    @Override
    public void onLocationChanged(Location location) {
        TextView txt = (TextView) findViewById(R.id.userSpeed);

        if (location == null) {
            // txt.setText("-.- m/s");
        }
        else{
            float nCurrentSpeed = location.getSpeed();

            txt.setText(nCurrentSpeed + " m/s");
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

