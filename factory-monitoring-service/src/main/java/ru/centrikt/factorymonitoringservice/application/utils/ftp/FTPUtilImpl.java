package ru.centrikt.factorymonitoringservice.application.utils.ftp;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.centrikt.factorymonitoringservice.domain.exceptions.FileDownloadException;
import ru.centrikt.factorymonitoringservice.domain.exceptions.FileExistsException;
import ru.centrikt.factorymonitoringservice.domain.exceptions.FileUploadException;
import ru.centrikt.factorymonitoringservice.domain.exceptions.MethodDisabledException;

import java.io.*;

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

    @Value("${xml-reports.five-minute-report}")
    private Boolean fiveMinuteReport;

    @Value("${xml-reports.mode-report}")
    private Boolean modeReport;

    private final FTPClient ftpClient;

    public FTPUtilImpl() {
        this.ftpClient = new FTPClient();
        log.trace("FTPClient instance created.");
    }

    private void connectIfNotConnected() {
        if (!ftpClient.isConnected()) {
            log.info("Reconnecting to FTP client");
            connect();
        }
    }

    private void connect() {
        log.trace("Entering connect method.");
        try {
            log.info("Connecting to FTP server at {}:{}", ftpHost, ftpPort);
            ftpClient.connect(ftpHost, ftpPort);
            log.debug("Attempting login with username: {}", ftpUsername);
            ftpClient.login(ftpUsername, ftpPassword);
            log.info("Successfully connected to FTP server.");
        } catch (IOException e) {
            log.error("Failed to connect to FTP server.", e);
        }
        log.trace("Exiting connect method.");
    }

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
            throw new FileUploadException("Error occurred while saving file locally: " + e.getMessage());
        } finally {
            log.trace("Exiting saveFileLocally method.");
        }
    }

    @Override
    public boolean saveFileToFTP(String localFilePath, String remoteFileName, FTPReportType reportType) {
        log.trace("Entering saveFileToFTP method with localFilePath: {}, remoteFileName: {}", localFilePath, remoteFileName);
        if (dailyReport && reportType == FTPReportType.DAILY) {
            return uploadFile(localFilePath, remoteFileName, reportType);
        } else if (fiveMinuteReport && reportType == FTPReportType.FIVEMINUTE) {
            return uploadFile(localFilePath, remoteFileName, reportType);
        } else if (modeReport && reportType == FTPReportType.MODE) {
            return uploadFile(localFilePath, remoteFileName, reportType);
        } else {
            log.warn("Attempted to save file to FTP, but FTP uploading is disabled.");
            throw new MethodDisabledException("Saving file to FTP is disabled");
        }
    }

    @Override
    public boolean fileExists(String remoteFileName, FTPReportType reportType) {
        log.trace("Entering file exist method with remoteFileName: {}", remoteFileName);
        connectIfNotConnected();
        try {
            log.debug("Setting FTP client to passive mode and binary file type.");
            ftpClient.changeWorkingDirectory(ftpDirectory + "/" + reportType.toString());
            log.debug("Changed working directory to: {}", ftpDirectory + "/" + reportType.toString());
            ftpClient.enterLocalPassiveMode();
            FTPFile[] files = ftpClient.listFiles();
            for (FTPFile file : files) {
                if (file.getName().equals(remoteFileName)) {
                    return true;
                }
            }
            return false;
        } catch (IOException ex) {
            log.error("Failed to check file from FTP server.", ex);
            throw new FileExistsException("Failed to check file from FTP server: " + ex.getMessage());
        } finally {
            disconnect();
            log.trace("Exiting existFiles method.");
        }
    }

    @Override
    public File getFileFromFTP(String remoteFileName, FTPReportType reportType) {
        log.trace("Entering getFileFromFTP with file: {}", remoteFileName);
        connectIfNotConnected();
        try {
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.changeWorkingDirectory(ftpDirectory + "/" + reportType.toString());

            File localFile = File.createTempFile("ftp_", "_" + remoteFileName);
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localFile))) {
                boolean success = ftpClient.retrieveFile(remoteFileName, outputStream);
                if (!success) {
                    throw new IOException("Could not retrieve file from FTP");
                }
            }

            log.info("File downloaded from FTP to: {}", localFile.getAbsolutePath());
            return localFile;

        } catch (IOException ex) {
            log.error("Failed to download file from FTP", ex);
            throw new FileDownloadException("Failed to download file from FTP server: " + ex.getMessage());
        } finally {
            disconnect();
            log.trace("Exiting getFileFromFTP method.");
        }
    }


    private boolean uploadFile(String localFilePath, String remoteFileName, FTPReportType reportType) {
        log.trace("Entering uploadFile method with localFilePath: {}, remoteFileName: {}", localFilePath, remoteFileName);
        connectIfNotConnected();
        try {
            log.debug("Setting FTP client to passive mode and binary file type.");
            ftpClient.changeWorkingDirectory(ftpDirectory + "/" + reportType.toString());
            log.debug("Changed working directory to: {}", ftpDirectory + "/" + reportType.toString());
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
            throw new FileUploadException("Error occurred while saving file locally: " + ex.getMessage());
        } finally {
            disconnect();
            log.trace("Exiting uploadFile method.");
        }
    }
}
