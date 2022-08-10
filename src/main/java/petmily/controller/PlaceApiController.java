package petmily.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import petmily.controller.dto.PlaceListResponseDto;
import petmily.controller.dto.PlaceSaveRequestDto;
import petmily.controller.dto.UserSaveRequestDto;
import petmily.service.place.PlaceService;
import petmily.service.user.UserService;

import java.util.List;

@RequestMapping("/api/place")
@RequiredArgsConstructor
@RestController
public class PlaceApiController {

    private final UserService userService;
    private final PlaceService placeService;

    @PostMapping("/save")
    public Long save(@RequestHeader(value = "email") String email, @RequestBody PlaceSaveRequestDto requestDto) {
        UserSaveRequestDto saveRequestDto = new UserSaveRequestDto();
        saveRequestDto.setEmail(email);
        saveRequestDto.setUserImg(requestDto.getUserImg());
        Long userId = userService.save(saveRequestDto);

        requestDto.setEmail(email);
        return placeService.save(requestDto);
    }

    @GetMapping("/findByEmail")
    public List<PlaceListResponseDto> findByEmail(@RequestHeader(value = "email") String email) {
        return placeService.findByEmail(email);
    }

    @DeleteMapping("/delete/{placeId}")
    public void delete(@PathVariable Long placeId) {
        placeService.delete(placeId);
    }

}
