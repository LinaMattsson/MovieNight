package movieNight;


import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class CalenderConnection {
    private List<String> calenderIds = new ArrayList<>();
    List<Bookedevent> bookedEvents = new ArrayList<>();


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

        System.out.println(freeResp.toPrettyString()+ "detta är fint");
        //System.out.println(freeResp);

        ObjectMapper mapper = new ObjectMapper();


        JsonObject allCalendars = new JsonParser().parse(freeResp.toPrettyString()).getAsJsonObject();
        for (int i = 0; i <calenderIds.size() ; i++) {
            JsonObject calender = allCalendars.getAsJsonObject("calendars").get(calenderIds.get(i)).getAsJsonObject();
            JsonArray busyList = calender.getAsJsonArray("busy");
            for (int j = 0; j < busyList.size(); j++) {
                JsonObject tempEvent = busyList.get(j).getAsJsonObject();
                String start = tempEvent.get("start").getAsString();
                String end = tempEvent.get("end").getAsString();
                bookedEvents.add(new Bookedevent(start, end));
                System.out.println("Start: " + start + " End: " + end);
            }
        }
        boolean ledigdag = checkIfGivenDateIsFree("2018-12-20T00:05", "2018-12-23T02:03");
        boolean ledigdag2 = checkIfGivenDateIsFree("2018-12-19T01:00", "2018-12-19T03:00");
        System.out.println("Är dagen ledig?: "+ ledigdag);
        System.out.println("Är dagen ledig?: "+ ledigdag2);

//        calenderIds.forEach(id -> {
//            try {
//                service.events().insert(id, event("Middag Ny", "spagetti", 2018, 12,16, 17,00, 2018,12,16,20,30)).execute();
//                //System.out.println(id);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
        //service.events().insert("primary", event()).execute();
    }

    public boolean checkIfGivenDateIsFree(String checkDateTimeStart, String checkDateTimeEnd){
        for (int i = 0; i <bookedEvents.size() ; i++) {
            String startDateTime = bookedEvents.get(i).getStartTime().toString();
            String endDateTime = bookedEvents.get(i).getEndTime().toString();
            LocalDateTime startTimeDate = LocalDateTime.parse(startDateTime.substring(0,16));
            LocalDateTime endTimeDate = LocalDateTime.parse(endDateTime.substring(0,16));
            LocalDateTime requestStart = LocalDateTime.parse(checkDateTimeStart);
            LocalDateTime requestEnd = LocalDateTime.parse(checkDateTimeEnd);

            //check if there are an event thats going on over more then just the day you will book
            if(startTimeDate.isAfter(requestStart)&&endTimeDate.isBefore(requestEnd)){
                return false;
            }
            else if(startTimeDate.isBefore(requestStart) && endTimeDate.isAfter(requestStart)|| startTimeDate.isBefore(requestEnd) && endTimeDate.isAfter(requestEnd)){
                return false;
            }
        }
        return true;
    }

        public static Event event(String summary, String description, Integer startYear, Integer startMonth, Integer startDay, Integer startHour, Integer startMinute, Integer endYear, Integer endMonth, Integer endDay, Integer endHour, Integer endMinute){
            ZoneId timeZoneId = ZoneId.of("CET");

            Instant now = LocalDateTime.of(startYear, startMonth, startDay, startHour, startMinute).atZone(timeZoneId).toInstant();
            DateTime now2 = new DateTime(Date.from(now));
            Instant end = LocalDateTime.of(endYear, endMonth, endDay, endHour, endMinute).atZone(timeZoneId).toInstant();

//            System.out.println(end.atZone(timeZoneId));
//            System.out.println(end);

            DateTime end2 = new DateTime(Date.from(end));
            Event event = new Event();

            event.setStart(new EventDateTime().setDateTime(now2));
            // event.setStart(new EventDateTime().setDate(new DateTime(Date.from(now).)));
            event.setEnd(new EventDateTime().setDateTime(end2));
            event.setDescription(description);
            event.setSummary(summary);
            return event;

    }

    public void addCalender(String calenderId) {
        calenderIds.add(calenderId);
    }
}
