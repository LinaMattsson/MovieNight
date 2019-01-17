package testMovieNight;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import testMovieNight.entities.User;
import testMovieNight.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class CalenderConnection {

    private final String CLIENT_ID = "1007447573383-kl38dafa0mmeo02ppebb9qag1cqujqa5.apps.googleusercontent.com";
    private final String CLIENT_SECRET = "WJ3stvtgYAzPlq0oxSdzqj0V";

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/storeauthcode", method = RequestMethod.POST)
    public ResponseEntity<String> storeauthcode(@RequestBody String code, @RequestHeader("X-Requested-With") String encoding) {
        if (encoding == null || encoding.isEmpty()) {
            // Without the `X-Requested-With` header, this request could be forged. Aborts.
            return new ResponseEntity<>("Error, wrong headers", HttpStatus.BAD_REQUEST);
        }

        GoogleTokenResponse tokenResponse = null;
        try {
            tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    "https://www.googleapis.com/oauth2/v4/token",
                    CLIENT_ID,
                    CLIENT_SECRET,
                    code,
                    // nodehill.com blog auto-converts non https-strings to https, thus the concatenation.
                    "http://localhost:8080")
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Store these 3in your DB
        String accessToken = tokenResponse.getAccessToken();
        String refreshToken = tokenResponse.getRefreshToken();
        Long expiresAt = System.currentTimeMillis() + (tokenResponse.getExpiresInSeconds() * 1000);

        // Debug purpose only
        System.out.println("accessToken: " + accessToken);
        System.out.println("refreshToken: " + refreshToken);
        System.out.println("expiresAt: " + expiresAt);

        GoogleIdToken idToken = null;
        try {
            idToken = tokenResponse.parseIdToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
        GoogleIdToken.Payload payload = idToken.getPayload();

        // Use THIS ID as a key to identify a google user-account.
        String userId = payload.getSubject();

        String email = payload.getEmail();
        boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");
        String locale = (String) payload.get("locale");
        String familyName = (String) payload.get("family_name");
        String givenName = (String) payload.get("given_name");


        //User user = new User(email, name, accessToken, refreshToken, expiresAt);
        if (userRepository.findByMail(email) == null) {
            userRepository.save(new User(email, name, accessToken, refreshToken, expiresAt));
            System.out.println("saved user");
        } else System.out.println("User already in db");
        //System.out.println(user.getMail() + user.getAccessToken());


        // Debugging purposes, should probably be stored in the database instead (At least "givenName").
        System.out.println("userId: " + userId);
        System.out.println("email: " + email);
        System.out.println("emailVerified: " + emailVerified);
        System.out.println("name: " + name);
        System.out.println("pictureUrl: " + pictureUrl);
        System.out.println("locale: " + locale);
        System.out.println("familyName: " + familyName);
        System.out.println("givenName: " + givenName);

        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
        //11111 Här skickar jag efter kalendern jag skickar med credentials där senaste accessToken ska finnas. I den här metoden finns den jag fått online INTE den som finns i databasen
        Calendar calendar =
                new Calendar.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                        .setApplicationName("Movie Nights")
                        .build();

        DateTime now = new DateTime(System.currentTimeMillis());
        Events events = null;
        try {
            events = calendar.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Event> items = events.getItems();
        if (items.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Upcoming events");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) { // If it's an all-day-event - store the date instead
                    start = event.getStart().getDate();
                }
                DateTime end = event.getEnd().getDateTime();
                if (end == null) { // If it's an all-day-event - store the date instead
                    end = event.getStart().getDate();
                }
                System.out.printf("%s (%s) -> (%s)\n", event.getSummary(), start, end);
            }
        }

        return new ResponseEntity<>("OK",HttpStatus.OK);
    }


    private GoogleCredential getRefreshedCredentials(String refreshCode) {
        try {
            GoogleTokenResponse response = new GoogleRefreshTokenRequest(
                    new NetHttpTransport(), JacksonFactory.getDefaultInstance(), refreshCode, CLIENT_ID, CLIENT_SECRET)
                    .execute();

            return new GoogleCredential().setAccessToken(response.getAccessToken());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }


    public List<Bookedevent> getBusyDates() {
        List<Bookedevent> busyDates = new ArrayList<>();

        // get accesstoken, refreshtoken och mail
        List<User> users = userRepository.findAll();


        refreshIfExpired();

        for (User u : users) {
            String accessToken = u.getAccessToken();

            try {
                GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
                Calendar calendar =
                        new Calendar.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                                .setApplicationName("Movie Nights")
                                .build();

                DateTime fromNow = new DateTime(System.currentTimeMillis());
                Events events = null;
                try {
                    events = calendar.events().list("primary")
                            .setMaxResults(30)
                            .setTimeMin(fromNow)
                            .setOrderBy("startTime")
                            .setSingleEvents(true)
                            .execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                List<Event> items = events.getItems();
                if (items.isEmpty()) {
                    System.out.println("No upcoming events found.");
                } else {
                    System.out.println("Upcoming events");
                    for (Event event : items) {
                        DateTime start = event.getStart().getDateTime();
                        if (start == null) { // If it's an all-day-event - store the date instead
                            start = event.getStart().getDate();
                        }
                        DateTime end = event.getEnd().getDateTime();
                        if (end == null) { // If it's an all-day-event - store the date instead
                            end = event.getStart().getDate();
                        }
                        busyDates.add(new Bookedevent(start, end));
                        System.out.printf("%s (%s) -> (%s)\n", event.getSummary(), start, end);
                    }
                }
            } catch (Exception e) {

            }
        }
        return busyDates;
    }


    @RequestMapping(value = "/lookForFreeTime", method = RequestMethod.GET)
    public ResponseEntity<List<LocalDateTime>> getFreeDates(){
        List<Bookedevent> busyDates= getBusyDates();
        List<LocalDateTime> freeDates = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.withHour(19).withMinute(0).withSecond(0);
        LocalDateTime end = now.withHour(23).withMinute(0).withSecond(0);

        for(int i = 0; i<30; i ++) {
          start = start.plusDays(1);
          end = end.plusDays(1);
          if(checkIfGivenDateIsFree(start, end, busyDates)){
           freeDates.add(start);
          }
        }
        if (freeDates.size()==0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(freeDates, HttpStatus.OK);
    }


    public LocalDateTime convertDateTimeToLocalDateTime(DateTime dt){
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS+01:00");
            return LocalDateTime.parse(dt.toStringRfc3339(), f);
        }
        catch(Exception e) { e.printStackTrace(); return null; }
    }

    public DateTime plusHours(DateTime dtIn, int hours){
        try {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS+01:00");
            LocalDateTime ldt = LocalDateTime.parse(dtIn.toStringRfc3339(), f);
            LocalDateTime ldtPlusHours = ldt.plusHours(hours);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS+01:00");
            String formatDateTime = ldtPlusHours.format(formatter);
            DateTime dateTimeOut = new DateTime(formatDateTime);
            return dateTimeOut;
        }
        catch(Exception e) { e.printStackTrace(); return null; }
    }

    public EventDateTime convertLocalDateTimeToEventDateTime(LocalDateTime localdt){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = localdt.format(formatter);
        DateTime dt = new DateTime(formatDateTime);
        EventDateTime edt;
        edt = new EventDateTime().setTimeZone("CET").setDateTime(dt);
      return edt;
    }

    public EventDateTime convertDateTimeToEventDateTime(DateTime dt){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        EventDateTime edt;
        edt = new EventDateTime().setTimeZone("CET").setDateTime(dt);
        return edt;
    }



    public boolean checkIfGivenDateIsFree(LocalDateTime checkDateTimeStart, LocalDateTime checkDateTimeEnd, List<Bookedevent> bookedEvents){
        for (int i = 0; i <bookedEvents.size() ; i++) {
            LocalDateTime startDateTime = convertDateTimeToLocalDateTime(bookedEvents.get(i).getStartTime());
            LocalDateTime endDateTime = convertDateTimeToLocalDateTime(bookedEvents.get(i).getEndTime());
            LocalDateTime requestStart = checkDateTimeStart;
            LocalDateTime requestEnd = checkDateTimeEnd;

            //check if there are an event thats going on over more then just the day you will book
            if(startDateTime.isAfter(requestStart)&&endDateTime.isBefore(requestEnd)){
                return false;
            }
            else if(startDateTime.isBefore(requestStart) && endDateTime.isAfter(requestStart)|| startDateTime.isBefore(requestEnd) && endDateTime.isAfter(requestEnd)){
                return false;
            }
        }
        return true;
    }

    public List<Calendar> getCalendars(){
        List<User> users = userRepository.findAll();
        System.out.println("rad 343" + users);
        List<Calendar> calendars= new ArrayList<>();
        refreshIfExpired();

        for(User user: users){
        GoogleCredential credential = new GoogleCredential().setAccessToken(user.getAccessToken());
        Calendar calendar =
                new Calendar.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                        .setApplicationName("Movie Nights")
                        .build();

        calendars.add(calendar);
        }
        return calendars;
    }

    @Autowired

    public void refreshIfExpired(){
        JodaTimeConverter joda = new JodaTimeConverter();


        long now = new DateTime(System.currentTimeMillis()).getValue();
        List<User> users = userRepository.findAll();

        for (User user : users){
        if (joda.hasExpired(user.getExpirationDate(),now )) {
            //hämta ny accessToken
            GoogleCredential credential = getRefreshedCredentials(user.getRefreshToken());
            //Spara denna och en ny datetime
            userRepository.findByMail(user.getMail()).setAccessToken(credential.getAccessToken());
            userRepository.findByMail(user.getMail()).setExpirationDate(credential.getExpiresInSeconds());
        }}
    }
}
