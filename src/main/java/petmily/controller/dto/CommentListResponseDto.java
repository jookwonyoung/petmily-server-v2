package petmily.controller.dto;

import lombok.Getter;
import lombok.Setter;
import petmily.domain.comment.Comment;

@Getter
@Setter
public class CommentListResponseDto {

    private Long commentId;
    private Long postId;
    private String email;
    private String commentContent;
    private String userImg;


    public CommentListResponseDto(Comment entity, String userImg) {
        this.commentId = entity.getCommentId();
        this.postId = entity.getPostId();
        this.email = entity.getEmail();
        this.commentContent = entity.getCommentContent();
        this.userImg = userImg;
    }
}
