package ru.centrikt.transportmonitoringservice.application.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.centrikt.transportmonitoringservice.application.utils.filter.FilterUtil;
import ru.centrikt.transportmonitoringservice.application.utils.filter.FilterUtilImpl;
import ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.daily.dateranges.DailyReportEndDateDateRangeHandler;
import ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.daily.dateranges.DailyReportStartDateDateRangeHandler;
import ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.daily.filters.*;
import ru.centrikt.transportmonitoringservice.domain.models.daily.DailyReport;

import java.util.List;

@Configuration
public class DailyReportFilterConfig {
    @Bean
    public FilterUtil<DailyReport> DailyReportFilterUtil(DailyReportProductFilterHandler productFilterHandler,
                                                         DailyReportTaxpayerNumberFilterHandler taxpayerNumberFilterHandler,
                                                         DailyReportStatusFilterHandler statusFilterHandler,
                                                         DailyReportGovNumberFilterHandler serialNumberFilterHandler,
                                                         DailyReportModeFilterHandler modeFilterHandler,
                                                         DailyReportUnitTypeFilterHandler unitTypeFilterHandler,
                                                         DailyReportStartDateDateRangeHandler dailyReportStartDateDateRangeHandler,
                                                         DailyReportEndDateDateRangeHandler dailyReportEndDateDateRangeHandler) {

        return new FilterUtilImpl<>(
                List.of(productFilterHandler, statusFilterHandler, serialNumberFilterHandler,
                modeFilterHandler, unitTypeFilterHandler, taxpayerNumberFilterHandler),
                List.of(dailyReportStartDateDateRangeHandler, dailyReportEndDateDateRangeHandler));
    }
}
