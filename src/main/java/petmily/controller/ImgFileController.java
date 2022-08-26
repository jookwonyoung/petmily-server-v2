package petmily.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RequiredArgsConstructor
@RestController
public class ImgFileController {

    private String localPath = "/Users/jookwonyoung/Documents/DB/petmily/testImg";
    private String ubuntuPath = "/home/jooky/petmilyServer/step1/imgDB";
    String RootPath;


    public String fileSave(String path, MultipartFile files, Long fileId, String email){

        //환경 구분(맥, 우분투 && post, walk)
        if (new File(ubuntuPath).exists()) {
            RootPath = ubuntuPath + "/" + path;     //ubuntu-server
        } else {
            RootPath = localPath+ "/" + path;       //localhost
        }


        //확장자 체크
        String conType = files.getContentType();
        if (!(conType.equals("image/png") || conType.equals("image/jpeg"))) {
            return "올바르지 않은 파일 - png 또는 jpeg 를 적용하세요";
        }


        //Walk 호출이면 email 디렉토리 확인,생성
        if(path.equals("walk")){
            RootPath = RootPath + "/" + email;
            if (!new File(RootPath).exists()) {
                try {
                    new File(RootPath).mkdir();
                } catch (Exception e) {
                    e.getStackTrace();
                    return "내부 서버 오류 - 사용자 산책 디렉토리 생성 실패";
                }
            }
        }


        //파일저장
        String filePath = RootPath + "/" + fileId;
        try {
            files.transferTo(new File(filePath));
        }catch (Exception e) {
            e.printStackTrace();
            return "내부 서버 오류 - 파일 저장 실패";
        }


        return "이미지 저장 성공";
    }

}
