package petmily.service.analysis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import petmily.client.FlaskTemplate;
import petmily.controller.dto.EmotionResponseDto;
import petmily.service.post.PostService;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final FlaskTemplate template;
    private final PostService postService;

    public String detectAnimal(String path) {
        return template.requestDetectAnimal(path);
    }

    public String breedDog(String path) {
        return template.requestBreedDog(path);
    }

    public String breedCat(String path) {
        return template.requestBreedCat(path);
    }

    public String emotion(String path) {
        return template.requestEmotion(path);
    }

    public void autoTagging(Long postId, String type, String filePath) {

        String breedResult = "";
        // get post image breed
        if (type.equals("dog")) {
            breedResult = template.requestBreedDog(filePath);
        }
        if (type.equals("cat")) {
            breedResult = template.requestBreedCat(filePath);
        }
        String emotionResult = template.requestEmotion(filePath);

        ObjectMapper objectMapper = new ObjectMapper();
        // get post image emotion
        try {
            JsonNode breedNode = objectMapper.readTree(breedResult);
            String breed = breedNode.get("top3").get(0).get("breed").asText();
            breed = breedNameReplacer(breed);
            JsonNode emotionNode = objectMapper.readTree(emotionResult);
            String emotion = "";
            Double angry = emotionNode.get("emotion").get("angry").asDouble();
            Double sad = emotionNode.get("emotion").get("sad").asDouble();
            Double happy = emotionNode.get("emotion").get("happy").asDouble();

            // Find the highest value among angry, sad, happy
            if (angry > sad && angry > happy) {
                emotion = "화나요";
            }
            if (sad > angry && sad > happy) {
                emotion = "슬퍼요";
            }
            if (happy > angry && happy > sad) {
                emotion = "행복해요";
            }

            // db update
            postService.appendTag(postId, breed);
            postService.appendTag(postId, emotion);

        } catch (Exception e) {
            System.out.println("내부 서버 오류 - 자동 태깅 실패");
        }
    }

    public EmotionResponseDto matchEmotionDto(String filePath) {

        // 초기 변수 세팅
        JsonNode breedNode = null;
        JsonNode emotionNode = null;

        ObjectMapper objectMapper = new ObjectMapper();
        EmotionResponseDto emotionResponseDto = new EmotionResponseDto(); // 반환 객체

        String emotionResult = template.requestEmotion(filePath);

        try {
            emotionNode = objectMapper.readTree(emotionResult);
        } catch (Exception e) {
            emotionResponseDto.setMessage("내부 서버 오류 - 감정 분석 실패");
            return emotionResponseDto;
        }

        // 개고양이를 대상으로 종 분류와 감정분석 실시
        String type = emotionNode.get("category").asText();
        // get breed
        String breedResult = "";
        if (type.equals("dog")) {
            breedResult = template.requestBreedDog(filePath);
        } else if (type.equals("cat")) {
            breedResult = template.requestBreedCat(filePath);
        } else {
            emotionResponseDto.setMessage("개와 고양이가 포함되어 있지 않은 이미지입니다.");
            return emotionResponseDto;
        }

        // get post image emotion
        try {
            breedNode = objectMapper.readTree(breedResult);
        } catch (Exception e) {
            emotionResponseDto.setMessage("내부 서버 오류 - 품종 분석 실패");
            return emotionResponseDto;
        }

        // Node에서 값 추출
        String top1 = breedNode.get("top3").get(0).get("breed").asText();
        top1 = breedNameReplacer(top1);
        Double top1Value = breedNode.get("top3").get(0).get("value").asDouble();
        String top2 = breedNode.get("top3").get(1).get("breed").asText();
        top2 = breedNameReplacer(top2);
        Double top2Value = breedNode.get("top3").get(1).get("value").asDouble();
        String top3 = breedNode.get("top3").get(2).get("breed").asText();
        top3 = breedNameReplacer(top3);
        Double top3Value = breedNode.get("top3").get(2).get("value").asDouble();
        Double angry = emotionNode.get("emotion").get("angry").asDouble();
        Double sad = emotionNode.get("emotion").get("sad").asDouble();
        Double happy = emotionNode.get("emotion").get("happy").asDouble();
        int leftX = emotionNode.get("crop_position").get(0).asInt();
        int rightX = emotionNode.get("crop_position").get(1).asInt();
        int leftY = emotionNode.get("crop_position").get(2).asInt();
        int rightY = emotionNode.get("crop_position").get(3).asInt();

        double minimum = Math.min(angry, Math.min(sad, happy));

        minimum = Math.abs(minimum);
        angry += minimum;
        sad += minimum;
        happy += minimum;

        double sum = angry + sad + happy;

        angry = Math.floor(angry / sum * 1000) / 10.0;
        sad = Math.floor(sad / sum * 1000) / 10.0;
        happy = Math.floor(happy / sum * 1000) / 10.0;

        emotionResponseDto.setType(type);
        emotionResponseDto.setBreed(top1,top1Value,top2,top2Value,top3,top3Value);
        emotionResponseDto.setEmotion(angry, sad, happy);
        emotionResponseDto.setCropPosition(leftX, leftY, rightX, rightY);

        return emotionResponseDto;
    }

    private String breedNameReplacer(String breedName) {
        switch (breedName) {
            case "Chihuaha":
                return "치와와";
            case "Japanese Spaniel":
                return "재패니즈 스패니얼";
            case "Maltese Dog":
                return "말티즈";
            case "Pekinese":
                return "페키니즈";
            case "Shih-Tzu":
                return "시츄";
            case "Blenheim Spaniel":
                return "킹 찰스 스패니얼";
            case "Papillon":
                return "파피용";
            case "Toy Terrier":
                return "러스키 토이";
            case "Rhodesian Ridgeback":
                return "로디지안 리지백";
            case "Afghan Hound":
                return "아프간 하운드";
            case "Basset Hound":
                return "바셋하운드";
            case "Beagle":
                return "비글";
            case "Bloodhound":
                return "블러드 하운드";
            case "Bluetick":
                return "블루틱 쿤하운드";
            case "Black-and-tan Coonhound":
                return "블랙 앤 탄 쿤하운드";
            case "Walker Hound":
                return "트리잉 워커 쿤하운드";
            case "English Foxhound":
                return "잉글리시 폭스하운드";
            case "Redbone":
                return "레드본 쿤하운드";
            case "Borzoi":
                return "보르조이";
            case "Irish Wolfhound":
                return "아이리시 울프하운드";
            case "Italian Greyhound":
                return "이탈리안 그레이하운드";
            case "Whippet":
                return "휘핏";
            case "Ibizian Hound":
                return "이비전 하운드";
            case "Norwegian Elkhound":
                return "노르웨이언 엘크하운드";
            case "Otterhound":
                return "오터 하운드";
            case "Saluki":
                return "살루키";
            case "Scottish Deerhound":
                return "스코티시 디어하운드";
            case "Weimaraner":
                return "와이머라너";
            case "Staffordshire Bullterrier":
                return "스타포드셔 불 테리어";
            case "American Staffordshire Terrier":
                return "아메리칸 스태퍼드셔 테리어";
            case "Bedlington Terrier":
                return "베들링턴 테리어";
            case "Border Terrier":
                return "보더 테리어";
            case "Kerry Blue Terrier":
                return "케리 블루 테리어";
            case "Irish Terrier":
                return "아이리시 테리어";
            case "Norfolk Terrier":
                return "노퍽 테리어";
            case "Norwich Terrier":
                return "노리치 테리어";
            case "Yorkshire Terrier":
                return "요크셔 테리어";
            case "Wirehaired Fox Terrier":
                return "와이어 폭스 테리어";
            case "Lakeland Terrier":
                return "레이클랜드 테리어";
            case "Sealyham Terrier":
                return "실리엄 테리어";
            case "Airedale":
                return "에어데일 테리어";
            case "Cairn":
                return "케언 테리어";
            case "Australian Terrier":
                return "오스트레일리안 테리어";
            case "Dandi Dinmont":
                return "댄디 딘몬트 테리어";
            case "Boston Bull":
                return "보스턴 테리어";
            case "Miniature Schnauzer":
                return "미니어처 슈나우저";
            case "Giant Schnauzer":
                return "자이언트 슈나우저";
            case "Standard Schnauzer":
                return "스탠더드 슈나우저";
            case "Scotch Terrier":
                return "스코티시 테리어";
            case "Tibetan Terrier":
                return "티베탄 테리어";
            case "Silky Terrier":
                return "오스트레일리안 실키 테리어";
            case "Soft-coated Wheaten Terrier":
                return "아이리쉬 소프트코티드 휘튼 테리어";
            case "West Highland White Terrier":
                return "웨스트 하일랜드 화이트 테리어";
            case "Lhasa":
                return "라사압소";
            case "Flat-coated Retriever":
                return "플랫 코티드 리트리버";
            case "Curly-coater Retriever":
                return "컬리 코티드 리트리버";
            case "Golden Retriever":
                return "골든 리트리버";
            case "Labrador Retriever":
                return "래브라도 리트리버";
            case "Chesapeake Bay Retriever":
                return "체서피크 베이 리트리버";
            case "German Short-haired Pointer":
                return "저먼 쇼트헤어드 포인터";
            case "Vizsla":
                return "비즐라";
            case "English Setter":
                return "르웰린";
            case "Irish Setter":
                return "아이리시 세터";
            case "Gordon Setter":
                return "고든 세터";
            case "Brittany":
                return "브리트니";
            case "Clumber":
                return "클럼버 스파니엘";
            case "English Springer Spaniel":
                return "잉글리시 스프링어 스패니얼";
            case "Welsh Springer Spaniel":
                return "웰시 스프링어 스패니얼";
            case "Cocker Spaniel":
                return "잉글리시 코커 스패니얼";
            case "Sussex Spaniel":
                return "서식스 스패니얼";
            case "Irish Water Spaniel":
                return "아이리시 워터 스패니얼";
            case "Kuvasz":
                return "쿠바츠";
            case "Schipperke":
                return "스키퍼키";
            case "Groenendael":
                return "그루넨달";
            case "Malinois":
                return "벨지안 셰퍼드";
            case "Briard":
                return "브리아드";
            case "Kelpie":
                return "오스트레일리안 켈피";
            case "Komondor":
                return "코몬돌";
            case "Old English Sheepdog":
                return "올드 잉글리시 쉽독";
            case "Shetland Sheepdog":
                return "셔틀랜드 쉽독";
            case "Collie":
                return "콜리";
            case "Border Collie":
                return "보더 콜리";
            case "Bouvier des Flandres":
                return "부비에 데 플랑드르";
            case "Rottweiler":
                return "로트바일러";
            case "German Shepard":
                return "저먼 셰퍼드";
            case "Doberman":
                return "도베르만 핀셔";
            case "Miniature Pinscher":
                return "미니어처 핀셔";
            case "Greater Swiss Mountain Dog":
                return "그레이터 스위스 마운틴 도그";
            case "Bernese Mountain Dog":
                return "버니즈 마운틴 도그";
            case "Appenzeller":
                return "아펜젤러 세넨훈드";
            case "EntleBucher":
                return "엔틀버쳐 마운틴 독";
            case "Boxer":
                return "복서";
            case "Bull Mastiff":
                return "불마스티프";
            case "Tibetan Mastiff":
                return "티베탄 마스티프";
            case "French Bulldog":
                return "프렌치 불도그";
            case "Great Dane":
                return "그레이트 데인";
            case "Saint Bernard":
                return "세인트버나드";
            case "Eskimo Dog":
                return "아메리칸 에스키모 도그";
            case "Malamute":
                return "알래스칸 맬러뮤트";
            case "Siberian Husky":
                return "시베리안 허스키";
            case "Affenpinscher":
                return "아펜핀셔";
            case "Basenji":
                return "바센지";
            case "Pug":
                return "퍼그";
            case "Leonberg":
                return "레온베르거";
            case "Newfoundland":
                return "뉴펀들랜드";
            case "Great Pyrenees":
                return "그레이트 피레니즈";
            case "Samoyed":
                return "사모예드견";
            case "Pomeranian":
                return "포메라니안";
            case "Chow":
                return "차우차우";
            case "Keeshond":
                return "키스혼드";
            case "Brabancon Griffon":
                return "브뤼셀 그리펀";
            case "Pembroke":
                return "펨브록 웰시 코기";
            case "Cardigan":
                return "카디건 웰시 코기";
            case "Toy Poodle":
                return "토이 푸들";
            case "Miniature Poodle":
                return "미니어처 푸들";
            case "Standard Poodle":
                return "스탠다드 푸들";
            case "Mexican Hairless":
                return "멕시칸 헤어리스 도그";
            case "Dingo":
                return "딩고";
            case "Dhole":
                return "승냥이";
            case "African Hunting Dog":
                return "아프리카들개";
            case "bengal":
                return "벵갈";
            case "british shorthair":
                return "브리티시 쇼트헤어";
            case "domestic long-haired":
                return "도메스틱 롱헤어";
            case "domestic short-haired":
                return "도메스틱 쇼트헤어";
            case "maine coon":
                return "메인쿤";
            case "Munchkin":
                return "먼치킨";
            case "Norwegian forest":
                return "노르웨이숲";
            case "persian":
                return "페르시안";
            case "ragdoll":
                return "래그돌";
            case "russian blue":
                return "러시안 블루";
            case "scottish fold":
                return "스코티시 폴드";
            case "selkirk rex":
                return "셀커크 렉스";
            case "siamese":
                return "샴";
            case "sphynx":
                return "스핑크스";
            default:
                return "unknown";
        }

    }
}
