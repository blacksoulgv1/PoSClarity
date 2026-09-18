package com.gerardgv.posclarity.api;

import com.gerardgv.posclarity.api.dto.goal.GoalRequest;
import com.gerardgv.posclarity.models.Goal;
import java.io.IOException;
import java.util.List;


public class GoalApiClient extends BaseApiClient {
    
    private static final String BASE_URL =
            ApiConfig.BASE_URL + "/goals";
    
     /*
     * Consulta todas las metas.
     */
    public List<Goal> getAll()
            throws IOException, InterruptedException {

        return getList(
                BASE_URL,
                Goal.class
        );
    }

    /*
     * Consulta la meta de una sucursal,
     * mes y año específicos.
     *
     * Devuelve null cuando la API responde 404.
     */
    public Goal getMonthly(
            int branchId,
            int month,
            int year)
            throws IOException, InterruptedException {

        return get(
                BASE_URL
                        + "/monthly"
                        + "?branchId=" + branchId
                        + "&month=" + month
                        + "&year=" + year,
                Goal.class
        );
    }
    
        /*
     * Crea o actualiza la meta de una sucursal.
     */
    public Goal save(
            int branchId,
            int month,
            int year,
            double amount)
            throws IOException, InterruptedException {

        GoalRequest request = new GoalRequest(
                month,
                year,
                java.math.BigDecimal.valueOf(amount)
        );

        return post(
                BASE_URL + "?branchId=" + branchId,
                request,
                Goal.class
        );
    }
}
