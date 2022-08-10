package petmily.controller.dto;

import lombok.Getter;

@Getter
public class EmotionResponseDto {

    private String message;
    private String type;
    private CropPosition cropPosition;
    private Breed breed;
    private Emotion emotion;

    public EmotionResponseDto() {
        this.emotion = null;
        this.breed = null;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCropPosition(int leftX, int leftY, int rightX, int rightY) {
        this.cropPosition = new CropPosition(leftX, leftY, rightX, rightY);
    }

    public void setEmotion(double angry, double sad, double happy) {
        this.emotion = new Emotion(angry, sad, happy);
    }

    public void setBreed(String top1, Double top1_prob, String top2, Double top2_prob, String top3, Double top3_prob) {
        this.breed = new Breed(top1, top1_prob, top2, top2_prob, top3, top3_prob);
    }
}

@Getter
class Breed {
    private String top1;
    private double top1_result;
    private String top2;
    private double top2_result;
    private String top3;
    private double top3_result;

    public Breed(String top1, Double top1_prob, String top2, Double top2_prob, String top3, Double top3_prob) {
        this.top1 = top1;
        this.top1_result = top1_prob;
        this.top2 = top2;
        this.top2_result = top2_prob;
        this.top3 = top3;
        this.top3_result = top3_prob;
    }
}

@Getter
class Emotion {
    private double angry;
    private double sad;
    private double happy;


    public Emotion(double angry, double sad, double happy) {
        this.angry = angry;
        this.sad = sad;
        this.happy = happy;
    }
}
@Getter
class CropPosition {
    private int leftX;
    private int leftY;
    private int rightX;
    private int rightY;

    public CropPosition(int leftX, int leftY, int rightX, int rightY) {
        this.leftX = leftX;
        this.leftY = leftY;
        this.rightX = rightX;
        this.rightY = rightY;
    }
}