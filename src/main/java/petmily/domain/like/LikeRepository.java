package petmily.domain.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("SELECT l FROM Like l WHERE l.email = :email and l.postId = :postId")
    Optional<Like> check(@Param("email") String email, @Param("postId") Long postId);

    @Query("SELECT l FROM Like l WHERE l.email = :email and l.postId = :postId")
    Like findByEmailPostId(@Param("email") String email, @Param("postId") Long postId);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.postId = :postId")
    int countLike(@Param("postId") Long postId);

    @Query("SELECT l FROM Like l WHERE l.postId = :postId")
    List<Like> findUserByPostId(@Param("postId") Long postId);

    @Query("SELECT l FROM Like l WHERE l.email = :email")
    List<Like> findByEmail(@Param("email") String mail);
}
