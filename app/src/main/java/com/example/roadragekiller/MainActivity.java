package com.example.roadragekiller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


//This is for the HERE maps API
import com.here.android.mpa.common.ApplicationContext;
import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.MapEngine;
import com.here.android.mpa.common.MatchedGeoPosition;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.prefetcher.MapDataPrefetcher;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

public class MainActivity extends Activity implements LocationListener {

    private static final  int REQUEST_CODE_ASK_PERMISSIONS=1;

    //This is for requesting permissions
    private static final String[] RUNTIME_PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE
    };
    int runtime_request =1; //used in .requestPermissions()   for error message
    LocationManager lm1;
    private boolean fetchingDataInProgress = false;
    boolean sdkINIT=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //permission check & request  + init Location Manager
        if(hasPermissions(this,RUNTIME_PERMISSIONS)){
            Log.d("RoadRageKiller","Runtime permissions already granted");
            lm1 = initLocationManager();
            initSDK();
        }else{
            Log.d("RoadRageKiller","Runtime permissions not granted");
            ActivityCompat.requestPermissions(this,RUNTIME_PERMISSIONS,runtime_request);
        }

        final TextView text_meters = findViewById(R.id.userSpeed);
        final TextView text_mph = findViewById(R.id.userSpeed_mph);
        final TextView text_speed_limit = findViewById(R.id.speedLimitText);
        final TextView api_status =findViewById(R.id.api_status);
        final TextView street_info=findViewById(R.id.streetData);

        final Button settingsButton = findViewById(R.id.button_settings);
        final Button startButton = findViewById(R.id.startButton);
        final Button stopButton = findViewById(R.id.stopButton);
        final Button stats = findViewById(R.id.button_stats);

        SettingsActivity.metric=false;
        text_meters.setVisibility(View.INVISIBLE);
        text_mph.setVisibility(View.INVISIBLE);
        text_speed_limit.setVisibility(View.INVISIBLE);
        street_info.setVisibility(View.INVISIBLE);
        api_status.setTextColor(Color.parseColor("#FF0000"));

        //Start button UI element
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // When user clicks start button do this
            public void onClick(View view) {
                Log.d("RoadRageKiller","Clicked Start Button");
                startLocationManager(lm1);//Android location manager
                if(sdkINIT) {
                    startListeners();//HERE map position manager
                    startPositioningManager();
                    api_status.setText("API service: running");
                    api_status.setTextColor(Color.parseColor("#31f505"));
                }else {
                    initSDK();
                    Log.d("RoadRageKiller","SDK not initialized");

                }
                if (SettingsActivity.metric) {
                    text_meters.setVisibility(View.VISIBLE);
                } else {
                    text_mph.setVisibility(View.VISIBLE);
                }
                // speed pops up
                startButton.setVisibility(View.INVISIBLE);  // button goes away
                stopButton.setVisibility(View.VISIBLE);
                text_speed_limit.setVisibility(View.VISIBLE);
                onLocationChanged(null);                    // calls the current speed tracker
            }
        });

        //Stop button Ui element
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // When user clicks stop button do this
            public void onClick(View view) {
                // speed pops up
                Log.d("RoadRageKiller","Clicked Stop Button");
                if(sdkINIT) {
                    stopPositioningManager();
                    stopWatching();
                    api_status.setText("API service: not running");
                    api_status.setTextColor(Color.parseColor("#FF0000"));
                    text_speed_limit.setText("Speed Limit:");
                }else{
                    Log.d("RoadRageKiller","SDK not initialized");
                }
                text_mph.setVisibility(View.INVISIBLE);
                text_meters.setVisibility(View.INVISIBLE);
                stopButton.setVisibility(View.INVISIBLE);
                text_speed_limit.setVisibility(View.INVISIBLE);
                startButton.setVisibility(View.VISIBLE);
            }
        });

        //Settings button   MainActivity -> Settings
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            // When user clicks start button do this
            public void onClick(View view) {
                Log.d("RoadRageKiller","Clicked Settings Button");

                TextView test = findViewById(R.id.button_settings);
                text_meters.setVisibility(View.INVISIBLE);
                text_mph.setVisibility(View.INVISIBLE);
                stopButton.setVisibility(View.INVISIBLE);
                startButton.setVisibility(View.VISIBLE);
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });

        //Stats button Main -> Stats
        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("RoadRageKiller","Clicked Stats Button");
                Intent intent = new Intent(MainActivity.this,StatsActivity.class);
                startActivity(intent);
            }
        });

        //Log.d("RoadRageKiller","Calling initSDK");
    }


    //HERE API init
    private void initSDK() {
        /*
        // Set path of isolated disk cache
        String diskCacheRoot = Environment.getExternalStorageDirectory().getPath()
                + File.separator + ".isolated-here-maps";
        // Retrieve intent name from manifest
        String intentName = "";
        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            intentName = bundle.getString("INTENT_NAME");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("MainActivity",
                    "Failed to find intent name, NameNotFound: " + e.getMessage());
        }
        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(
                diskCacheRoot, intentName);
        if (!success) {
            Toast.makeText(this, "Operation 'setIsolatedDiskCacheRootPath' was not successful",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        */

        ApplicationContext appContext = new ApplicationContext(this);
        MapEngine.getInstance().init(appContext, new OnEngineInitListener() {
            @Override
            public void onEngineInitializationCompleted(Error error) {
                if (error == Error.NONE) {
                    MapEngine.getInstance().onResume();
                    startPositioningManager();
                    startNavigationManager();
                    sdkINIT=true;
                    Toast readyToaster = Toast.makeText(getApplicationContext(), "Here API initialized. Ready",Toast.LENGTH_LONG);
                    readyToaster.show();
                    Log.d("RoadRageKiller","initSDK");
                } else {
                    //handle error here
                    Log.e("RoadRagekiller", " init error: " + error + ", " + error.getDetails(), error.getThrowable());
                    Log.d("RoadRageKiller","initSDK error");
                    String errorText = error.getDetails();
                    Toast errorToaster = Toast.makeText(getApplicationContext(), errorText,Toast.LENGTH_LONG);
                    errorToaster.show();
                    sdkINIT=false;
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                for (int index = 0; index < permissions.length; index++) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {

                        /*
                         * If the user turned down the permission request in the past and chose the
                         * Don't ask again option in the permission request system dialog.
                         */
                        if (!ActivityCompat
                                .shouldShowRequestPermissionRationale(this, permissions[index])) {
                            Toast.makeText(this, "Required permission " + permissions[index]
                                            + " not granted. "
                                            + "Please go to settings and turn on for sample app",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this, "Required permission " + permissions[index]
                                    + " not granted", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                Log.d("RoadRageKiller","Location Manager Initialized + initSDK()");
                lm1 = initLocationManager();
                initSDK();
                break;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //Here api posManager
    private void startPositioningManager() {
        boolean positioningManagerStarted = PositioningManager.getInstance().start(PositioningManager.LocationMethod.GPS_NETWORK);
        if (!positioningManagerStarted) {
            //handle error here
        }
    }

    //Here api posManagerStop
    private void stopPositioningManager() {
        PositioningManager.getInstance().stop();
    }

    private void startNavigationManager() {
        NavigationManager.Error navError = NavigationManager.getInstance().startTracking();

        if (navError != NavigationManager.Error.NONE) {
            //handle error navError.toString());
        }
    }

    //Android Location Manager
    @Override
    public void onLocationChanged(Location location) {
        TextView meters = findViewById(R.id.userSpeed);
        TextView mph = findViewById(R.id.userSpeed_mph);
        DecimalFormat df2 = new DecimalFormat("#");

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


    public LocationManager initLocationManager(){
        LocationManager location = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Log.d("RoadRageKiller","Location Manager Initialized");
        return location;
    }
    public void startLocationManager(LocationManager location){
        if(location!=null) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                location.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);
                Log.d("RoadRageKiller", "Location Manager Started");
            }
        }else{
            Log.d("RoadRageKiller", "Location Manager Start error - Null");
        }
    }
    public void pauseLocationManager(LocationManager location){

}

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }
    @Override
    public void onProviderEnabled(String s) {

    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }else{
            Log.d("RoadRageKiller","OS Build Version Not Met");
        }
        return true;
    }



    @Override
    public void onProviderDisabled(String s) {

    }

    MapDataPrefetcher.Adapter prefetcherListener = new MapDataPrefetcher.Adapter() {
        @Override
        public void onStatus(int requestId, PrefetchStatus status) {
            if(status != PrefetchStatus.PREFETCH_IN_PROGRESS) {
                fetchingDataInProgress = false;
            }
        }
    };

    PositioningManager.OnPositionChangedListener positionLister = new PositioningManager.OnPositionChangedListener() {
        @Override
        public void onPositionUpdated(PositioningManager.LocationMethod locationMethod, GeoPosition geoPosition, boolean b) {
            Log.d("RoadRageKiller","Here:onPositionUpdated");

            if (PositioningManager.getInstance().getRoadElement() == null && !fetchingDataInProgress) {
                GeoBoundingBox areaAround = new GeoBoundingBox(geoPosition.getCoordinate(), 500, 500);
                MapDataPrefetcher.getInstance().fetchMapData(areaAround);
                fetchingDataInProgress = true;
            }

            if (geoPosition.isValid() && geoPosition instanceof MatchedGeoPosition) {

                MatchedGeoPosition mgp = (MatchedGeoPosition) geoPosition;

                double currentSpeed = mgp.getSpeed();
                double speedLimit = 0;
                int speedLimitMPH=0;
                if (mgp.getRoadElement() != null && currentSpeed >5) {
                    speedLimit = mgp.getRoadElement().getSpeedLimit();
                    TextView limit = findViewById(R.id.speedLimitText);
                    //TextView data = findViewById(R.id.streetData);
                    //limit.setText("Speed Limit:" + metersPerSecToMPH(speedLimit));
                    speedLimitMPH=metersPerSecToMPH(speedLimit);
                    limit.setText("Speed Limit: "+getClosestSpeedLimit(speedLimitMPH));
                }

            } else {
                //handle error
            }
        }

        @Override
        public void onPositionFixChanged(PositioningManager.LocationMethod locationMethod, PositioningManager.LocationStatus locationStatus) {

        }
    };

    public void startListeners() {
        PositioningManager.getInstance().addListener(new WeakReference<>(positionLister));
        MapDataPrefetcher.getInstance().addListener(prefetcherListener);
    }
    public void stopWatching() {
        PositioningManager.getInstance().removeListener(positionLister);
        MapDataPrefetcher.getInstance().removeListener(prefetcherListener);
    }

    public int metersPerSecToMPH(double speed){
        double temp_speed = (int) speed * 2.23694;
        return (int)temp_speed;
    }
    public int getClosestSpeedLimit(int speed){
        return (speed + 5) / 5 * 5;
    }
    public int metersPerSecToKPH(double speed){
        return 1;
    }
}

