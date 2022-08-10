package petmily.domain.posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //id 자동 할당
    private Long postId;

    @Column(nullable = false)
    private String email;

    private String postImg;

    @Column(columnDefinition = "TEXT")
    private String postContent;

    @Column(columnDefinition = "TEXT")
    private String tags;

    @Builder
    public Post(String email, String postImg, String postContent, String tags) {
        this.email = email;
        this.postImg = postImg;
        this.postContent = postContent;
        this.tags = tags;
    }

    public void update(Long id) {
        this.postImg = id.toString();
    }

    public void appendTag(String tag) {
        this.tags = this.tags + ", " + tag;
    }
}
