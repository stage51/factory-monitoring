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
public class ImageUploaderImpl implements ImageUploader {

    @Value("${user.image-hosting-api-url}")
    private String imageHostingApiUrl;
    @Value("${user.image-hosting-secret-key}")
    private String imageHostingSecretKey;

    public String saveFile(MultipartFile file) throws IOException {
        log.trace("Entered saveFile method");

        File tempFile = convertMultipartFileToFile(file);
        log.debug("Converted MultipartFile to File: {}", tempFile.getAbsolutePath());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.set("Authorization", imageHostingSecretKey);
            log.debug("HTTP headers set for request: {}", headers);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new FileSystemResource(tempFile));
            log.debug("Request body set with image: {}", tempFile.getName());

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            log.debug("Prepared HttpEntity for API call: {}", requestEntity);

            ResponseEntity<String> response = restTemplate.exchange(imageHostingApiUrl, HttpMethod.POST, requestEntity, String.class);
            log.debug("Received response with status: {}", response.getStatusCode());

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Error loading image: HTTP status {}", response.getStatusCode());
                throw new IOException("Error loading image: HTTP status " + response.getStatusCode());
            }

            log.debug("Response Body: {}", response.getBody());

            ImageHostUploadResponse parsedResponse = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(response.getBody(), ImageHostUploadResponse.class);
            log.debug("Parsed response: {}", parsedResponse);

            if (parsedResponse == null || !parsedResponse.isSuccess() || parsedResponse.getData() == null) {
                log.error("Error loading image: Invalid response format or empty data");
                throw new IOException("Error loading image: Invalid response format or empty data");
            }

            log.info("Image uploaded successfully, link: {}", parsedResponse.getData().getLink());
            return parsedResponse.getData().getLink();

        } catch (IOException e) {
            log.error("IOException occurred during image upload: {}", e.getMessage(), e);
            throw e;
        } finally {
            if (tempFile.exists()) {
                if (!tempFile.delete()) {
                    log.warn("Failed to delete temporary file: {}", tempFile.getAbsolutePath());
                } else {
                    log.debug("Temporary file deleted: {}", tempFile.getAbsolutePath());
                }
            } else {
                log.trace("Temporary file does not exist, no deletion needed: {}", tempFile.getAbsolutePath());
            }
        }
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        log.trace("Converting MultipartFile to File");
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        log.debug("Temporary file created at: {}", convFile.getAbsolutePath());
        file.transferTo(convFile);
        return convFile;
    }
}

