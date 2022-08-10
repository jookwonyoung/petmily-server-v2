package petmily.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petmily.domain.walk.Walk;

@Getter
@Setter
@NoArgsConstructor
public class WalkSaveRequestDto {
    private String email;
    private int year;
    private int month;
    private int day;
    private String img;
    private float avgSpeedInKMH;
    private int distanceInMeters;
    private long timeInMillis;
    private int caloriesBurned;
    private int id;

    @Builder
    public WalkSaveRequestDto(String email, int year, int month, int day, String img, float avgSpeedInKMH, int distanceInMeters, long timeInMillis, int caloriesBurned, int id) {
        this.email = email;
        this.year = year;
        this.month = month;
        this.day = day;
        this.img = img;
        this.avgSpeedInKMH = avgSpeedInKMH;
        this.distanceInMeters = distanceInMeters;
        this.timeInMillis = timeInMillis;
        this.caloriesBurned = caloriesBurned;
        this.id = id;
    }

    public Walk toEntity() {
        return Walk.builder()
                .email(email)
                .year(year)
                .month(month)
                .day(day)
                .img(img)
                .avgSpeedInKMH(avgSpeedInKMH)
                .distanceInMeters(distanceInMeters)
                .timeInMillis(timeInMillis)
                .caloriesBurned(caloriesBurned)
                .id(id)
                .build();
    }

}
