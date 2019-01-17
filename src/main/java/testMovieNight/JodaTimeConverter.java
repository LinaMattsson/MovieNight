package testMovieNight;


import org.joda.time.DateTime;

public class JodaTimeConverter {
    public  JodaTimeConverter(){
    }
  public boolean hasExpired(long expiresAt, long compareTo){
      DateTime expire = new DateTime(expiresAt);
      DateTime current = new DateTime(compareTo);
      return current.isAfter(expire);
  }
}
