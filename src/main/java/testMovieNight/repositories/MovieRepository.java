package testMovieNight.repositories;
import testMovieNight.entities.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface MovieRepository extends JpaRepository<Movie, Integer> {
    Movie findById(String id);
}