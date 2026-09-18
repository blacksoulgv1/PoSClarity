package com.gerardgv.posclarity.api;

import com.gerardgv.posclarity.api.dto.report.*;
import com.gerardgv.posclarity.utils.Session;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportApiClient extends BaseApiClient {
    
    private static final String BASE_URL =
            ApiConfig.BASE_URL + "/reports";
    
    public ReportSalesSummaryResponse getMonthlySalesSummary(
            int month,
            int year)
            throws IOException, InterruptedException {

        int branchId = Session.getSucursal().getId();

        String url = BASE_URL
                + "/sales/monthly"
                + "?branchId=" + branchId
                + "&month=" + month
                + "&year=" + year;

        return get(
                url,
                ReportSalesSummaryResponse.class
        );
    }

    public Map<Integer, Double> getDailySales(
            int month,
            int year)
            throws IOException, InterruptedException {

        int branchId = Session.getSucursal().getId();

        String url = BASE_URL
                + "/sales/daily"
                + "?branchId=" + branchId
                + "&month=" + month
                + "&year=" + year;

        List<DailySalesResponse> responses =
                getList(
                        url,
                        DailySalesResponse.class
                );

        Map<Integer, Double> salesByDay = new HashMap<>();

        for (DailySalesResponse response : responses) {

            BigDecimal total = response.getTotal();

            salesByDay.put(
                    response.getDay(),
                    total != null
                            ? total.doubleValue()
                            : 0.0
            );
        }

        return salesByDay;
    }
    
}
