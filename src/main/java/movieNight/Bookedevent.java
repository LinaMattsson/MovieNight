package movieNight;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Bookedevent {
    private String startTime;
    private String endTime;

    public Bookedevent(String start, String end){
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


    public String getStartTime() {
        return startTime;
   }

    public String getEndTime() {
        return endTime;
    }
}
