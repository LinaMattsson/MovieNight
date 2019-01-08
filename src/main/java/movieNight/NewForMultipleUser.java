package movieNight;
//
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.util.DateTime;
//import com.google.api.services.calendar.Calendar;
//import com.google.api.services.calendar.model.Event;
//import com.google.api.services.calendar.model.Events;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//import java.util.List;
//
//@RestController
//public class NewForMultipleUser {
//    //private final String CLIENT_ID= "718214363368-2631skuo8n47pdkt5d7ftrojjpaj0vhb.apps.googleusercontent.com";
//    private final String CLIENT_ID= "1007447573383-kl38dafa0mmeo02ppebb9qag1cqujqa5.apps.googleusercontent.com";
//    //private final String CLIENT_SECRET= "XTx7zpBSuc_ueBuD1ShmWBo0";
//    private final String CLIENT_SECRET= "WJ3stvtgYAzPlq0oxSdzqj0V";
//
//    @RequestMapping(value = "/storeauthcode", method = RequestMethod.POST)
//    public String storeauthcode(@RequestBody String code, @RequestHeader("X-Requested-With") String encoding) {
//        if (encoding == null || encoding.isEmpty()) {
//            // Without the `X-Requested-With` header, this request could be forged. Aborts.
//            return "Error, wrong headers";
//        }
//
//        GoogleTokenResponse tokenResponse = null;
//        try {
//            tokenResponse = new GoogleAuthorizationCodeTokenRequest(
//                    new NetHttpTransport(),
//                    JacksonFactory.getDefaultInstance(),
//                    "https://www.googleapis.com/oauth2/v4/token",
//                    CLIENT_ID,
//                    CLIENT_SECRET,
//                    code,
//                    // nodehill.com blog auto-converts non https-strings to https, thus the concatenation.
//                    "http://localhost:8080")
//                    .execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // Store these 3in your DB
//        String accessToken = tokenResponse.getAccessToken();
//        String refreshToken = tokenResponse.getRefreshToken();
//        Long expiresAt = System.currentTimeMillis() + (tokenResponse.getExpiresInSeconds() * 1000);
//
//        // Debug purpose only
//        System.out.println("accessToken: " + accessToken);
//        System.out.println("refreshToken: " + refreshToken);
//        System.out.println("expiresAt: " + expiresAt);
//
//
//        // Get profile info from ID token (Obtained at the last step of OAuth2)
//        GoogleIdToken idToken = null;
//        try {
//            idToken = tokenResponse.parseIdToken();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        GoogleIdToken.Payload payload = idToken.getPayload();
//
//        // Use THIS ID as a key to identify a google user-account.
//        String userId = payload.getSubject();
//
//        String email = payload.getEmail();
//        boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
//        String name = (String) payload.get("name");
//        String pictureUrl = (String) payload.get("picture");
//        String locale = (String) payload.get("locale");
//        String familyName = (String) payload.get("family_name");
//        String givenName = (String) payload.get("given_name");
//
//        // Debugging purposes, should probably be stored in the database instead (At least "givenName").
//        System.out.println("userId: " + userId);
//        System.out.println("email: " + email);
//        System.out.println("emailVerified: " + emailVerified);
//        System.out.println("name: " + name);
//        System.out.println("pictureUrl: " + pictureUrl);
//        System.out.println("locale: " + locale);
//        System.out.println("familyName: " + familyName);
//        System.out.println("givenName: " + givenName);
//
//
//
//        // Use an accessToken previously gotten to call Google's API
//        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
//        Calendar calendar =
//                new Calendar.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
//                        .setApplicationName("Test")
//                        .build();
//
///*
//  List the next 10 events from the primary calendar.
//    Instead of printing these with System out, you should ofcourse store them in a database or in-memory variable to use for your application.
//{1}
//    The most important parts are:
//    event.getSummary()             // Title of calendar event
//    event.getStart().getDateTime() // Start-time of event
//    event.getEnd().getDateTime()   // Start-time of event
//    event.getStart().getDate()     // Start-date (without time) of event
//    event.getEnd().getDate()       // End-date (without time) of event
//{1}
//    For more methods and properties, see: Google Calendar Documentation.
//*/
//        DateTime now = new DateTime(System.currentTimeMillis());
//        Events events = null;
//        try {
//            events = calendar.events().list("primary")
//                    .setMaxResults(10)
//                    .setTimeMin(now)
//                    .setOrderBy("startTime")
//                    .setSingleEvents(true)
//                    .execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        List<Event> items = events.getItems();
//        if (items.isEmpty()) {
//            System.out.println("No upcoming events found.");
//        } else {
//            System.out.println("Upcoming events");
//            for (Event event : items) {
//                DateTime start = event.getStart().getDateTime();
//                if (start == null) { // If it's an all-day-event - store the date instead
//                    start = event.getStart().getDate();
//                }
//                DateTime end = event.getEnd().getDateTime();
//                if (end == null) { // If it's an all-day-event - store the date instead
//                    end = event.getStart().getDate();
//                }
//                System.out.printf("%s (%s) -> (%s)\n", event.getSummary(), start, end);
//            }
//        }
//        return "OK";
//    }
//}


import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
//import entities.User;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
public class NewForMultipleUser {

    //@Autowired
    //private UserRepository userRepository;

    //final String CLIENT_ID = "1006990252029-qs8flqkermtsguihvknj7aitsa1klcqa.apps.googleusercontent.com";
    private final String CLIENT_ID= "1007447573383-kl38dafa0mmeo02ppebb9qag1cqujqa5.apps.googleusercontent.com";
    //final String CLIENT_SECRET = "EpBdHHEGhEb1Gn99EHcU014U";
    private final String CLIENT_SECRET= "WJ3stvtgYAzPlq0oxSdzqj0V";


    @RequestMapping(value = "/storeauthcode", method = RequestMethod.POST)
    public String storeauthcode(@RequestBody String code, @RequestHeader("X-Requested-With") String encoding) {
        if (encoding == null || encoding.isEmpty()) {
            // Without the `X-Requested-With` header, this request could be forged. Aborts.
            return "Error, wrong headers";
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


        /* Kod stycke 2 */ ////////////////////////////////////////////////////////

        // Get profile info from ID token (Obtained at the last step of OAuth2)
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

        // Debugging purposes, should probably be stored in the database instead (At least "givenName").
        System.out.println("userId: " + userId);
        System.out.println("email: " + email);
        System.out.println("emailVerified: " + emailVerified);
        System.out.println("name: " + name);
        System.out.println("pictureUrl: " + pictureUrl);
        System.out.println("locale: " + locale);
        System.out.println("familyName: " + familyName);
        System.out.println("givenName: " + givenName);


        //For att kunna se vilken RefreshToken som tillhor vilken User
       // String loggedInUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        //userRepository.save(new User(accessToken, refreshToken, expiresAt, loggedInUsername));


        /* Google Calendar - Kod stycke 3 */ ////////////////////////////////////////////////////////

        // Use an accessToken previously gotten to call Google's API
//        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);




        /*
          List the next 10 events from the primary calendar.
            Instead of printing these with System out, you should ofcourse store them in a database or in-memory variable to use for your application.
        {1}
            The most important parts are:
            event.getSummary()             // Title of calendar event
            event.getStart().getDateTime() // Start-time of event
            event.getEnd().getDateTime()   // Start-time of event
            event.getStart().getDate()     // Start-date (without time) of event
            event.getEnd().getDate()       // End-date (without time) of event
        {1}
            For more methods and properties, see: Google Calendar Documentation.
        */

//        DateTime now = new DateTime(System.currentTimeMillis());
//        Events events = null;
//        try {
//            events = calendar.events().list("primary")
//                    .setMaxResults(10)
//                    .setTimeMin(now)
//                    .setOrderBy("startTime")
//                    .setSingleEvents(true)
//                    .execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        List<Event> items = events.getItems();
//
//        if (items.isEmpty()) {
//            System.out.println("No upcoming events found.");
//        } else {
//            System.out.println("Upcoming events");
//            for (Event event : items) {
//                DateTime start = event.getStart().getDateTime();
//                if (start == null) { // If it's an all-day-event - store the date instead
//                    start = event.getStart().getDate();
//                }
//                DateTime end = event.getEnd().getDateTime();
//                if (end == null) { // If it's an all-day-event - store the date instead
//                    end = event.getStart().getDate();
//                }
//                System.out.printf("%s (%s) -> (%s)\n", event.getSummary(), start, end);
//            }
//        }


        return "OK";
    }


    private GoogleCredential getRefreshedCredentials(String refreshCode) {
        try {
            GoogleTokenResponse response = new GoogleRefreshTokenRequest(
                    new NetHttpTransport(),
                    JacksonFactory.getDefaultInstance(),
                    refreshCode,
                    CLIENT_ID,
                    CLIENT_SECRET )
                    .execute();

            return new GoogleCredential().setAccessToken(response.getAccessToken());
        }
        catch( Exception ex ){
            ex.printStackTrace();
            return null;
        }
    }


//    public String getNewAccessToken(refreshToken) {
//        ClientLogin.Response r = GoogleGetNewAccessToken(refreshToken);
//        return r.getAccessToken();
//    }
//
//    AccessToken accessToken = getNewAccessToken(refreshToken);


//    @RequestMapping(value = "/freeTimes", method = RequestMethod.GET)
//    public void getFreeTimes() {
//
//        List<Event> allEvents = new ArrayList<>();
//        for (User user : userRepository.findAll()) {
//
//            Long expiredTime = user.getExpiresAt();
//            Long currentTime = System.currentTimeMillis();
//
//            GoogleCredential cred;
//
//            if( expiredTime < currentTime ) {
//                cred = getRefreshedCredentials(user.getRefreshToken());
//                user.setAccessToken(cred.getAccessToken());
//                user.setExpiresAt(cred.getExpiresInSeconds() * 1000);
//                userRepository.save(user);
//            } else {
//                cred = new GoogleCredential().setAccessToken(user.getAccessToken());
//            }
//
//            Calendar calendar = getCalendar(cred);
//
//
//            DateTime now = new DateTime(System.currentTimeMillis());
//            Events events = null;
//
//            try {
//                events = calendar.events().list("primary")
//                        .setMaxResults(10)
//                        .setTimeMin(now)
//                        .setOrderBy("startTime")
//                        .setSingleEvents(true)
//                        .execute();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            List<Event> items = events.getItems();
//
//
//            if (items.isEmpty()) {
//                System.out.println("No upcoming events found.");
//            } else {
//                System.out.println("Upcoming events");
//                for (Event event : items) {
//                    DateTime start = event.getStart().getDateTime();
//                    if (start == null) { // If it's an all-day-event - store the date instead
//                        start = event.getStart().getDate();
//                    }
//                    DateTime end = event.getEnd().getDateTime();
//                    if (end == null) { // If it's an all-day-event - store the date instead
//                        end = event.getStart().getDate();
//                    }
//                    System.out.printf("%s (%s) -> (%s)\n", event.getSummary(), start, end);
//                }
//            }
//
//
//            // Pusha upp alla Events i listan
//
//            // Jämföra lediga tider med händelserna
//
//        }
//    }

    public Calendar getCalendar(GoogleCredential credential) {
        return new Calendar.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName("Movie Nights")
                .build();
    }

}