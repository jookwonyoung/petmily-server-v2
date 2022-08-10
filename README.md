# petmily-server

[Petmily-Android-Application](https://github.com/bagoonichanger/Petmliy_android_app)  
[Petmily-Flask-Server](https://github.com/wolfdate25/Petmily_flask_server)

## 목차

[1. 개요](#1-개요)  
[2. 개발 환경](#2-개발-환경)  
[3. 기능](#3-기능)  
[REST API 한눈에 보기](#Rest-API-한눈에-보기)  
[4. 테이블 구조](#4-테이블-구조)  

## 1. 개요

Petmily 모바일 어플리케이션의 메인 서버이다. 

통신 대상은 클라이언트와 Flask 서버 이고, 모든 기능은 REST API를 기반으로 한다.



## 2. 개발 환경

* AWS EC2 인스턴스, RDS
* IntelliJ IDEA
* Spring Boot

#### build.gradle

```
buildscript {
    ext {   
        springBootVersion = '2.1.7.RELEASE'
    }
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
    }
}
```

* Java 11
* MySQL 8.0.27



## 3. 기능

#### 각 패키지별 기능

|   패키지   |                            기능                            |
| :--------: | :--------------------------------------------------------: |
|   client   |                      Flask 서버 연결                       |
| controller | 외부 요청 응답과 파일처리 / 데이터 이동간 사용될 객체 생성 |
|   domain   |                           DB접근                           |
|  service   |             내부 서비스 로직과 트랜잭션 스케줄             |


#### Rest API 한눈에 보기
<img width="958" alt="스크린샷 2022-05-30 오후 1 41 27" src="https://user-images.githubusercontent.com/95841885/170918550-31211bb8-ac33-44da-8701-da2835b1b936.png">



### 3-1. 로그인

클라이언트에서 구글 로그인을 진행하면 구글 계정 정보를 바탕으로 user를 조회, 생성한다.

#### UserService.java

```java
    @Transactional
    public Long save(UserSaveRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElse(requestDto.toEntity());

        Long id = userRepository.save(user).getUserId();
        return id;
    }
```



### 3-2. 감정 분석

1. 전송받은 이미지를 EC2 로컬에 저장

2. 저장한 이미지 파일의 filePath를 Flask 서버로 전송

   #### AnalysisController.java

   ```java
   EmotionResponseDto result = analysisService.matchEmotionDto(filePath);
   ```

   #### AnalysisService.java

   ```java
   String emotionResult = template.requestEmotion(filePath);
   ```

3. Flask 서버에서 사진 분석 후 ("dog" / "cat" / error) 중 해당하는 결과 return

4. "dog" 또는 "cat" 일 경우 Flask 서버로 분석 요청

   #### AnalysisService.java

   ```java
           if (type.equals("dog")) {
               breedResult = template.requestBreedDog(filePath);
           } else if (type.equals("cat")) {
               breedResult = template.requestBreedCat(filePath);
           } else {
               emotionResponseDto.setMessage("개와 고양이가 포함되어 있지 않은 이미지입니다.");
               return emotionResponseDto;
           }
   ```

5. Flask 서버에서 사진속 동물의 종과 감정을 분석 후 결과 return 

6. 결과를 응답 객체로 매칭 후 클라이언트로 return

   #### AnalysisService.java

   ```java
           emotionResponseDto.setType(type);
           emotionResponseDto.setBreed(top1,top1Value,top2,top2Value,top3,top3Value);
           emotionResponseDto.setEmotion(angry, sad, happy);
           emotionResponseDto.setCropPosition(leftX, leftY, rightX, rightY);
   
           return emotionResponseDto;
   ```

   

### 3-3. 게시글

* 저장

  1. 전송받은 post 이미지를 EC2 로컬 임시 폴더에 저장

     #### PostApiController.java

     ```java
            String tmpPath = postRootPath + "/tmp/" + System.currentTimeMillis();
             File tmpFile = new File(tmpPath);
             try {
                 files.transferTo(tmpFile);
             } catch (Exception e) {
                 e.printStackTrace();
                 returnMessage = "내부 서버 오류 - 파일 저장 실패";
             }
     ```

  2. Flask 서버에서 이미지에 개나 고양이 사진이 포함되어 있는지 확인

     #### PostApiController.java

     ```java
     String result = analysisService.detectAnimal(tmpPath);
     ```

     #### AnalysisService.java

     ```java
        public String detectAnimal(String path) {
             return template.requestDetectAnimal(path);
         }
     ```

  3. 개나 고양이 사진이 있다면 post 저장 객체 생성 후 "강아지" 또는 "고양이" 태그 추가

     #### PostApiController.java

     ```java
     PostSaveRequestDto requestDto = new PostSaveRequestDto();
     requestDto.setEmail(email);
     requestDto.setPostContent(content);
     if (detected.equals("dog")) {
         requestDto.setTags("강아지");
     } else {
         requestDto.setTags("고양이");
     }
     ```

  4. 글 내용을 포함한 저장 객체 DB에 저장

     #### PostApiController.java

     ```java
     Long postId = postService.save(requestDto);
     ```

  5. 이미지를 임시 폴더에서 메인 폴더로 복사 후, 클라이언트에 게시글 저장 완료 메세지 return

     #### PostApiController.java

     ```java
     String filePath = postRootPath + "/" + postId;
     Files.copy(tmpFile.toPath(), new File(filePath).toPath());
     returnMessage = postId.toString();
     ```

  6. Flask 서버에서 동물의 종, 감정 분석 결과 return

  7. 비동기로 return 값을 태그로 추가 후 DB에 반영

     #### PostApiController.java

     ```java
     Thread thread = new Thread(new Runnable() {
     	@Override
     	public void run() {
     	    analysisService.autoTagging(postId, detected, filePath);
     	}
     });
     thread.start();
     ```

     

* 등록되 전체 게시글 조회

  #### PostService.java

  ```java
      @Transactional(readOnly = true)
      public List<PostListResponseDto> findAllDesc() {
          List<PostListResponseDto> result = postRepository.findAllDesc().stream()
                  .map(post -> {
                      return new PostListResponseDto(post, userService.findImgByEmail(post.getEmail()));
                  })
                  .collect(Collectors.toList());
          return result;
      }
  ```

* 사용자가 좋아요 누른 게시글 조회

  #### PostService.java

  ```java
      @Transactional(readOnly = true)
      public List<PostListResponseDto> findAllMyLikePost(List<Long> likes) {
          List<PostListResponseDto> result = postRepository.findAllDesc().stream()
                  .map(post -> {
                      return new PostListResponseDto(post, userService.findImgByEmail(post.getEmail()));
                  })
                  .filter(post -> likes.contains(post.getPostId()))
                  .collect(Collectors.toList());
          return result;
      }
  ```

  

* 게시글 삭제

  #### PostService.java

  ```java
      public String delete(Long postId, String email) {
          Post post = postRepository.findById(postId).orElseThrow(
                  () -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + postId));
  
          try{//삭제 성공
              if(post.getEmail().equals(email)) {
                  postRepository.delete(post);
                  return "게시글이 삭제되었습니다.";
              }else{
                  return "자신의 게시글만 삭제할 수 있습니다.";
              }
          }catch(Exception e){//삭제 실패
              return "게시글이 삭제되지 않았습니다.";
          }
      }
  ```

  





### 3-4. 댓글

* 게시글, 사용자 정보로 새로운 댓글 저장

  #### CommentApiController.java

  ```java
      @PostMapping("/save")
      public Long save(@RequestHeader(value = "email") String email, @RequestParam("postId") Long postId,
                       @RequestParam("commentContent") String commentContent, @RequestParam("userImg") String userImg) {
  
          CommentSaveRequestDto requestDto = new CommentSaveRequestDto();
          requestDto.setEmail(email);
          requestDto.setPostId(postId);
          requestDto.setCommentContent(commentContent);
  
          return commentService.save(requestDto);
      }
  ```

* 한 게시글에 대한 모든 댓글 return

  #### commentService.java

  ```java
      @Transactional(readOnly = true)
      public List<CommentListResponseDto> findAllDesc(Long postId) {
          List<CommentListResponseDto> result = commentRepository.findAll().stream()
                  .map(comment -> {
                      return new CommentListResponseDto(comment, userService.findImgByEmail(comment.getEmail()));
                  })
                  .filter(comment -> comment.getPostId() == postId)
                  .collect(Collectors.toList());
  
          return result;
      }
  ```

  

### 3-5. 좋아요

* 좋아요 등록

  #### LikeService.java

  ```java
      public String createLike(LikeSaveRequestDto requestDto) {
          try {
              if (likeRepository.check(requestDto.getEmail(), requestDto.getPostId()).isPresent()) {
                  return "이미 좋아요 누른 게시글입니다.";
              } else {
                  likeRepository.save(requestDto.toEntity());
                  return "좋아요 성공!!";
              }
          }catch (Exception e){
              return "내부 서버 오류 - 좋아요 실패";
          }
  ```

* 좋아요 취소

  #### LikeService.java

  ```java
      public void delete(String email, Long postId) {
          try {
              Like like = likeRepository.findByEmailPostId(email, postId);
              likeRepository.delete(like);
          } catch (Exception e) {
          }
      }
  ```

* 특정 게시글에 대한 내 좋아요 여부

  좋아요 누른 게시글이면 1, 누르지 않은 게시글이면 0 리턴

  #### LikeService.java

  ```java
      public int findMyLike(String email, Long postId) {
          if (likeRepository.check(email, postId).isPresent()) {
              return 1;
          } else {
              return 0;
          }
      }
  ```

* 특정 게시글에 대한 전체 좋아요 수

  #### LikeRepository.java

  ```java
  public interface LikeRepository extends JpaRepository<Like, Long> {
  
      @Query("SELECT COUNT(l) FROM Like l WHERE l.postId = :postId")
      int countLike(@Param("postId") Long postId);
      
  }
  ```

* 특정 게시글에 대해 좋아요 누른 사용자

  #### LikeService.java

  ```java
  public List<Long> findLikedPost(String email) {
          return likeRepository.findByEmail(email).stream()
                  .map(like -> like.getPostId()).
            collect(Collectors.toCollection(ArrayList::new));
  }
  ```

  

### 3-6. 즐겨찾기 장소

즐겨찾기 장소 등록 / 등록한 전체 즐겨찾기 장소 / 즐겨찾기 장소 취소

#### PlaceService.java

```java
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
```

### 3-7. 산책

* 산책 기록 저장

  1. 요청받은 데이터로 산책 기록 저장 객체 생성 후 저장(산책 id = 사진이름)

     #### WalkService.java

     ```java
         @Transactional
         public Long save(WalkSaveRequestDto requestDto) {
             Long id = walkRepository.save(requestDto.toEntity()).getWalkId();
             Walk walk = walkRepository.findById(id).orElseThrow(() -> new IllegalArgumentException(String.valueOf(id)));
             walk.update(id);
             return id;
         }
     ```

     

  2. 산책 이미지를 EC2 로컬 폴더에 저장(폴더명은 사용자 이메일, 사진 파일명은 산책 ID와 동일)

     #### WalkApuController.java

     ```java
     		@PostMapping("/save")
         public Long save( ... ) {
             
           	...
             
             if (!new File(emailPath).exists()) {
                 try {
                     new File(emailPath).mkdir();
                 } catch (Exception e) {
                     e.getStackTrace();
                 }
             }
     
             String filePath = emailPath + "/" + walkId;
             try {
                 files.transferTo(new File(filePath));
             } catch (Exception e) {
                 e.printStackTrace();
             }
     
             return walkId;
         }
     ```

* 사용자의 전체 산책 기록 return / 한 날짜에 대한 사용자의 전체 산책 기록 return / 산책 기록 삭제

  #### WalkService.java

  ```java
      @Transactional(readOnly = true)
      public List<WalkImgListResponseDto> findAllDesc(String email) {
          return walkRepository.findAllDesc(email).stream()
                  .map(WalkImgListResponseDto::new)
                  .collect(Collectors.toList());
      }
  
      @Transactional(readOnly = true)
      public List<WalkImgListResponseDto> findDateDesc(int year, int month, int day, String email) {
          return walkRepository.findDateDesc(year, month, day, email).stream()
                  .map(WalkImgListResponseDto::new)
                  .collect(Collectors.toList());
      }
  
      @Transactional
      public void delete(int id, int year, int month, int day, String email) {
          Walk walk = walkRepository.findByEmail(id, year, month, day, email);
          walkRepository.delete(walk);
      }
  
  ```

  
  

## 4. 테이블 구조

![스크린샷 2022-06-01 오전 1 58 10](https://user-images.githubusercontent.com/95841885/171231506-e4899f1f-4a6e-4cf5-992d-f81713112192.png)

