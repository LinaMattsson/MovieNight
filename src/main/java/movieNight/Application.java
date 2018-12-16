package movieNight;


import movieNight.controllers.MovieController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.security.GeneralSecurityException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        SpringApplication.run(Application.class, args);
        CalenderConnection c = new CalenderConnection();

        c.addCalender("s05uo4rrlcpfdu3ukaj8hogdh4@group.calendar.google.com");
        //c.addCalender("asd503saoqvo14clp799dtenps@group.calendar.google.com");

        c.showCalender();






        //MovieController m = new MovieController();
        //String message = m.addNewMovie("tt1856010");
        //System.out.println(message);
    }
}