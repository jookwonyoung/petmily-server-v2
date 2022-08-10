package petmily.controller.dto;

import lombok.Getter;
import petmily.domain.walk.Walk;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class WalkImgListResponseDto {
    private final Long walkId;
    private final String email;
    private final int year;
    private final int month;
    private final int day;
    private byte[] img;
    private final float avgSpeedInKMH;
    private final int distanceInMeters;
    private final long timeInMillis;
    private final int caloriesBurned;
    private final int id;

    public WalkImgListResponseDto(Walk entity) {

        String emailPath = "/home/ec2-user/petmilyServer/step1/imgDB/walk/" + entity.getEmail();

        this.walkId = entity.getWalkId();
        this.email = entity.getEmail();
        this.year = entity.getYear();
        this.month = entity.getMonth();
        this.day = entity.getDay();
        try {
            InputStream in;

            in = new FileInputStream(emailPath + "/" + entity.getWalkId());   //파일 읽어오기
            this.img = in.readAllBytes();
            in.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
        this.avgSpeedInKMH = entity.getAvgSpeedInKMH();
        this.distanceInMeters = entity.getDistanceInMeters();
        this.timeInMillis = entity.getTimeInMillis();
        this.caloriesBurned = entity.getCaloriesBurned();
        this.id = entity.getId();
    }
}
