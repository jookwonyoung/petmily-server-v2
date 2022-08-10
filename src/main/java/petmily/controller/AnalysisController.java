package petmily.controller;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import petmily.controller.dto.EmotionResponseDto;
import petmily.service.analysis.AnalysisService;

import java.io.File;

@RequestMapping("/api/analysis")
@RequiredArgsConstructor
@RestController
public class AnalysisController {

    private final AnalysisService analysisService;

    private final String ec2Path = "/home/ec2-user/petmilyServer/step1/imgDB/tmp";
/*

    @GetMapping("/breed/dog")
    public String breedDog(@RequestParam("img") MultipartFile img) {
        String filePath = saveImg(img);

        if (filePath == null) return "not valid image";

        String result = analysisService.breedDog(filePath);

        deleteImg(filePath);

        return result;
    }

    @GetMapping("/breed/cat")
    public String breedCat(@RequestParam("img") MultipartFile img) {
        String filePath = saveImg(img);
        if (filePath == null) return "not valid image";

        String result = analysisService.breedCat(filePath);

        deleteImg(filePath);

        return result;
    }

    @GetMapping("/emotion")
    public EmotionResponseDto emotion(@RequestParam("img") MultipartFile img) {
        String filePath = saveImg(img);
        if (filePath == null) return null;

        EmotionResponseDto result = analysisService.matchEmotionDto(filePath);

        deleteImg(filePath);

        return result;
    }
*/

    @PostMapping("/emotion")
    public EmotionResponseDto emotion(@RequestParam("img") MultipartFile img) {
        String filePath = saveImg(img);
        if (filePath == null) return null;

        EmotionResponseDto result = analysisService.matchEmotionDto(filePath);

        deleteImg(filePath);

        return result;
    }

    private void deleteImg(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                if (!file.delete()) {
                    System.out.println("파일삭제 실패");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    private String saveImg(MultipartFile img) {
        String conType = img.getContentType();
        if (!(conType.equals("image/png") || conType.equals("image/jpeg"))) {
            return null;
        }

        String filePath = ec2Path + "/" + System.currentTimeMillis();
        try {
            img.transferTo(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filePath;
    }
}
