package petmily.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import petmily.controller.dto.LikeSaveRequestDto;
import petmily.controller.dto.UserListResponseDto;
import petmily.controller.dto.UserSaveRequestDto;
import petmily.service.like.LikeService;
import petmily.service.user.UserService;

import java.util.List;

@RequestMapping("/api/like")
@RequiredArgsConstructor
@RestController
public class LikeApiController {

    private final UserService userService;
    private final LikeService likeService;

    @PostMapping("/save")
    public String createLike(@RequestHeader(value = "email") String email, @RequestParam("postId") Long postId, @RequestParam("userImg") String userImg) {
        UserSaveRequestDto saveRequestDto = new UserSaveRequestDto();
        saveRequestDto.setEmail(email);
        saveRequestDto.setUserImg(userImg);
        userService.save(saveRequestDto);

        LikeSaveRequestDto requestDto = new LikeSaveRequestDto();
        requestDto.setEmail(email);
        requestDto.setPostId(postId);
        return likeService.createLike(requestDto);
    }

    @DeleteMapping("/delete/{postId}")
    public void delete(@RequestHeader(value = "email") String email, @PathVariable Long postId) {
        likeService.delete(email, postId);
    }

    @GetMapping("/aboutMyLike/{postId}")
    public int findMyLike(@RequestHeader(value = "email") String email, @PathVariable Long postId) {
        return likeService.findMyLike(email, postId);
    }

    @GetMapping("/count/{postId}")
    public int countLike(@PathVariable Long postId) {
        return likeService.countLike(postId);
    }

    @GetMapping("/users/{postId}")
    public List<UserListResponseDto> findAllUser(@PathVariable Long postId) {
        return likeService.findAllUser(postId);
    }

}
