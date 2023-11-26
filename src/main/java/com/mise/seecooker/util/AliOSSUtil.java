package com.mise.seecooker.util;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyuncs.exceptions.ClientException;
import com.mise.seecooker.enums.ImageType;
import com.mise.seecooker.exception.BizException;
import com.mise.seecooker.exception.ErrorType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
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
        InputStream inputStream = file.getInputStream();
        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();;
        // 避免文件重名覆盖
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BizException(ErrorType.FILE_NOT_NAMED);
        }
        String filename = imageType.getType() + "/" + UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));

        // 上传文件
        OSS ossClient = new OSSClientBuilder().build(ENDPOINT, credentialsProvider.getCredentials().getAccessKeyId(), credentialsProvider.getCredentials().getSecretAccessKey());
        ossClient.putObject(BUCKET_NAME, filename, inputStream);

        String url = ENDPOINT.split("//")[0] + "//" + BUCKET_NAME + "." + ENDPOINT.split("//")[1] + "/" + filename;
        ossClient.shutdown();
        return url;
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
}
