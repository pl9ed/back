package tubalubback.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Response;
import tubalubback.services.S3Service;

import javax.websocket.server.PathParam;
import java.util.Optional;

@RestController
@CrossOrigin
public class UploadController {

    @Autowired
    private S3Service s3;

    @GetMapping("/upload")
    public ResponseEntity<String> getPresignedUrl(@PathParam("filename") String filename) {
        return ResponseEntity.ok(s3.presignPutUrl(filename));
    }

    @DeleteMapping("/delete/{filename}")
    public ResponseEntity<Optional<String>> deleteFile(@PathVariable("filename") String filename) {
        SdkHttpResponse resp = s3.deleteObject(filename).sdkHttpResponse();
        return ResponseEntity.status(resp.statusCode()).body(resp.statusText());
    }
}
