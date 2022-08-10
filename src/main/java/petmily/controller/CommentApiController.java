package petmily.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import petmily.controller.dto.CommentListResponseDto;
import petmily.controller.dto.CommentSaveRequestDto;
import petmily.controller.dto.UserSaveRequestDto;
import petmily.service.comment.commentService;
import petmily.service.user.UserService;

import java.util.List;

@RequestMapping("/api/comment")
@RequiredArgsConstructor
@RestController
public class CommentApiController {

    private final UserService userService;
    private final commentService commentService;

    @PostMapping("/save")
    public Long save(@RequestHeader(value = "email") String email, @RequestParam("postId") Long postId,
                     @RequestParam("commentContent") String commentContent, @RequestParam("userImg") String userImg) {

        UserSaveRequestDto saveRequestDto = new UserSaveRequestDto();
        saveRequestDto.setEmail(email);
        saveRequestDto.setUserImg(userImg);
        Long userId = userService.save(saveRequestDto);

        CommentSaveRequestDto requestDto = new CommentSaveRequestDto();
        requestDto.setEmail(email);
        requestDto.setPostId(postId);
        requestDto.setCommentContent(commentContent);

        return commentService.save(requestDto);
    }


    @GetMapping(value = "/findByPostId/{postId}")
    public List<CommentListResponseDto> findByPostId(@PathVariable Long postId) {
        List<CommentListResponseDto> responseDtoList = commentService.findAllDesc(postId);
        return responseDtoList;
    }
}
