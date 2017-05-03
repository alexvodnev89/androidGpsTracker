package pnzio.gpsassignment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    // Variables
    private ArrayList<UserActivityDetails> userActivities;
    private boolean working;
    private LocationManager lm;
    private Location loc;
    private Double distanceTraveled;

    private TextView currentSpeed,averageSpeed,distance;
    private Button startButton,finishButton,resetButton;
    private Chronometer cm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialising variables
        working = false;
        distanceTraveled = 0.0;
        userActivities = new ArrayList<UserActivityDetails>();
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        // Initialising Views, Buttons and Cgronometer
        currentSpeed = (TextView)findViewById(R.id.speedView);
        averageSpeed = (TextView)findViewById(R.id.averageSpeedView);
        distance = (TextView)findViewById(R.id.distance);
        cm = (Chronometer) findViewById(R.id.cmtime);
        startButton = (Button)findViewById(R.id.btn_start);
        finishButton = (Button)findViewById(R.id.btn_finish);
        resetButton = (Button)findViewById(R.id.btn_reset);

        /* Initialising Location Listener, declaring it to be final as it is then
        accessed within inner class. */
        final LocationListener ll = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location != null){

                    /* New instance of UserActivityDetails that will keep all information
                    about the current location as well as distance traveled so far and time
                    passed by
                    */
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

                    // Resetting location to current location
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

        /*
        Adding a listener to a start button. If app is running, a corresponding message
        is shown to the user. If not, start tracking movement and start Chronometer.
         */
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!working){
                    cm.setBase(SystemClock.elapsedRealtime());
                    cm.start();
                    trackingON(ll);
                    working=true;
                }else{
                    Toast.makeText(MainActivity.this,R.string.appRunning,Toast.LENGTH_LONG).show();
                }

            }
        });
        /*
        Adding a listener to a reset button. Stops tracking and resets all fields and variables.
         */
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trackingOFF(ll);
                reset();
            }
        });
        /*
        Adding a listener to a finish button. It stops tracking.
        creates a bundle with details needed to be shown in new activity,
        creates new intent, adds bundle and starts UserActivity activity.
        it also resets all fields and variables , so when a user comes back to MainActivity,
        all the data has been reset.
         */
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent showDetails = new Intent(MainActivity.this,UserActivity.class);
                trackingOFF(ll);
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

                    int[] graphData = getGraphInfo();
                    data.putIntArray("graphArray",graphData);
                }

                reset();
                showDetails.putExtras(data);
                startActivity(showDetails);
            }
        });

    }
    /*
    Resets all fields and variables. Clears a list of UserActivities
     */
    void reset(){
        working=false;
        resetTextFields();
        cm.stop();
        userActivities.removeAll(userActivities);
        distanceTraveled = 0.0;
        loc = null;
    }
    /*
    Resets all text fields.
     */
    void resetTextFields(){
        currentSpeed.setText(R.string.currentSpeed);
        averageSpeed.setText(R.string.averageSpeed);
        distance.setText(R.string.distance);
        cm.setText("00:00");
    }
    /*
    Returns average user speed. By traversing the array on UserActivities and then getting
    the average value of speed.
     */
    double getAverageSpeed(){
        double result = 0;
        for(int i=0;i<userActivities.size();i++){
            result = result + userActivities.get(i).getSpeed();
        }
        return result/userActivities.size();
    }
    /*
    Starts tracking of user movement. Permissions are checked as this is now the android standard
    and just having permissions in a manifest file isn't enough.
    If permissions are off, it prompts the Application user to enable location on the device.
    If everything is ok, it starts tracking with 1000 milliseconds or 5 meters as params.
     */
    void trackingON(LocationListener temp){
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
    /*
    Stops the tracking of user movement
     */
    void trackingOFF(LocationListener temp){
        lm.removeUpdates(temp);
    }
    /*
    Returs an array of ints. Each element in the array is the amount of time it took
    user to complete 1 km. Data gets takes from UserActivities list and then saved into an array.
    That array is then traversed to get time it took user ti complete each km.
    */
     int[] getGraphInfo(){
        ArrayList<Integer> times = new ArrayList<>();
        double distanceCheck = 1000;
        for(int i=0;i<userActivities.size();i++){
            if(userActivities.get(i).getDistanceTraveled()>=distanceCheck){
                distanceCheck = distanceCheck + 1000;
                times.add((int) Math.round(userActivities.get(i).getTime()));
            }
        }
        int[] graphValues = new int[times.size()];
        Integer[] tempGraphValues = (Integer[]) times.toArray(new Integer[times.size()]);

        for(int i=0;i<tempGraphValues.length;i++){
            if(i==0){
                Integer temp = tempGraphValues[i];
                graphValues[i] = temp;
            }
            else{
                Integer temp = tempGraphValues[i] - tempGraphValues[i-1];
                graphValues[i] = temp;
            }
        }

        return graphValues;
    }
    public void onResume() {
        super.onResume();
    }
}
