package centrikt.factory_monitoring.daily_report.utils.ftp;

import centrikt.factory_monitoring.daily_report.exceptions.MethodDisabledException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Component
@RefreshScope
@Slf4j
public class FTPUtilImpl implements FTPUtil {

    @Value("${xml-reports.ftp-host}")
    private String ftpHost;
    @Value("${xml-reports.ftp-port}")
    private int ftpPort;
    @Value("${xml-reports.ftp-username}")
    private String ftpUsername;
    @Value("${xml-reports.ftp-password}")
    private String ftpPassword;
    @Value("${xml-reports.ftp-directory}")
    private String ftpDirectory;

    @Value("${xml-reports.daily-report}")
    private Boolean dailyReport;

    private final FTPClient ftpClient;

    public FTPUtilImpl() {
        this.ftpClient = new FTPClient();
    }

    @PostConstruct
    private void connect() {
        try {
            log.info("Connecting to FTP server...");
            ftpClient.connect(ftpHost, ftpPort);
            ftpClient.login(ftpUsername, ftpPassword);
            log.info("Connected to FTP server");
            ftpClient.changeWorkingDirectory(ftpDirectory + "/daily");
        } catch (IOException e) {
            log.error("Failed to connect to FTP server", e);
        }
    }

    @PreDestroy
    private void disconnect() {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
                log.info("Disconnected from FTP server");
            }
        } catch (IOException ex) {
            log.error("Failed to disconnect from FTP server", ex);
        }
    }

    @Override
    public String saveFileLocally(MultipartFile file){
        try {
            log.info("Saving file locally...");
            String uploadDir = System.getProperty("java.io.tmpdir");
            File localFile = new File(uploadDir, file.getOriginalFilename());
            file.transferTo(localFile);
            log.info("File uploaded successfully with path: " + localFile.getAbsolutePath());
            return localFile.getAbsolutePath();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return file.getOriginalFilename();
    }

    @Override
    public boolean saveFileToFTP(String localFilePath, String remoteFileName) {
        if (dailyReport) {
            return uploadFile(localFilePath, remoteFileName);
        } else throw new MethodDisabledException("Saving file to FTP is disabled");
    }

    private boolean uploadFile(String localFilePath, String remoteFileName) {
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            File localFile = new File(localFilePath);
            try (FileInputStream fis = new FileInputStream(localFile)) {
                if (ftpClient.storeFile(remoteFileName, fis)) {
                    log.info("File uploaded to ftp successfully with name: " + remoteFileName);
                    return true;
                } else {
                    log.error("File uploading failed with name: {}, code: {}, status: {}", remoteFileName, ftpClient.getReplyCode(), ftpClient.getReplyString());
                    return false;
                }
            }
        } catch (IOException ex) {
            log.error("Failed to upload file to FTP server", ex);
            return false;
        }
    }
}
