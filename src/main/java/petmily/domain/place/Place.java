package petmily.domain.place;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    @Column(nullable = false)
    private String email;

    @Column(length = 500, nullable = false)
    private String placeName;

    @Column(length = 500, nullable = false)
    private String phone;

    @Column(length = 500, nullable = false)
    private String address;

    @Column(length = 500, nullable = false)
    private String categories;

    @Column(length = 500, nullable = false)
    private String url;

    @Builder
    public Place(String email, String placeName, String phone, String address, String categories, String url) {
        this.email = email;
        this.placeName = placeName;
        this.phone = phone;
        this.address = address;
        this.categories = categories;
        this.url = url;
    }

}
