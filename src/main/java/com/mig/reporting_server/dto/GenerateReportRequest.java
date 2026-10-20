package com.mig.reporting_server.dto;


import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;
import java.util.Map;

public record GenerateReportRequest(

        @NotNull
        String reportName,

        Map<String, Object> parameters,

        List<Map<String, Object>> data

) {
}

