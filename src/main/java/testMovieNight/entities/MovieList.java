package testMovieNight.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MovieList {
    @JsonProperty("Search")
    private List<Movie> movies;

    public MovieList(){
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }
}
