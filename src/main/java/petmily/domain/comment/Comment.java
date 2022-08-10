package petmily.domain.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String commentContent;

    @Builder
    public Comment(Long postId, String email, String commentContent) {
        this.postId = postId;
        this.email = email;
        this.commentContent = commentContent;
    }
}
