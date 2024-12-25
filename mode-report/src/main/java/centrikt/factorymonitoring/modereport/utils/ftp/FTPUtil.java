package centrikt.factorymonitoring.modereport.utils.ftp;

import org.springframework.web.multipart.MultipartFile;

public interface FTPUtil {
    String saveFileLocally(MultipartFile file);
    boolean saveFileToFTP(String localFilePath, String remoteFileName);
}
