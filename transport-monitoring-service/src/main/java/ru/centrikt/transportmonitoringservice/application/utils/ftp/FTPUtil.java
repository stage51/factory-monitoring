package ru.centrikt.transportmonitoringservice.application.utils.ftp;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface FTPUtil {
    String saveFileLocally(MultipartFile file);
    boolean saveFileToFTP(String localFilePath, String remoteFileName, FTPReportType reportType);
    boolean fileExists(String remoteFileName, FTPReportType reportType);
    File getFileFromFTP(String remoteFileName, FTPReportType reportType);
}
