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

    private String localPath = "/Users/jookwonyoung/Documents/DB/petmily/testImg/post";
    private String ubuntuPath = "/home/jooky/petmilyServer/step1/imgDB/post";

    String postRootPath;    //post 폴더

    @PostMapping("/save")
    public String save(@RequestHeader(value = "email") String email, @RequestParam("userImg") String userImg, @RequestParam("postImg") MultipartFile files,
                       @RequestParam("postContent") String content) {

        if (new File(ubuntuPath).exists()) {
            postRootPath = ubuntuPath;        //ubuntu-server
        } else {
            postRootPath = localPath;     //localhost
        }

        UserSaveRequestDto saveRequestDto = new UserSaveRequestDto();
        saveRequestDto.setEmail(email);
        saveRequestDto.setUserImg(userImg);
        Long userId = userService.save(saveRequestDto);


        // 확장자 체크
        String conType = files.getContentType();
        if (!(conType.equals("image/png") || conType.equals("image/jpeg"))) {
            return "올바르지 않은 파일";
        }

        //post 객체 생성, 저장
        PostSaveRequestDto requestDto = new PostSaveRequestDto();
        requestDto.setEmail(email);
        requestDto.setPostContent(content);
        Long postId = postService.save(requestDto);     //저장할 postImg(filename)


        //postImg 저장 - 로컬에 실제 이미지 저장
        String filePath = postRootPath + "/" + postId;
        File img = new File(filePath);
        try {
            files.transferTo(img);
        }catch (Exception e) {
            e.printStackTrace();
            return "내부 서버 오류 - 파일 저장 실패";
        }


        return postId.toString();
    }

    @GetMapping(value = "/getImg/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
        InputStream in;
        try {
            try {
                in = new FileInputStream(ubuntuPath + "/" + id);   //파일 읽어오기
            } catch (Exception e) {
                in = new FileInputStream(localPath + "/" + id);   //파일 읽어오기
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
