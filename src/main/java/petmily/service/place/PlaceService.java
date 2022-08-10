package petmily.service.place;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import petmily.controller.dto.PlaceListResponseDto;
import petmily.controller.dto.PlaceSaveRequestDto;
import petmily.domain.place.Place;
import petmily.domain.place.PlaceRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PlaceService {
    private final PlaceRepository placeRepository;

    @Transactional
    public Long save(PlaceSaveRequestDto requestDto) {
        return placeRepository.save(requestDto.toEntity()).getPlaceId();
    }

    @Transactional(readOnly = true)
    public List<PlaceListResponseDto> findByEmail(String email) {
        return placeRepository.findByEmail(email).stream()
                .map(PlaceListResponseDto::new)
                .collect(Collectors.toList());
    }

    public void delete(Long placeId) {
        Place place = placeRepository.findById(placeId).orElseThrow(
                () -> new IllegalArgumentException("해당 즐겨찾기 장소가 없습니다. placeId=" + placeId));

        placeRepository.delete(place);
    }
}
