package com.example.cloneInstragram.application.storage.service;

import io.minio.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class MinioService {

    private final MinioClient minioClient;

    public MinioService(@Value("${minio.url}") String url,
                        @Value("${minio.accessKey}") String accessKey,
                        @Value("${minio.secretKey}") String secretKey) {
        this.minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    public String uploadFile(MultipartFile file, String bucketName) {
        try {
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();

            System.out.println("Загружаем файл в MinIO: " + fileName + ", размер: " + file.getSize() + " в бакет: " + bucketName);

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

    // Метод для получения файла из MinIO
    public InputStream getFile(String fileName, String bucketName) {
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
