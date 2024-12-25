package centrikt.factory_monitoring.daily_report.utils.ftp;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FTPUtil {
    String saveFileLocally(MultipartFile file);
    boolean saveFileToFTP(String localFilePath, String remoteFileName);
}
