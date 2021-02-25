package tubalubback.services;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.time.Duration;

@Service
@Log4j2
public class S3Service {

    public static final String BUCKET_NAME = "tubalub";
    public static final Region S3_REGION = Region.US_EAST_1;
    public static final String BUCKET_PATH = "uploads/";

    private static final S3Client s3 = S3Client.builder().region(S3_REGION).credentialsProvider(DefaultCredentialsProvider.create()).build();
    private static final S3Presigner presigner = S3Presigner.builder().region(S3_REGION).credentialsProvider(DefaultCredentialsProvider.create()).build();

    public boolean deleteObject(String objKey) {
        try {
            DeleteObjectRequest req = DeleteObjectRequest.builder().bucket(BUCKET_NAME).key(BUCKET_PATH+objKey).build();
            DeleteObjectResponse resp = s3.deleteObject(req);
            if (resp.sdkHttpResponse().statusCode() / 100 == 2) {
                return true;
            }
        } catch (S3Exception e) {
            return false;
        }
        return false;
    }

    public boolean deleteFromURL(String url) {
        String filename = url.substring(url.lastIndexOf("/")+1);
        return deleteObject(filename);
    }

    public String presignPutUrl(String filename) {
        log.info("{} : Generated presigned requestion for: {}", System.getenv("AWS_ACCESS_KEY_ID"), filename);
        PresignedPutObjectRequest putReq = presigner.presignPutObject(r -> r.signatureDuration(Duration.ofMinutes(2))
                .putObjectRequest(por -> por.bucket(BUCKET_NAME).key(BUCKET_PATH+filename)));
        //log.info("Presigned URL generated: {}", putReq.url().toString());
        return putReq.url().toString();
    }

}
