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
    String emailPath;       //walk/email 폴더
    private final String localPath = "/Users/jookwonyoung/Documents/petmily/testImg/walk";
    private final String ec2Path = "/home/ec2-user/petmilyServer/step1/imgDB/walk";

    @PostMapping("/save")
    public Long save(@RequestHeader(value = "email") String email, @RequestParam("year") int year,
                     @RequestParam("month") int month, @RequestParam("day") int day,
                     @RequestParam("img") MultipartFile files, @RequestParam("avgSpeedInKMH") float avgSpeedInKMH,
                     @RequestParam("distanceInMeters") int distanceInMeters, @RequestParam("timeInMillis") long timeInMillis,
                     @RequestParam("caloriesBurned") int caloriesBurned, @RequestParam("id") int id,
                     @RequestParam("userImg") String userImg) {

        UserSaveRequestDto saveRequestDto = new UserSaveRequestDto();
        saveRequestDto.setEmail(email);
        saveRequestDto.setUserImg(userImg);
        Long userId = userService.save(saveRequestDto);

        // 1. walk 저장객체 생성, 저장
        WalkSaveRequestDto requestDto = new WalkSaveRequestDto();
        requestDto.setEmail(email);
        requestDto.setYear(year);
        requestDto.setMonth(month);
        requestDto.setDay(day);
        requestDto.setAvgSpeedInKMH(avgSpeedInKMH);
        requestDto.setCaloriesBurned(caloriesBurned);
        requestDto.setDistanceInMeters(distanceInMeters);
        requestDto.setId(id);
        requestDto.setTimeInMillis(timeInMillis);
        Long walkId = walkService.save(requestDto);

        if (new File(ec2Path).exists()) {
            emailPath = ec2Path + "/" + email;
        } else {
            emailPath = localPath + "/" + email;
        }

        // 확장자 체크
        String conType = files.getContentType();
        if (!(conType.equals("image/png") || conType.equals("image/jpeg"))) {
            Long error = null;
            return error;
        }

        // 2. 사용자 이메일 디렉토리 생성
        if (!new File(emailPath).exists()) {
            try {
                new File(emailPath).mkdir();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        // 3. 산책 이미지 저장
        String filePath = emailPath + "/" + walkId;
        try {
            files.transferTo(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return walkId;
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
