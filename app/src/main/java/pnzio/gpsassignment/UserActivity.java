package pnzio.gpsassignment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Alexander on 29/04/2017.
 *
 * Class that displays a summary of users Activity
 */

public class UserActivity extends Activity {
    // Variables, Text Fields and a Button
    Button backbtn;
    TextView speedView,distanceView,timeView,graphTextView;
    GraphView gv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details);

        // Initialising Views and a button
        backbtn = (Button)findViewById(R.id.goBack);
        speedView = (TextView)findViewById(R.id.userDetailsSpeed);
        distanceView = (TextView)findViewById(R.id.userDetailsDistance);
        timeView = (TextView)findViewById(R.id.userDetailsTime);
        graphTextView = (TextView)findViewById(R.id.graphTextView);
        gv = (GraphView)findViewById(R.id.barChart);

        // Initialising variables
        Bundle data = getIntent().getExtras();
        int speed = data.getInt("averageSpeed");
        String dist = data.getString("distanceStaveled");
        String time = data.getString("totalUserTime");
        int[] userKmInfo = data.getIntArray("graphArray");

        /*
        If data.getIntArray returns nothing, create an empty array to prevent any null
        pointers in the graph custom view later on. Only happens if a user presses Finish button
        in MainActivity without actually recording any activity before that.

        If list is valid, then an array is passed to the Custom View
        */
        if(userKmInfo != null) {
            gv.setCoordinates(userKmInfo);
        }
        else{
            int[] temp = new int[0];
            gv.setCoordinates(temp);
        }

        if(speed == 0)
            speedView.setText(R.string.averageSpeed);
        else
            speedView.setText("Average user speed : " + speed);


        if(dist != null)
            distanceView.setText("Distance : " + dist);
        else
            distanceView.setText(R.string.distance);

        if(time != null)
            timeView.setText("Total time is: " + time + " seconds");
        else
            timeView.setText("Total time is: 00:00");


        /* A listener added to a button, stops current activity and
        brings a user back to the MainActivity
        */
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
