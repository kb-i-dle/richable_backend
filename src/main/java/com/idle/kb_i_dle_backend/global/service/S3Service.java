package com.idle.kb_i_dle_backend.global.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.idle.kb_i_dle_backend.config.exception.CustomException;
import com.idle.kb_i_dle_backend.global.codes.ErrorCode;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final AmazonS3Client amazonS3Client;


    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /**
     * multipart 파일 받아서 file로 convert해서 upload함수 호출
     *
     * @param file
     * @param dirName
     * @return
     * @throws IOException
     */
    public String upload(MultipartFile file, String dirName) {
        Optional<File> uploadFile = convert(file);
        if (uploadFile.isPresent()) {
            return upload(uploadFile.get(), dirName);
        } else {
            throw new CustomException(ErrorCode.INVALID_FILE, "파일 변환중 오류");
        }

    }

    public String uploadProfile(MultipartFile file, String userId) {
        Optional<File> uploadFile = convert(file);
        if (uploadFile.isPresent()) {
            return uploadProfile(uploadFile.get(), "profileImages", userId);
        } else {
            throw new CustomException(ErrorCode.INVALID_FILE, "파일 변환중 오류");
        }

    }

    /**
     * file s3에 업로드 후 변환과정중 생성된 파일 삭제
     *
     * @param uploadFile
     * @param dirName
     * @return
     */
    private String upload(File uploadFile, String dirName) {

        String fileName = dirName + "/" + uploadFile.getName();
        String uploadImageUrl = putS3(uploadFile, fileName);

        removeNewFile(uploadFile); // convert() 과정에서 로컬에 생성된 파일 삭제

        return uploadImageUrl;
    }


    private String uploadProfile(File uploadFile, String dirName, String userId) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(userId.getBytes(StandardCharsets.UTF_8));

            // 해시 값을 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder();
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            String fileName = dirName + "/" + hexString;
            String uploadImageUrl = putS3(uploadFile, fileName);

            removeNewFile(uploadFile); // convert() 과정에서 로컬에 생성된 파일 삭제

            return uploadImageUrl;
        } catch (NoSuchAlgorithmException e) {
            throw new CustomException(ErrorCode.NO_SUCH_ALGO, e.getMessage());
        }
        // SHA-256 해싱 알고리즘 사용

    }

    /**
     * 파일 삭제
     *
     * @param targetFile
     */
    private void removeNewFile(File targetFile) {

        String name = targetFile.getName();

        // convert() 과정에서 로컬에 생성된 파일을 삭제
        if (targetFile.delete()) {
            log.info(name + "파일 삭제 완료");
        } else {
            log.info(name + "파일 삭제 실패");
        }
    }

    /**
     * s3업로드
     *
     * @param uploadFile
     * @param fileName
     * @return
     */
    private String putS3(File uploadFile, String fileName) {

        amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, uploadFile) // PublicRead 권한으로 upload
        );

        return amazonS3Client.getUrl(bucket, fileName).toString(); // File의 URL return
    }

    /**
     * multipartfile file로 업로드
     *
     * @param multipartFile
     * @return
     * @throws IOException
     */
    private Optional<File> convert(MultipartFile multipartFile) {

        // 기존 파일 이름으로 새로운 File 객체 생성
        // 해당 객체는 프로그램이 실행되는 로컬 디렉토리(루트 디렉토리)에 위치하게 됨
        try {
            File convertFile = new File(multipartFile.getOriginalFilename());

            if (convertFile.createNewFile()) { // 해당 경로에 파일이 없을 경우, 새 파일 생성

                try (FileOutputStream fos = new FileOutputStream(convertFile)) {

                    // multipartFile의 내용을 byte로 가져와서 write
                    fos.write(multipartFile.getBytes());
                }
                return Optional.of(convertFile);
            }

            // 새파일이 성공적으로 생성되지 않았다면, 비어있는 Optional 객체를 반환
            return Optional.empty();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new CustomException(ErrorCode.INVALID_FILE);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new CustomException(ErrorCode.INVALID_FILE);
        }

    }

    public void deleteFile(String fileName) {
        amazonS3Client.deleteObject(bucket, fileName);
    }
}
