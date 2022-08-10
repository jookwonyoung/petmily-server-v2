package petmily.controller.dto;

import lombok.Getter;
import petmily.domain.place.Place;

@Getter
public class PlaceListResponseDto {

    private final Long placeId;
    private final String email;
    private final String placeName;
    private final String phone;
    private final String address;
    private final String categories;
    private final String url;

    public PlaceListResponseDto(Place entity) {
        this.placeId = entity.getPlaceId();
        this.email = entity.getEmail();
        this.placeName = entity.getPlaceName();
        this.phone = entity.getPhone();
        this.address = entity.getAddress();
        this.categories = entity.getCategories();
        this.url = entity.getUrl();
    }
}
