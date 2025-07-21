package ru.centrikt.transportmonitoringservice.application.services;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface  AbstractService<Request, Response> {
    Response get(Long id);
    List<Response> getAll();
    Page<Response> getPage(int size, int number, String sortBy, String sortDirection, Map<String, String> filters, Map<String, String> dateRanges);
    boolean uploadFile(Long id, MultipartFile file);
    File getFile(Long id);
}
