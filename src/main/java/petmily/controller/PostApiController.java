package petmily.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import petmily.controller.dto.PostListResponseDto;
import petmily.controller.dto.PostSaveRequestDto;
import petmily.controller.dto.UserSaveRequestDto;
import petmily.service.like.LikeService;
import petmily.service.post.PostService;
import petmily.service.user.UserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@RequestMapping("/api/post")
@RequiredArgsConstructor
@RestController
public class PostApiController {

    private final UserService userService;
    private final PostService postService;
    private final LikeService likeService;

    private final ImgFileController imgFileController;

    @PostMapping("/save")
    public String save(@RequestHeader(value = "email") String email, @RequestParam("userImg") String userImg, @RequestParam("postImg") MultipartFile files,
                       @RequestParam("postContent") String content) {


        UserSaveRequestDto saveRequestDto = new UserSaveRequestDto();
        saveRequestDto.setEmail(email);
        saveRequestDto.setUserImg(userImg);
        Long userId = userService.save(saveRequestDto);


        //post 객체 생성, 저장
        PostSaveRequestDto requestDto = new PostSaveRequestDto(email, null, content, null);
        Long postId = postService.save(requestDto);     //저장할 postImg(filename)


        //이미지파일 저장
        imgFileController.fileSave("post", files, postId, email);


        return postId.toString();
    }

    @GetMapping(value = "/getImg/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        InputStream in;
        try {
            try {
                in = new FileInputStream("/home/jooky/petmilyServer/step1/imgDB/post" + "/" + id);   //파일 읽어오기
            } catch (Exception e) {
                in = new FileInputStream("/Users/jookwonyoung/Documents/DB/petmily/testImg/post" + "/" + id);   //파일 읽어오기
            }
            byte[] imgByteArray = in.readAllBytes();                    //byte로 변환
            in.close();
            return new ResponseEntity<byte[]>(imgByteArray, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<byte[]>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/findAll")
    public List<PostListResponseDto> findAll() {
        List<PostListResponseDto> responseDtoList = postService.findAllDesc();
        return responseDtoList;
    }

    @GetMapping(value = "/findAllLike")
    public List<PostListResponseDto> findAllMyLikePost(@RequestHeader(value = "email") String email) {
        List<Long> likes = likeService.findLikedPost(email);
        List<PostListResponseDto> responseDtoList = postService.findAllMyLikePost(likes);
        return responseDtoList;
    }

    @DeleteMapping(value = "/delete/{postId}")
    public String delete(@RequestHeader(value = "email") String email, @PathVariable Long postId){
        return postService.delete(postId, email);
    }
}
