package testMovieNight;

import testMovieNight.entities.Movie;
import testMovieNight.entities.MovieList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class OmdbConnection {


    private static final Logger log = LoggerFactory.getLogger(OmdbConnection.class);
    RestTemplate restTemplate = new RestTemplate();

    public OmdbConnection(){
    }

    public List<Movie> getMoviesByName(String search){
        MovieList response = restTemplate.getForObject(
                "http://www.omdbapi.com/?s="+search+"&apikey=6540b93c",
                MovieList.class);
        List<Movie> movies;
        try {
            movies = response.getMovies();
        }//movies.forEach(movie -> log.info(movie.toString()));
        catch (Exception e){
            movies = null;
        }
        return movies;
    }

    //Can only search for year with title
    public Movie getMoviesById(String id){
        Movie movie = restTemplate.getForObject(
                "http://www.omdbapi.com/?i="+id+"&apikey=6540b93c",
                Movie.class);
        return movie;
    }
}
