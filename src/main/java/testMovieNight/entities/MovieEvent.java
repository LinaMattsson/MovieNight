package testMovieNight.entities;


import com.google.api.services.calendar.model.EventDateTime;

public class MovieEvent {
    private static MovieEvent movieEvent_instance = null;
    private String MovieTitle;
    private EventDateTime eventStart;
    private EventDateTime eventEnd;

    private MovieEvent()
    {
    }

    public static MovieEvent getInstance()
    {
        if (movieEvent_instance == null) {
            movieEvent_instance = new MovieEvent();
        }
        return movieEvent_instance;
    }

    public String getMovieTitle() {
        return MovieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        MovieTitle = movieTitle;
    }

    public EventDateTime getEventStart() {
        return eventStart;
    }

    public void setEventStart(EventDateTime eventStart) {
        this.eventStart = eventStart;
    }

    public EventDateTime getEventEnd() {
        return eventEnd;
    }

    public void setEventEnd(EventDateTime eventEnd) {
        this.eventEnd = eventEnd;
    }
}
