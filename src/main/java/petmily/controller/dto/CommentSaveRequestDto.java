package petmily.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petmily.domain.comment.Comment;

@Getter
@Setter
@NoArgsConstructor
public class CommentSaveRequestDto {

    private Long postId;
    private String email;
    private String commentContent;

    @Builder
    public CommentSaveRequestDto(Long postId, String email, String commentContent) {
        this.postId = postId;
        this.email = email;
        this.commentContent = commentContent;
    }

    public Comment toEntity() {
        return Comment.builder()
                .postId(postId)
                .email(email)
                .commentContent(commentContent)
                .build();
    }
}
