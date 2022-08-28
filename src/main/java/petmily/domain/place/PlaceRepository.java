package petmily.domain.place;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import petmily.domain.posts.Post;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query("SELECT p FROM Place p WHERE p.email = :email")
    List<Place> findByEmail(@Param("email") String email);

    @Query("SELECT p FROM Place p ORDER BY p.id DESC")
    List<Place> findAllDesc();
}
