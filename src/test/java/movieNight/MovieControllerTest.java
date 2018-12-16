package movieNight;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.assertThat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import movieNight.controllers.*;
import movieNight.repositories.MovieRepository;
import movieNight.entities.Movie;

@RunWith(SpringRunner.class)
@DataJpaTest(showSql = false)
public class MovieControllerTest {

    // @Autowired
    // private TestEntityManager entityManager;

    private static Logger logger = LogManager.getLogger();

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TestEntityManager entityManager;

    private final String ID_STARWARS = "tt2527336";
    private final String ID_AVATAR = "tt0499549";

    @Test
    public void whenFindByName_thenReturnMovie() {

        // Can be used to add movies directly to database
        Movie mov = new Movie();
        mov.setTitle("Linas Movie");
        entityManager.persist(mov);
        entityManager.flush();
        assertEquals(1, movieRepository.count());


        MovieController mc = new MovieController(movieRepository);

        // Add Star Wars
        mc.addNewMovie(ID_STARWARS);
        mc.addNewMovie(ID_AVATAR);
        long expected = 3;
        long count = movieRepository.count();
        assertEquals("Wanted three movie", expected, count);

        mc.addNewMovie(ID_AVATAR);
        assertEquals("Wanted three movie", expected, count);

        boolean starwarsFound = false;
        boolean randomMovieFound = false;
        for (Movie movie : mc.getAllMovies()) {

            switch (movie.getTitle()) {
                case "Star Wars: The Last Jedi":
                    starwarsFound = true;
                    break;
                case "Random movie xyz":
                    randomMovieFound = true;
                    break;
                default:
                    break;
            }
        }
        assertTrue("Star Wars not found", starwarsFound);
        assertFalse("Random movie was found! Should not have been", randomMovieFound);
        assertEquals(3,movieRepository.count());
    }
}