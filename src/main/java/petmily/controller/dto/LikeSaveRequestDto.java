package petmily.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petmily.domain.like.Like;

@Getter
@Setter
@NoArgsConstructor
public class LikeSaveRequestDto {
    private Long postId;
    private String email;

    @Builder
    public LikeSaveRequestDto(Long postId, String email) {
        this.postId = postId;
        this.email = email;
    }

    public Like toEntity() {
        return Like.builder()
                .postId(postId)
                .email(email)
                .build();
    }
}
