package ru.centrikt.factorymonitoringservice.application.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.centrikt.factorymonitoringservice.application.utils.filter.FilterUtil;
import ru.centrikt.factorymonitoringservice.application.utils.filter.FilterUtilImpl;
import ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.daily.dateranges.DailyReportEndDateDateRangeHandler;
import ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.daily.dateranges.DailyReportStartDateDateRangeHandler;
import ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.daily.filters.*;
import ru.centrikt.factorymonitoringservice.domain.models.daily.DailyReport;

import java.util.List;

@Configuration
public class DailyReportFilterConfig {
    @Bean
    public FilterUtil<DailyReport> DailyReportFilterUtil(DailyReportProductFilterHandler productFilterHandler,
                                                         DailyReportStatusFilterHandler statusFilterHandler,
                                                         DailyReportSensorNumberFilterHandler sensorNumberFilterHandler,
                                                         DailyReportModeFilterHandler modeFilterHandler,
                                                         DailyReportTypeFilterHandler typeFilterHandler,
                                                         DailyReportUnitTypeFilterHandler unitTypeFilterHandler,
                                                         DailyReportStartDateDateRangeHandler dailyReportStartDateDateRangeHandler,
                                                         DailyReportEndDateDateRangeHandler dailyReportEndDateDateRangeHandler) {

        return new FilterUtilImpl<>(List.of(productFilterHandler, statusFilterHandler, sensorNumberFilterHandler,
                modeFilterHandler, typeFilterHandler, unitTypeFilterHandler), List.of(dailyReportStartDateDateRangeHandler, dailyReportEndDateDateRangeHandler));
    }
}
