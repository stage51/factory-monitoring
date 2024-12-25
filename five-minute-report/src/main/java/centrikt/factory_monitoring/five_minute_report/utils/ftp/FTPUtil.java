package centrikt.factory_monitoring.five_minute_report.utils.ftp;

import org.springframework.web.multipart.MultipartFile;

public interface FTPUtil {
    String saveFileLocally(MultipartFile file);
    boolean saveFileToFTP(String localFilePath, String remoteFileName);
}
