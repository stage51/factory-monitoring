package ru.centrikt.factorymonitoringservice.application.configs;

import org.springframework.context.annotation.Bean;
import ru.centrikt.factorymonitoringservice.application.utils.filter.FilterUtil;
import ru.centrikt.factorymonitoringservice.application.utils.filter.FilterUtilImpl;
import ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.fiveminute.dateranges.FiveMinuteReportControlDateDateRangeHandler;
import ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.fiveminute.filters.*;
import ru.centrikt.factorymonitoringservice.domain.models.fiveminute.FiveMinuteReport;

import java.util.List;

public class FiveMinuteReportFilterConfig {
    @Bean
    public FilterUtil<FiveMinuteReport> FiveMinuteReportFilterUtil(FiveMinuteReportProductFilterHandler productFilterHandler,
                                                                   FiveMinuteReportStatusFilterHandler statusFilterHandler,
                                                                   FiveMinuteReportSensorNumberFilterHandler sensorNumberFilterHandler,
                                                                   FiveMinuteReportModeFilterHandler modeFilterHandler,
                                                                   FiveMinuteReportTypeFilterHandler typeFilterHandler,
                                                                   FiveMinuteReportUnitTypeFilterHandler unitTypeFilterHandler,
                                                                   FiveMinuteReportControlDateDateRangeHandler fiveMinuteReportControlDateDateRangeHandler) {

        return new FilterUtilImpl<>(List.of(productFilterHandler, statusFilterHandler, sensorNumberFilterHandler,
                modeFilterHandler, typeFilterHandler, unitTypeFilterHandler), List.of(fiveMinuteReportControlDateDateRangeHandler));
    }
}
