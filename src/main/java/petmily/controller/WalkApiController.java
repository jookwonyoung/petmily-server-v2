package petmily.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import petmily.controller.dto.UserSaveRequestDto;
import petmily.controller.dto.WalkImgListResponseDto;
import petmily.controller.dto.WalkSaveRequestDto;
import petmily.service.user.UserService;
import petmily.service.walk.WalkService;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;


@RequestMapping("/api/walk")
@RequiredArgsConstructor
@RestController
public class WalkApiController {

    private final UserService userService;
    private final WalkService walkService;

    private final ImgFileController imgFileController;

    @PostMapping("/save")
    public String save(@RequestHeader(value = "email") String email, @RequestParam("year") int year,
                     @RequestParam("month") int month, @RequestParam("day") int day,
                     @RequestParam("img") MultipartFile files, @RequestParam("avgSpeedInKMH") float avgSpeedInKMH,
                     @RequestParam("distanceInMeters") int distanceInMeters, @RequestParam("timeInMillis") long timeInMillis,
                     @RequestParam("caloriesBurned") int caloriesBurned, @RequestParam("id") int id,
                     @RequestParam("userImg") String userImg) {

        UserSaveRequestDto saveRequestDto = new UserSaveRequestDto();
        saveRequestDto.setEmail(email);
        saveRequestDto.setUserImg(userImg);
        Long userId = userService.save(saveRequestDto);


        //walk 저장객체 생성, 저장
        WalkSaveRequestDto requestDto = new WalkSaveRequestDto(email, year, month, day, null, avgSpeedInKMH, distanceInMeters, timeInMillis, caloriesBurned, id);
        Long walkId = walkService.save(requestDto);


        //이미지파일 저장
        return imgFileController.fileSave("walk", files, walkId, email);
    }

    @GetMapping("findAll")
    public List<WalkImgListResponseDto> findAll(@RequestHeader(value = "email") String email) {
        return walkService.findAllDesc(email);
    }

    @GetMapping("/findByDate/{year}/{month}/{day}")
    public List<WalkImgListResponseDto> findByDate(@RequestHeader(value = "email") String email, @PathVariable int year, @PathVariable int month, @PathVariable int day) {
        return walkService.findDateDesc(year, month, day, email);
    }

    @DeleteMapping("/delete/{id}/{year}/{month}/{day}")
    public void delete(@RequestHeader(value = "email") String email, @PathVariable int id, @PathVariable int year, @PathVariable int month, @PathVariable int day) {
        walkService.delete(id, year, month, day, email);
    }
}
