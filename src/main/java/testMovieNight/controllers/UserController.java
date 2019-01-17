package testMovieNight.controllers;

import testMovieNight.OmdbConnection;
import testMovieNight.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(path = "/Lina")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    private OmdbConnection omdbConnection = new OmdbConnection();


    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
