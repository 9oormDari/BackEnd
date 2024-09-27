package com.goormdari.global.config.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class S3Service {
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private String changedImageName(String originName) { //이미지 이름 중복 방지를 위해 랜덤으로 생성
        String random = UUID.randomUUID().toString();
        return random +'-' + originName;
    }

    public String uploadImageToS3(MultipartFile image) { //이미지를 S3에 업로드하고 이미지의 url을 반환
        String originName = image.getOriginalFilename(); //원본 이미지 이름
        String ext = originName.substring(originName.lastIndexOf(".")); //확장자
        String changedName = changedImageName(originName); //새로 생성된 이미지 이름
        ObjectMetadata metadata = new ObjectMetadata(); //메타데이터
        metadata.setContentLength(image.getSize()); // 파일의 크기 설정
        metadata.setContentType("image/" + ext);
        try {
            PutObjectResult putObjectResult = amazonS3.putObject(new PutObjectRequest(
                    bucket, changedName, image.getInputStream(), metadata
            ));
        } catch (IOException e) {
            throw new RuntimeException("ImageUploadException"); //커스텀 예외 던짐.
        }
        return amazonS3.getUrl(bucket, changedName).toString(); //데이터베이스에 저장할 이미지가 저장된 주소
    }

    //삭제는 일단 보류
    public void deleteImageOnS3(String imgURL) {
        String fileName = imgURL.substring(imgURL.lastIndexOf("-"));;

        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }
}
