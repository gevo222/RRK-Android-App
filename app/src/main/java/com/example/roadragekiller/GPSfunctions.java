package com.example.roadragekiller;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.prefetcher.MapDataPrefetcher;

import java.lang.ref.WeakReference;

public interface GPSfunctions {

    public void startListeners();

    public void stopWatching();

    public int metersPerSecToMPH(double speed);

    public int getClosestSpeedLimit(int speed);

    public int metersPerSecToKPH(double speed);

    public void startPositioningManager();

    public void stopPositioningManager();

    public void startNavigationManager();

    public void onStatusChanged(String s, int i, Bundle bundle);

    public void onProviderEnabled(String s);

    public void onProviderDisabled(String s);

    public void onLocationChanged(Location location);

    public LocationManager initLocationManager();

    public void startLocationManager(LocationManager location);

    public void initSDK();

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);


}
