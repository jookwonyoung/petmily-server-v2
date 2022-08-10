package petmily.client;

import org.jetbrains.annotations.Nullable;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import petmily.controller.dto.EmotionResponseDto;

import java.io.File;

@Component
public class FlaskTemplate {

    private final RestTemplate restTemplate;
    String url = "http://localhost:34343/";

    public FlaskTemplate(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    // 개와 고양이의 포함 여부
    public String requestDetectAnimal(File imageFile) {
        String apiUrl = url + "detect";

        return sendRequest(imageFile, apiUrl);
    }

    public String requestDetectAnimal(String filePath) {
        String apiUrl = url + "detect";

        return sendRequest(filePath, apiUrl);
    }


    // 개의 종 분류
    public String requestBreedDog(File imageFile) {
        String apiUrl = url + "predict/breed/dog";

        return sendRequest(imageFile, apiUrl);
    }

    public String requestBreedDog(String filePath) {
        String apiUrl = url + "predict/breed/dog";

        return sendRequest(filePath, apiUrl);
    }

    public String requestBreedCat(File imageFile) {
        String apiUrl = url + "predict/breed/cat";

        return sendRequest(imageFile, apiUrl);
    }

    public String requestBreedCat(String filePath) {
        String apiUrl = url + "predict/breed/cat";

        return sendRequest(filePath, apiUrl);
    }

    // 감정 분류
    public String requestEmotion(File imageFile) {
        String apiUrl = url + "predict/emotion";

        return sendRequest(imageFile, apiUrl);
    }

    public String requestEmotion(String filePath) {
        String apiUrl = url + "predict/emotion";

        return sendRequest(filePath, apiUrl);
    }

    public EmotionResponseDto requestEmotion2(String filePath) {
        String apiUrl = url + "predict/emotion";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl).queryParam("path", filePath);


        // send request
        return restTemplate.getForObject(builder.toUriString(), EmotionResponseDto.class);
    }

    @Nullable
    private String sendRequest(File imageFile, String apiUrl) {
        // write header part
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // write body part
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>(1);
        body.add("file", new FileSystemResource(imageFile));

        // write HTTPEntity instance
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // send request
        return restTemplate.postForObject(apiUrl, requestEntity, String.class);
    }

    @Nullable
    private String sendRequest(String filePath, String apiUrl) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(apiUrl).queryParam("path", filePath);


        // send request
        return restTemplate.getForObject(builder.toUriString(), String.class);
    }
}
