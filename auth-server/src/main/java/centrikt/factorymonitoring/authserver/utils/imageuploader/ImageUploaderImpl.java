package centrikt.factorymonitoring.authserver.utils.imageuploader;

import centrikt.factorymonitoring.authserver.dtos.responses.image.ImageHostUploadResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Component
@Slf4j
public class ImageUploaderImpl implements ImageUploader{

    @Value("${user.image-hosting-api-url}")
    private String imageHostingApiUrl;
    @Value("${user.image-hosting-secret-key}")
    private String imageHostingSecretKey;

    public String saveFile(MultipartFile file) throws IOException {
        File tempFile = convertMultipartFileToFile(file);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", imageHostingSecretKey);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new FileSystemResource(tempFile));

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(imageHostingApiUrl, HttpMethod.POST, requestEntity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new IOException("Error loading image: HTTP status " + response.getStatusCode());
            }

            log.info("Response Body: " + response.getBody());

            ImageHostUploadResponse parsedResponse = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(response.getBody(), ImageHostUploadResponse.class);

            if (parsedResponse == null || !parsedResponse.isSuccess() || parsedResponse.getData() == null) {
                throw new IOException("Error loading image: Invalid response format or empty data");
            }

            return parsedResponse.getData().getLink();
        } finally {
            if (tempFile.exists() && !tempFile.delete()) {
                log.warn("Failed to delete temporary file: " + tempFile.getAbsolutePath());
            }
        }
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        file.transferTo(convFile);
        return convFile;
    }
}
