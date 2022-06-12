package com.example.demo.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class S3Service {
    String UPLOAD_DIR = "C:\\Users\\Shubham Srivastava\\IdeaProjects\\DocPdf\\src\\main\\resources\\templates";


    @Value("${bucketName}")
    private String bucketName ;

    private final AmazonS3 s3 ;

    public S3Service(AmazonS3 s3) {
        this.s3 = s3;
    }


    public String uploadToBucket(MultipartFile multipartFile) throws IOException
    {
        System.out.println(bucketName);
        String OGFilename = multipartFile.getOriginalFilename();
        try {
            File fileConv = convertMultipartToFile(multipartFile);
            PutObjectResult putObjectResult = s3.putObject(bucketName, OGFilename, fileConv);
            return putObjectResult.getContentMd5();
        }
        catch (IOException e)
        {
            throw new RuntimeException("err");
        }

    }


    public void downloadFromBucket(String filename) throws IOException {
        S3Object object = s3.getObject(bucketName , filename);
        S3ObjectInputStream objectContent = object.getObjectContent();

        try {

            Files.copy(objectContent ,
                    Paths.get(UPLOAD_DIR+ File.separator+filename) ,
                    StandardCopyOption.REPLACE_EXISTING);



        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public File convertMultipartToFile(MultipartFile file) throws IOException {
        File conFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(conFile);
        fos.write(file.getBytes());
        fos.close();
        return conFile;
    }


}
