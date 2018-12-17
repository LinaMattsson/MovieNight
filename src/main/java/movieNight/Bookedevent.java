package movieNight;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Bookedevent {
    String startTime;
    String endTime;

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

//    public Bookedevent(String startTime, String endTime){
//
//        this.startTime = startTime;
//        this.endTime = endTime;
//    }
//
//    public String getStartTime() {
//        return startTime;
//    }
//
//    public void setStartTime(String startTime) {
//        this.startTime = startTime;
//    }
//
//    public String getEndTime() {
//        return endTime;
//    }
//
//    public void setEndTime(String endTime) {
//        this.endTime = endTime;
//    }
}
