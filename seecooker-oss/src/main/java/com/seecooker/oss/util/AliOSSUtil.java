package com.seecooker.oss.util;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyuncs.exceptions.ClientException;
import com.seecooker.common.enums.ImageType;
import com.seecooker.common.exception.BizException;
import com.seecooker.common.exception.ErrorType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 阿里云oss工具类
 *
 * @author xueruichen
 * @date 2023.11.24
 */
public class AliOSSUtil {
    private static final String BUCKET_NAME = "seecooker";
    private static final String ENDPOINT = "https://oss-cn-shanghai.aliyuncs.com";
    private AliOSSUtil() {}
    public static String uploadFile(MultipartFile file, ImageType imageType) throws IOException, ClientException {
        if (file == null) {
            return null;
        }
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, credentialsProvider.getCredentials().getAccessKeyId(), credentialsProvider.getCredentials().getSecretAccessKey());

        String url = uploadFile(file, imageType, ossClient);
        ossClient.shutdown();

        return url;
    }

    public static List<String> uploadFile(MultipartFile[] files, ImageType imageType) throws IOException, ClientException {
        if (files == null) {
            return Collections.emptyList();
        }
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, credentialsProvider.getCredentials().getAccessKeyId(), credentialsProvider.getCredentials().getSecretAccessKey());
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            String url = uploadFile(file, imageType, ossClient);
            urls.add(url);
        }
        ossClient.shutdown();
        return urls;
    }

    private static String uploadFile(MultipartFile file, ImageType imageType, OSS ossClient) throws IOException {
        InputStream inputStream = file.getInputStream();
        // 避免文件重名覆盖
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BizException(ErrorType.FILE_NOT_NAMED);
        }
        String filename = imageType.getType() + "/" + UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));

        // 上传文件
        ossClient.putObject(BUCKET_NAME, filename, inputStream);

        return ENDPOINT.split("//")[0] + "//" + BUCKET_NAME + "." + ENDPOINT.split("//")[1] + "/" + filename;
    }

    public static String authorizeAccess(String url) throws ClientException {
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, credentialsProvider);
        Date expiration = new Date(new Date().getTime() + 360 * 1000L);

        // 生成签名URL。
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(BUCKET_NAME, url.substring(url.indexOf('/', BUCKET_NAME.length() + ENDPOINT.length() + 1) + 1), HttpMethod.GET);
        // 设置过期时间。
        request.setExpiration(expiration);
        // 通过HTTP GET请求生成签名URL。
        String signedUrl = String.valueOf(ossClient.generatePresignedUrl(request));
        ossClient.shutdown();
        return signedUrl;
    }

    public static List<String> authorizeAccess(List<String> urls) throws ClientException {
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, credentialsProvider);
        Date expiration = new Date(new Date().getTime() + 360 * 1000L);

        // 生成签名URL。
        List<String> signedUrls = urls.stream().map(url->{
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(BUCKET_NAME, url.substring(url.indexOf('/', BUCKET_NAME.length() + ENDPOINT.length() + 1) + 1), HttpMethod.GET);
            // 设置过期时间。
            request.setExpiration(expiration);
            // 通过HTTP GET请求生成签名URL。
            return String.valueOf(ossClient.generatePresignedUrl(request));
        }).toList();

        ossClient.shutdown();
        return signedUrls;
    }
}
