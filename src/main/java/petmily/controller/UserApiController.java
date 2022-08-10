package petmily.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import petmily.controller.dto.UserSaveRequestDto;
import petmily.service.user.UserService;

@RequestMapping("/api/user")
@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    @PostMapping("/save")
    public Long save(@RequestHeader(value = "email") String email, @RequestBody UserSaveRequestDto requestDto) {
        requestDto.setEmail(email);
        return userService.save(requestDto);
    }

}
