package pnzio.gpsassignment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Alexander on 29/04/2017.
 */

public class UserActivity extends Activity {
    Button backbtn;
    TextView speedView,distanceView,timeView;
    ArrayList<Integer> userKmInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_details);
        backbtn = (Button)findViewById(R.id.goBack);
        speedView = (TextView)findViewById(R.id.userDetailsSpeed);
        distanceView = (TextView)findViewById(R.id.userDetailsDistance);
        timeView = (TextView)findViewById(R.id.userDetailsTime);

        Bundle data = getIntent().getExtras();
        int speed = data.getInt("averageSpeed");
        String dist = data.getString("distanceStaveled");
        String time = data.getString("totalUserTime");
        userKmInfo = data.getIntegerArrayList("graphArray");

        if(userKmInfo != null) {
            Log.d("UserActivity", "fsfsffdsAAAAAAAAAAAAA" + userKmInfo.size());
        }

        speedView.setText("Average user speed : " + speed);
        distanceView.setText("Distance : " + dist);
        timeView.setText("Total time is: " + time + " seconds");









        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}

