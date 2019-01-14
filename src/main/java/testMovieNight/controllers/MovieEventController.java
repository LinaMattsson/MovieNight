package testMovieNight.controllers;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import testMovieNight.CalenderConnection;
import testMovieNight.entities.MovieEvent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class MovieEventController {
    @Autowired
    CalenderConnection c;

    public MovieEventController(){
    }

    @GetMapping(path="/setMovieTitleToEvent")
    public EventDateTime setMovieTitleToEvent(@RequestParam String movieTitle){
        EventDateTime eventDate;
        MovieEvent.getInstance().setMovieTitle(movieTitle);
        eventDate = MovieEvent.getInstance().getEventStart();
        return eventDate;
    }


    @GetMapping(path="/setDateTimeToEvent")
    public String setDateTimeToEvent(@RequestParam String eventStart){
        //LocalDateTime time = LocalDateTime.now();
        DateTime start = new DateTime(eventStart);
        DateTime eventEnd = c.plusHours(start, 4);
        EventDateTime Estart = c.convertDateTimeToEventDateTime(start);
        EventDateTime Eend = c.convertDateTimeToEventDateTime(eventEnd);
        MovieEvent.getInstance().setEventStart(Estart);
        MovieEvent.getInstance().setEventEnd(Eend);
        String movieTitle = MovieEvent.getInstance().getMovieTitle();
        return movieTitle;
    }

    @RequestMapping(value = "/bookEvent", method = RequestMethod.GET)
    public boolean bookEvent(){

        boolean didBook=false;
        List<Calendar> calendars = c.getCalendars();
        Event event = new Event();
        event.setStart(MovieEvent.getInstance().getEventStart());
        event.setEnd(MovieEvent.getInstance().getEventEnd());
        for(Calendar calendar : calendars) {
            try {
                calendar.events().insert("primary", event).execute(); //????? hittade på själv typ
                didBook = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MovieEvent.getInstance().setMovieTitle(null);
        MovieEvent.getInstance().setEventStart(null);
        MovieEvent.getInstance().setEventEnd(null);
        return didBook;
    }

}
