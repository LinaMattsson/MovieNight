package movieNight;

import movieNight.entities.Movie;
import movieNight.repositories.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DbConnection {
    @Autowired
    private MovieRepository movieRepository;

    public DbConnection(){
    }
    @RequestMapping(path="/lina")
    public Boolean isMovieInDb(String id){

        Movie movie = movieRepository.findById(id);
        if(movie==null){
            return false;
        }
        return true;
    }
}
