package testMovieNight;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.security.GeneralSecurityException;

@SpringBootApplication
public class Application {

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        SpringApplication.run(Application.class, args);






        //MovieController m = new MovieController();
        //String message = m.addNewMovie("tt1856010");
        //System.out.println(message);
    }
}