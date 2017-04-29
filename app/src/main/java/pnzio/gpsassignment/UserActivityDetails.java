package pnzio.gpsassignment;

/**
 * Created by Alexander on 29/04/2017.
 */

public class UserActivityDetails {
    double speed, langt, longt, distanceTraveled;
    long timeInSeconds;

    void setSpeed(Double s){
        speed = s;
    }
    Double getSpeed(){
        return speed;
    }
    void setLangt(Double l){
        langt = l;
    }
    void setLongt(Double l){
        longt = l;
    }
    void setDistanceTraveled(Double d){
        distanceTraveled = d;
    }
    Double getDistanceTraveled(){
        return distanceTraveled;
    }
    void setTime(long l){
        timeInSeconds = l;
    }
    long getTime(){
        return timeInSeconds;
    }
}


