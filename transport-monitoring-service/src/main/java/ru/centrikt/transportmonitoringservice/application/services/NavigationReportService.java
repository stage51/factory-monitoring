package ru.centrikt.transportmonitoringservice.application.services;


import ru.centrikt.transportmonitoringservice.presentation.dtos.requests.navigation.*;
import ru.centrikt.transportmonitoringservice.presentation.dtos.responses.navigation.NavigationReportResponse;

import java.util.List;
import java.util.Map;

public interface NavigationReportService extends AbstractService<NavigationReportRequest, NavigationReportResponse> {
    NavigationReportResponse create(NavigationReportRequest dto);
    NavigationReportResponse update(Long id, NavigationReportRequest dto);
    void delete(Long id);
    List<NavigationReportResponse> navigate(String taxpayerNumber, NavigationUserNavigateParamsRequest paramsRequest);
    List<NavigationReportResponse> navigate(NavigationAdminNavigateParamsRequest paramsRequest);
    NavigationReportResponse find(String taxpayerNumber, NavigationUserFindParamsRequest paramsRequest);
    NavigationReportResponse find(NavigationAdminFindParamsRequest paramsRequest);
}
