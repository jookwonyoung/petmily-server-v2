package petmily.domain.walk;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import petmily.domain.posts.Post;

import java.util.List;

public interface WalkRepository extends JpaRepository<Walk, Long> {

    @Query("SELECT w FROM Walk w ORDER BY w.id DESC")
    List<Walk> findAllDesc();
    @Query("SELECT w FROM Walk w WHERE w.email = :email")
    List<Walk> findByEmail(@Param("email") String email);

    @Query("SELECT w FROM Walk w WHERE w.year = :year and  w.month = :month and w.day = :day and w.email = :email")
    List<Walk> findDateDesc(@Param("year") int year, @Param("month") int month, @Param("day") int day, @Param("email") String email);

    @Query("SELECT w FROM Walk w WHERE w.id = :id and w.year = :year and  w.month = :month and w.day = :day and w.email = :email")
    Walk findByEmail(@Param("id") int num, @Param("year") int year, @Param("month") int month, @Param("day") int day, @Param("email") String email);

}
