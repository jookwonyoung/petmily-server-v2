package petmily.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petmily.domain.user.User;

@Setter
@Getter
@NoArgsConstructor
public class UserSaveRequestDto {
    private String email;
    private String userImg;

    @Builder
    public UserSaveRequestDto(String email, String userImg) {
        this.email = email;
        this.userImg = userImg;
    }

    public User toEntity() {
        return User.builder()
                .email(email)
                .userImg(userImg)
                .build();
    }
}
