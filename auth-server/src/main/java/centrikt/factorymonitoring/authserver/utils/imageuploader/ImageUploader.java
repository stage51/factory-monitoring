package centrikt.factorymonitoring.authserver.utils.imageuploader;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageUploader {
    String saveFile(MultipartFile file) throws IOException;
}
