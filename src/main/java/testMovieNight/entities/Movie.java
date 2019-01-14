package testMovieNight.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;

@Entity // This tells Hibernate to make a table out of this class
@Table(name="movie")
public class Movie {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer i;
    @JsonProperty("imdbID")
    private String id;
    @JsonProperty("Title")
    private String title;
    @JsonProperty("Year")
    private String year;
    @JsonProperty("Plot")
    private String plot;
    @JsonProperty("Genre")
    private String genre;
    @JsonProperty("Runtime")
    private String runtime;
    @JsonProperty("imdbRating")
    private String rating;
    @JsonProperty("Language")
    private String language;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
