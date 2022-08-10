package petmily.service.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petmily.controller.dto.PostListResponseDto;
import petmily.controller.dto.PostSaveRequestDto;
import petmily.domain.posts.Post;
import petmily.domain.posts.PostRepository;
import petmily.service.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserService userService;

    @Transactional
    public Long save(PostSaveRequestDto requestDto) {
        Long id = postRepository.save(requestDto.toEntity()).getPostId();  //실제 db 저장
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(String.valueOf(id)));//수정할 객체(지금 저장한거)
        post.update(id);    //객체의 id를 이미지의 id로 보냄
        return id;
    }

    @Transactional
    public void appendTag(Long postId, String tag) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException(String.valueOf(postId)));
        post.appendTag(tag);
    }

    @Transactional(readOnly = true)
    public List<PostListResponseDto> findAllDesc() {
        List<PostListResponseDto> result = postRepository.findAllDesc().stream()
                .map(post -> {
                    return new PostListResponseDto(post, userService.findImgByEmail(post.getEmail()));
                })
                .collect(Collectors.toList());
        return result;
    }

    @Transactional(readOnly = true)
    public List<PostListResponseDto> findAllMyLikePost(List<Long> likes) {
        List<PostListResponseDto> result = postRepository.findAllDesc().stream()
                .map(post -> {
                    return new PostListResponseDto(post, userService.findImgByEmail(post.getEmail()));
                })
                .filter(post -> likes.contains(post.getPostId()))
                .collect(Collectors.toList());
        return result;
    }

    public String delete(Long postId, String email) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + postId));

        try{//삭제 성공
            if(post.getEmail().equals(email)) {
                postRepository.delete(post);
                return "게시글이 삭제되었습니다.";
            }else{
                return "자신의 게시글만 삭제할 수 있습니다.";
            }
        }catch(Exception e){//삭제 실패
            return "게시글이 삭제되지 않았습니다.";
        }
    }
}
