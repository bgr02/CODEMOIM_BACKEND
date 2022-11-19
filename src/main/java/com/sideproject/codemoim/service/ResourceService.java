package com.sideproject.codemoim.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.sideproject.codemoim.property.CustomProperties;
import com.sideproject.codemoim.property.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final AmazonS3Client amazonS3Client;
    private final S3Properties s3Properties;
    private final CustomProperties customProperties;

    public String upload(String dirName, MultipartFile uploadFile) throws IOException {
        String fileName = dirName + "/" + createFileName(uploadFile.getOriginalFilename());
        putS3(fileName, uploadFile);

        return customProperties.getCookieConfig().getProtocol() + "://" + s3Properties.getCloudFront().getDistributionDomain() + "/" + fileName;
    }

    public void delete(Map<String, Object> imgUrlInfo) {
        String imgUrl = (String) imgUrlInfo.get("imgUrl");

        String[] splitImgUrl = imgUrl.split("/");

        StringBuilder key = new StringBuilder();

        for (int i = 3; i < splitImgUrl.length; i++) {
            if (i != splitImgUrl.length - 1) {
                key.append(splitImgUrl[i]).append("/");
            } else {
                key.append(splitImgUrl[i]);
            }
        }

        boolean isExistObject = amazonS3Client.doesObjectExist(s3Properties.getS3().getBucket(), key.toString());

        if (isExistObject) {
            amazonS3Client.deleteObject(s3Properties.getS3().getBucket(), key.toString());
        }
    }

    private String createFileName(String originalFileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(originalFileName));
    }

    private String getFileExtension(String fileName) {
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(String.format("Invalid format of file(%s).", fileName));
        }
    }

    private void putS3(String fileName, MultipartFile uploadFile) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(uploadFile.getContentType());

        byte[] bytes = IOUtils.toByteArray(uploadFile.getInputStream());
        objectMetadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

        amazonS3Client.putObject(
                new PutObjectRequest(s3Properties.getS3().getBucket(), fileName, byteArrayInputStream, objectMetadata).withCannedAcl(CannedAccessControlList.Private)
        );
    }

}
