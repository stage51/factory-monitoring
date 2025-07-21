package ru.centrikt.transportmonitoringservice.application.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.centrikt.transportmonitoringservice.application.utils.filter.FilterUtil;
import ru.centrikt.transportmonitoringservice.application.utils.filter.FilterUtilImpl;
import ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.navigation.dateranges.NavigationReportNavigationDateDateRangeHandler;
import ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.navigation.filters.NavigationReportGovNumberFilterHandler;
import ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.navigation.filters.NavigationReportOrganizationNameFilterHandler;
import ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.navigation.filters.NavigationReportStatusFilterHandler;
import ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.navigation.filters.NavigationReportTaxpayerNumberFilterHandler;
import ru.centrikt.transportmonitoringservice.domain.models.navigation.NavigationReport;

import java.util.List;

@Configuration
public class NavigationReportFilterConfig {
    @Bean
    public FilterUtil<NavigationReport> NavigationReportFilterUtil(NavigationReportStatusFilterHandler statusFilterHandler,
                                                                   NavigationReportGovNumberFilterHandler govNumberFilterHandler,
                                                                   NavigationReportTaxpayerNumberFilterHandler taxpayerNumberFilterHandler,
                                                                   NavigationReportOrganizationNameFilterHandler organizationNameFilterHandler,
                                                                   NavigationReportNavigationDateDateRangeHandler navigationDateDateRangeHandler) {

        return new FilterUtilImpl<>(
                List.of(statusFilterHandler, govNumberFilterHandler, taxpayerNumberFilterHandler, organizationNameFilterHandler),
                List.of(navigationDateDateRangeHandler));
    }
}
