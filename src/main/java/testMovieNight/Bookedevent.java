package testMovieNight;

import com.google.api.client.util.DateTime;

public class Bookedevent {
    private DateTime startTime;
    private DateTime endTime;

    public Bookedevent(DateTime start, DateTime end){
        this.startTime = start;
        this.endTime = end;
    }
    @Override
    public String toString() {
        return "Bookedevent{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }


    public DateTime getStartTime() {
        return startTime;
   }

    public DateTime getEndTime() {
        return endTime;
    }
}
