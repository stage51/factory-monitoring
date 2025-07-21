package ru.centrikt.transportmonitoringservice.application.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.centrikt.transportmonitoringservice.application.utils.filter.FilterUtil;
import ru.centrikt.transportmonitoringservice.application.utils.filter.FilterUtilImpl;
import ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.mode.dateranges.ModeReportEndDateDateRangeHandler;
import ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.mode.dateranges.ModeReportStartDateDateRangeHandler;
import ru.centrikt.transportmonitoringservice.application.utils.filter.handlers.mode.filters.*;
import ru.centrikt.transportmonitoringservice.domain.models.mode.ModeReport;

import java.util.List;

@Configuration
public class ModeReportFilterConfig {
    @Bean
    public FilterUtil<ModeReport> ModeReportFilterUtil(ModeReportProductFilterHandler productFilterHandler,
                                                       ModeReportTaxpayerNumberFilterHandler taxpayerNumberFilterHandler,
                                                       ModeReportGovNumberFilterHandler serialNumberFilterHandler,
                                                       ModeReportModeFilterHandler modeFilterHandler,
                                                       ModeReportUnitTypeFilterHandler unitTypeFilterHandler,
                                                       ModeReportStartDateDateRangeHandler modeReportStartDateDateRangeHandler,
                                                       ModeReportEndDateDateRangeHandler modeReportEndDateDateRangeHandler) {

        return new FilterUtilImpl<>(
                List.of(productFilterHandler, serialNumberFilterHandler,
                modeFilterHandler, unitTypeFilterHandler, taxpayerNumberFilterHandler),
                List.of(modeReportStartDateDateRangeHandler, modeReportEndDateDateRangeHandler));
    }
}
