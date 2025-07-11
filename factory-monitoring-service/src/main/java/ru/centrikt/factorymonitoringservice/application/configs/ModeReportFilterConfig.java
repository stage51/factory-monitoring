package ru.centrikt.factorymonitoringservice.application.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.centrikt.factorymonitoringservice.application.utils.filter.FilterUtil;
import ru.centrikt.factorymonitoringservice.application.utils.filter.FilterUtilImpl;
import ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.mode.dateranges.ModeReportEndDateDateRangeHandler;
import ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.mode.dateranges.ModeReportStartDateDateRangeHandler;
import ru.centrikt.factorymonitoringservice.application.utils.filter.handlers.mode.filters.*;
import ru.centrikt.factorymonitoringservice.domain.models.mode.ModeReport;

import java.util.List;

@Configuration
public class ModeReportFilterConfig {
    @Bean
    public FilterUtil<ModeReport> ModeReportFilterUtil(ModeReportProductFilterHandler productFilterHandler,
                                                       ModeReportSensorNumberFilterHandler sensorNumberFilterHandler,
                                                       ModeReportModeFilterHandler modeFilterHandler,
                                                       ModeReportTypeFilterHandler typeFilterHandler,
                                                       ModeReportUnitTypeFilterHandler unitTypeFilterHandler,
                                                       ModeReportStartDateDateRangeHandler modeReportStartDateDateRangeHandler,
                                                       ModeReportEndDateDateRangeHandler modeReportEndDateDateRangeHandler) {

        return new FilterUtilImpl<>(List.of(productFilterHandler, sensorNumberFilterHandler,
                modeFilterHandler, typeFilterHandler, unitTypeFilterHandler), List.of(modeReportStartDateDateRangeHandler, modeReportEndDateDateRangeHandler));
    }
}
