package movieNight;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CalenderConnection {
    private List<String> calenderIds = new ArrayList<>();

    private static final Logger log = LoggerFactory.getLogger(CalenderConnection.class);


    private static final String APPLICATION_NAME = "Google Calendar API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(CalendarScopes.CALENDAR); //Read only är borttaget
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = CalenderConnection.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
    public void getFreeTime(){

    }

    public void showCalender() throws GeneralSecurityException, IOException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());

        // Create items for request
        List<FreeBusyRequestItem> freeBusyItems = new ArrayList<>();

        for (String id : calenderIds) {
            FreeBusyRequestItem item = new FreeBusyRequestItem();
            item.setId(id);
            freeBusyItems.add(item);
        }

        // Create request
        FreeBusyRequest req = new FreeBusyRequest();
        req.setItems(freeBusyItems);
        req.setTimeMin(now);
        req.setTimeZone("CET");
        DateTime timeMax = new DateTime(String.valueOf(DateTime.parseRfc3339("2019-01-01T00:00:01Z")));

        req.setTimeMax(timeMax);

        FreeBusyResponse freeResp = service.freebusy().query(req).execute();

        System.out.println(freeResp.toPrettyString());

//        Events events = service.events().list("s05uo4rrlcpfdu3ukaj8hogdh4@group.calendar.google.com")
//                .setMaxResults(10)
//                .setTimeMin(now)
//                .setOrderBy("startTime")
//                .setSingleEvents(true)
//                .execute();
//        System.out.println(events);
//        List<Event> items = events.getItems();
//
//        Events events2 = service.events().list("asd503saoqvo14clp799dtenps@group.calendar.google.com")
//                .setMaxResults(10)
//                .setTimeMin(now)
//                .setOrderBy("startTime")
//                .setSingleEvents(true)
//                .execute();
//        System.out.println(events2);
//        List<Event> items2 = events2.getItems();
//
//        if (items.isEmpty()) {
//            System.out.println("No upcoming events found.");
//        } else {
//            System.out.println("Upcoming events");
//            for (Event event : items) {
//                DateTime start = event.getStart().getDateTime();
//                DateTime end = event.getEnd().getDateTime();
//                if (start == null) {
//                    start = event.getStart().getDate();
//                }
//                if (end == null) {
//                    end = event.getEnd().getDate();
//                }
//                System.out.printf("%s, start time: (%s), end time: (%s)\n", event.getSummary(), start, end);
//            }
//        }
//        if (items2.isEmpty()) {
//            System.out.println("No upcoming events found.");
//        } else {
//            System.out.println("Upcoming events");
//            for (Event event : items2) {
//                DateTime start = event.getStart().getDateTime();
//                DateTime end = event.getEnd().getDateTime();
//                if (start == null) {
//                    start = event.getStart().getDate();
//                }
//                if (end == null) {
//                    end = event.getEnd().getDate();
//                }
//                System.out.printf("%s, start time: (%s), end time: (%s)\n", event.getSummary(), start, end);
//            }
//        }
        calenderIds.forEach(id -> {
            try {
                service.events().insert(id, event()).execute();
                System.out.println(id);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        //service.events().insert("primary", event()).execute();
        System.out.println("hej2");
    }

        public static Event event(){

            Instant now = LocalDateTime.of(2018, 12, 12, 10, 10).toInstant(ZoneOffset.UTC);
            DateTime now2 = new DateTime(Date.from(now));
            Instant end = LocalDateTime.of(2018, 12, 16, 10, 10).toInstant(ZoneOffset.UTC);
            DateTime end2 = new DateTime(Date.from(end));
            Event event = new Event();
            event.setStart(new EventDateTime().setDateTime(now2));
            // event.setStart(new EventDateTime().setDate(new DateTime(Date.from(now).)));
            event.setEnd(new EventDateTime().setDateTime(end2));
            event.setDescription("Middag");
            event.setSummary("Middag");
            System.out.println("hej");
            return event;

    }

    public void addCalender(String calenderId) {
        calenderIds.add(calenderId);
    }
}
