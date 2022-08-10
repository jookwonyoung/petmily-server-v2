package petmily.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petmily.domain.posts.Post;

@Getter
@Setter
@NoArgsConstructor
public class PostSaveRequestDto {
    private String email;
    private String postImg;
    private String postContent;
    private String tags;

    @Builder
    public PostSaveRequestDto(String email, String postImg, String postContent, String tags) {
        this.email = email;
        this.postImg = postImg;
        this.postContent = postContent;
        this.tags = tags;
    }

    public Post toEntity() {
        return Post.builder()
                .email(email)
                .postImg(postImg)
                .postContent(postContent)
                .tags(tags)
                .build();
    }
}
