package petmily.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petmily.controller.dto.UserSaveRequestDto;
import petmily.domain.user.User;
import petmily.domain.user.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public Long save(UserSaveRequestDto requestDto) {

        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElse(requestDto.toEntity());

        Long id = userRepository.save(user).getUserId();
        return id;
    }

    @Transactional
    public String findImgByEmail(String email) {
        return userRepository.findImgByEmail(email).getUserImg();
    }
}
