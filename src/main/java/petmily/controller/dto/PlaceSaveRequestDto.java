package petmily.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import petmily.domain.place.Place;

@Getter
@Setter
@NoArgsConstructor
public class PlaceSaveRequestDto {
    private String email;
    private String placeName;
    private String phone;
    private String address;
    private String categories;
    private String url;
    private String userImg;


    @Builder
    public PlaceSaveRequestDto(String email, String placeName, String phone, String address, String categories, String url, String userImg) {
        this.email = email;
        this.placeName = placeName;
        this.phone = phone;
        this.address = address;
        this.categories = categories;
        this.url = url;
        this.userImg = userImg;
    }

    public Place toEntity() {
        return Place.builder()
                .email(email)
                .placeName(placeName)
                .phone(phone)
                .address(address)
                .categories(categories)
                .url(url)
                .build();
    }

}
