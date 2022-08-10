package petmily.service.comment;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petmily.controller.dto.CommentListResponseDto;
import petmily.controller.dto.CommentSaveRequestDto;
import petmily.domain.comment.CommentRepository;
import petmily.service.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class commentService {

    private final CommentRepository commentRepository;
    private final UserService userService;

    @Transactional
    public Long save(CommentSaveRequestDto requestDto) {
        return commentRepository.save(requestDto.toEntity()).getCommentId();
    }

    @Transactional(readOnly = true)
    public List<CommentListResponseDto> findAllDesc(Long postId) {
        List<CommentListResponseDto> result = commentRepository.findAll().stream()
                .map(comment -> {
                    return new CommentListResponseDto(comment, userService.findImgByEmail(comment.getEmail()));
                })
                .filter(comment -> comment.getPostId() == postId)
                .collect(Collectors.toList());

        return result;
    }
}
