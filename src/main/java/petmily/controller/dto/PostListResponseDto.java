package petmily.controller.dto;

import lombok.Getter;
import lombok.Setter;
import petmily.domain.posts.Post;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Getter
@Setter
public class PostListResponseDto {

    private Long postId;
    private String email;
    private byte[] postImg;
    private String postContent;
    private String userImg;
    private String tags;

    public PostListResponseDto(Post entity, String userImg) {

        String ec2Path = "/home/ec2-user/petmilyServer/step1/imgDB/post";
        String localPath = "/Users/jookwonyoung/Documents/petmily/testImg/post";

        this.postId = entity.getPostId();
        this.email = entity.getEmail();
        try {
            InputStream in;
            try {
                in = new FileInputStream(ec2Path + "/" + entity.getPostId());
            } catch (IOException e) {
                in = new FileInputStream(localPath + "/" + entity.getPostId());
            }
            this.postImg = in.readAllBytes();
            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.postContent = entity.getPostContent();
        this.tags = entity.getTags();
        this.userImg = userImg;
    }

}
