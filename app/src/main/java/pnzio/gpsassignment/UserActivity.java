package pnzio.gpsassignment;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Alexander on 29/04/2017.
 */

public class UserActivity extends Activity {
    Button backbtn;
    TextView speedView,distanceView,timeView,graphTextView;
    ArrayList<Integer> userKmInfo;
    GraphView gv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details);
        backbtn = (Button)findViewById(R.id.goBack);
        speedView = (TextView)findViewById(R.id.userDetailsSpeed);
        distanceView = (TextView)findViewById(R.id.userDetailsDistance);
        timeView = (TextView)findViewById(R.id.userDetailsTime);
        graphTextView = (TextView)findViewById(R.id.graphTextView);
        gv = (GraphView)findViewById(R.id.barChart);

        Bundle data = getIntent().getExtras();
        int speed = data.getInt("averageSpeed");
        String dist = data.getString("distanceStaveled");
        String time = data.getString("totalUserTime");
        userKmInfo = data.getIntegerArrayList("graphArray");

        if(userKmInfo != null) {
            Integer[] dataArray = (Integer[]) userKmInfo.toArray(new Integer[userKmInfo.size()]);
            gv.setCoordinates(dataArray);
        }
        else{
            userKmInfo = new ArrayList<>();
            Integer[] dataArray = (Integer[]) userKmInfo.toArray(new Integer[userKmInfo.size()]);
            gv.setCoordinates(dataArray);
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

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

