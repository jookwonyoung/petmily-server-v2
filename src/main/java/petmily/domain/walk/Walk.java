package petmily.domain.walk;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Walk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walkId;

    @Column(nullable = false)
    private String email;

    private int year;
    private int month;
    private int day;
    private String img;
    private float avgSpeedInKMH;
    private int distanceInMeters;
    private long timeInMillis;
    private int caloriesBurned;
    @Column(nullable = true)
    private int id;


    @Builder
    public Walk(String email, int year, int month, int day, String img, float avgSpeedInKMH, int distanceInMeters, long timeInMillis, int caloriesBurned, int id) {
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

    public void update(Long id) {
        this.img = id.toString();
    }

}
