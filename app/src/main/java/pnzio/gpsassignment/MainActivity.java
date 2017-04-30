package pnzio.gpsassignment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<UserActivityDetails> userActivities;
    public boolean working = false;
    private LocationManager lm;
    private Location loc = null;
    private Double distanceTraveled = 0.0;

    private TextView currentSpeed,averageSpeed,distance;
    private Button startButton,finishButton,resetButton;
    private Chronometer cm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userActivities = new ArrayList<UserActivityDetails>();
        currentSpeed = (TextView)findViewById(R.id.speedView);
        averageSpeed = (TextView)findViewById(R.id.averageSpeedView);
        distance = (TextView)findViewById(R.id.distance);
        cm = (Chronometer) findViewById(R.id.cmtime);
        startButton = (Button)findViewById(R.id.btn_start);
        finishButton = (Button)findViewById(R.id.btn_finish);
        resetButton = (Button)findViewById(R.id.btn_reset);

        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        final LocationListener ll = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location != null){

                    UserActivityDetails user = new UserActivityDetails();
                    user.setLongt(location.getLongitude());
                    user.setLangt(location.getLatitude());

                    // Speed calculation
                    Double activitySpeed = (double) location.getSpeed();
                    user.setSpeed(activitySpeed*3.6);
                    Double s = activitySpeed*3.6;
                    double tempSpeed = userActivities.isEmpty() ? s : getAverageSpeed();

                    // Distance calculation
                    if(loc == null){
                        loc = location;
                    }
                    double tempLocation = loc.distanceTo(location);
                    distanceTraveled = distanceTraveled + tempLocation;
                    user.setDistanceTraveled(distanceTraveled);
                    String shownDistance = (distanceTraveled<1000) ?
                            (int) Math.round(distanceTraveled) + " meters" :
                             String.format("%.2f", distanceTraveled/1000) + " km";



                    // Saving time in seconds, (to be user in graphs)
                    long elapsedMillis = SystemClock.elapsedRealtime() - cm.getBase();
                    long timePassedInSeconds = elapsedMillis/1000;
                    user.setTime(timePassedInSeconds);

                    currentSpeed.setText("Current speed : " + (int) Math.round(s) + " km/hour");
                    averageSpeed.setText("Average speed: " + (int) Math.round(tempSpeed) + " km/hour");
                    distance.setText("Distance : " + shownDistance);

                    loc = location;
                    userActivities.add(user);
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {
                resetTextFields();
            }
        };
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cm.setBase(SystemClock.elapsedRealtime());
                cm.start();
                startTracking(ll);
                working=true;
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTracking(ll);
                reset();
            }
        });
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent showDetails = new Intent(MainActivity.this,UserActivity.class);
                stopTracking(ll);
                Bundle data = new Bundle();

                if(!userActivities.isEmpty()) {
                    data.putInt("averageSpeed", (int) Math.round(getAverageSpeed()));

                    Double dist = userActivities.get(userActivities.size() - 1).getDistanceTraveled();
                    String distance = (dist < 1000) ?
                            (int) Math.round(dist) + " meters" :
                            String.format("%.2f", dist / 1000) + " km";

                    data.putString("distanceStaveled", distance);

                    long totalTime = userActivities.get(userActivities.size() - 1).getTime();
                    String userTime = "" + totalTime;
                    data.putString("totalUserTime", userTime);

                    ArrayList<Integer> graphData = getGraphInfo();
                    data.putIntegerArrayList("graphArray",graphData);
                }

                reset();
                showDetails.putExtras(data);
                startActivity(showDetails);
            }
        });

    }
    public void reset(){
        working=false;
        resetTextFields();
        cm.stop();
        userActivities.removeAll(userActivities);
        distanceTraveled = 0.0;
        loc = null;
    }
    public void resetTextFields(){
        currentSpeed.setText(R.string.currentSpeed);
        averageSpeed.setText(R.string.averageSpeed);
        distance.setText(R.string.distance);
        cm.setText("00:00");
    }
    public double getAverageSpeed(){
        double result = 0;
        for(int i=0;i<userActivities.size();i++){
            result = result + userActivities.get(i).getSpeed();
        }
        return result/userActivities.size();
    }
    public void startTracking(LocationListener temp){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String PERMISSIONS_REQUIRED[] = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
            ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, 1);
        }
        else{
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,5,temp);
        }

    }

    public void stopTracking(LocationListener temp){
        lm.removeUpdates(temp);
    }

    public ArrayList<Integer> getGraphInfo(){
        ArrayList<Integer> times = new ArrayList<>();
        double distanceCheck = 1000;
        for(int i=0;i<userActivities.size();i++){
            if(userActivities.get(i).getDistanceTraveled()>=distanceCheck){
                distanceCheck = distanceCheck + 1000;
                times.add((int) Math.round(userActivities.get(i).getTime()));
            }
        }
        return times;
    }

    public void onResume() {
        super.onResume();
    }
}
