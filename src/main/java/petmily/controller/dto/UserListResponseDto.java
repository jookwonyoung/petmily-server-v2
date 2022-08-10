package petmily.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserListResponseDto {

    private String email;
    private String userImg;

    public UserListResponseDto(String email, String userImg) {
        this.email = email;
        this.userImg = userImg;
    }
}
