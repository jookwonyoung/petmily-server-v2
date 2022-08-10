package petmily.service.like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petmily.controller.dto.LikeSaveRequestDto;
import petmily.controller.dto.UserListResponseDto;
import petmily.domain.like.Like;
import petmily.domain.like.LikeRepository;
import petmily.service.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserService userService;

    public String createLike(LikeSaveRequestDto requestDto) {
        try {
            if (likeRepository.check(requestDto.getEmail(), requestDto.getPostId()).isPresent()) {
                return "이미 좋아요 누른 게시글입니다.";
            } else {
                likeRepository.save(requestDto.toEntity());
                return "좋아요 성공!!";
            }
        }catch (Exception e){
            return "내부 서버 오류 - 좋아요 실패";
        }

//        Like like = likeRepository.check(requestDto.getEmail(), requestDto.getPostId())
//                .orElse(requestDto.toEntity());
//        Long id = likeRepository.save(like).getLikeId();
//        return id;
    }

    public void delete(String email, Long postId) {
        try {
            Like like = likeRepository.findByEmailPostId(email, postId);
            likeRepository.delete(like);
        } catch (Exception e) {
        }
    }

    public int findMyLike(String email, Long postId) {
        if (likeRepository.check(email, postId).isPresent()) {
            return 1;
        } else {
            return 0;
        }
    }

    @Transactional(readOnly = true)
    public int countLike(Long postId) {
        return likeRepository.countLike(postId);
    }

    @Transactional(readOnly = true)
    public List<UserListResponseDto> findAllUser(Long postId) {
        List<UserListResponseDto> result = likeRepository.findUserByPostId(postId).stream().map(like -> {
            return new UserListResponseDto(like.getEmail(), userService.findImgByEmail(like.getEmail()));
        }).collect(Collectors.toList());

        return result;
    }


    public List<Long> findLikedPost(String email) {
        return likeRepository.findByEmail(email).stream()
                .map(like -> like.getPostId()).collect(Collectors.toCollection(ArrayList::new));
    }
}
