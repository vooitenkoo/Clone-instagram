package com.example.cloneInstragram.services;


import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class MinioService {

    private final MinioClient minioClient;
    private final String bucketName;

    public MinioService(@Value("${minio.url}") String url,
                        @Value("${minio.accessKey}") String accessKey,
                        @Value("${minio.secretKey}") String secretKey,
                        @Value("${minio.bucket}") String bucket) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
        this.bucketName = bucket;
    }

    public String uploadFile(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();

            System.out.println("Загружаем файл в MinIO: " + fileName + ", размер: " + file.getSize());

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            System.out.println("Файл загружен: " + fileName);
            return "/" + bucketName + "/" + fileName;
        } catch (Exception e) {
            e.printStackTrace(); // Вывести ошибку в консоль
            throw new RuntimeException("Ошибка загрузки файла в MinIO", e);
        }
    }


    public InputStream getFile(String fileName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки файла", e);
        }
    }
}
