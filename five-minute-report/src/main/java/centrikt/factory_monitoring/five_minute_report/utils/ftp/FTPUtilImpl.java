package centrikt.factory_monitoring.five_minute_report.utils.ftp;

import centrikt.factory_monitoring.five_minute_report.exceptions.MethodDisabledException;
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

    @Value("${xml-reports.mode-report}")
    private Boolean modeReport;

    private final FTPClient ftpClient;

    public FTPUtilImpl() {
        this.ftpClient = new FTPClient();
        log.trace("FTPClient instance created.");
    }

    @PostConstruct
    private void connect() {
        log.trace("Entering connect method.");
        try {
            log.info("Connecting to FTP server at {}:{}", ftpHost, ftpPort);
            ftpClient.connect(ftpHost, ftpPort);
            log.debug("Attempting login with username: {}", ftpUsername);
            ftpClient.login(ftpUsername, ftpPassword);
            log.info("Successfully connected to FTP server.");
            ftpClient.changeWorkingDirectory(ftpDirectory + "/mode");
            log.debug("Changed working directory to: {}", ftpDirectory + "/mode");
        } catch (IOException e) {
            log.error("Failed to connect to FTP server.", e);
        }
        log.trace("Exiting connect method.");
    }

    @PreDestroy
    private void disconnect() {
        log.trace("Entering disconnect method.");
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
                log.info("Disconnected from FTP server.");
            } else {
                log.warn("Disconnect called, but FTP client is not connected.");
            }
        } catch (IOException ex) {
            log.error("Failed to disconnect from FTP server.", ex);
        }
        log.trace("Exiting disconnect method.");
    }

    @Override
    public String saveFileLocally(MultipartFile file) {
        log.trace("Entering saveFileLocally method with file: {}", file.getOriginalFilename());
        try {
            log.trace("Saving file locally");
            String uploadDir = System.getProperty("java.io.tmpdir");
            log.debug("Temporary directory resolved as: {}", uploadDir);
            File localFile = new File(uploadDir, file.getOriginalFilename());
            file.transferTo(localFile);
            log.info("File uploaded successfully with path: {}", localFile.getAbsolutePath());
            return localFile.getAbsolutePath();
        } catch (IOException e) {
            log.error("Error occurred while saving file locally: {}", e.getMessage(), e);
        }
        log.trace("Exiting saveFileLocally method.");
        return file.getOriginalFilename();
    }

    @Override
    public boolean saveFileToFTP(String localFilePath, String remoteFileName) {
        log.trace("Entering saveFileToFTP method with localFilePath: {}, remoteFileName: {}", localFilePath, remoteFileName);
        if (modeReport) {
            return uploadFile(localFilePath, remoteFileName);
        } else {
            log.warn("Attempted to save file to FTP, but FTP uploading is disabled.");
            throw new MethodDisabledException("Saving file to FTP is disabled");
        }
    }

    private boolean uploadFile(String localFilePath, String remoteFileName) {
        log.trace("Entering uploadFile method with localFilePath: {}, remoteFileName: {}", localFilePath, remoteFileName);
        try {
            log.debug("Setting FTP client to passive mode and binary file type.");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            File localFile = new File(localFilePath);
            log.debug("Preparing to upload file: {}", localFile.getAbsolutePath());

            try (FileInputStream fis = new FileInputStream(localFile)) {
                if (ftpClient.storeFile(remoteFileName, fis)) {
                    log.info("File uploaded to FTP successfully with name: {}", remoteFileName);
                    return true;
                } else {
                    log.error("File upload failed. Remote file name: {}, Reply code: {}, Reply string: {}",
                            remoteFileName, ftpClient.getReplyCode(), ftpClient.getReplyString());
                    return false;
                }
            }
        } catch (IOException ex) {
            log.error("Failed to upload file to FTP server.", ex);
            return false;
        } finally {
            log.trace("Exiting uploadFile method.");
        }
    }
}
