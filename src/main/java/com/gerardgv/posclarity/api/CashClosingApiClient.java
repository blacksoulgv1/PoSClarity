package com.gerardgv.posclarity.api;

import com.gerardgv.posclarity.api.dto.cashclosing.CashClosingRequest;
import com.gerardgv.posclarity.api.dto.cashclosing.CashClosingResponse;
import com.gerardgv.posclarity.models.Branch;
import com.gerardgv.posclarity.models.CashClosing;
import com.gerardgv.posclarity.utils.Session;
import java.io.IOException;
import java.math.BigDecimal;


public class CashClosingApiClient extends BaseApiClient {
    
    private static final String BASE_URL =
            ApiConfig.BASE_URL + "/cash-closings";

    public BigDecimal getInitialCash()
            throws IOException, InterruptedException {

        Integer branchId = Session.getSucursal().getId();

        return get(
                BASE_URL + "/initial-cash?branchId=" + branchId,
                BigDecimal.class
        );
    }

    public CashClosing getTodayClosing()
            throws IOException, InterruptedException {

        Integer branchId = Session.getSucursal().getId();

        CashClosingResponse response = get(
                BASE_URL + "/today?branchId=" + branchId,
                CashClosingResponse.class
        );
        System.out.println("=== CASH CLOSING RESPONSE ===");
System.out.println("newSalesDetail: " + response.getNewSalesDetail());
System.out.println("deliveredSalesDetail: " + response.getDeliveredSalesDetail());
System.out.println("paymentsDetail: " + response.getPaymentsDetail());

        return convert(response);
    }

    private CashClosing convert(
            CashClosingResponse response) {

        CashClosing closing = new CashClosing();
        
        if (response.getBranchId() != null) {

            Branch branch = new Branch();

            branch.setId(response.getBranchId());
            branch.setName(response.getBranchName());

            closing.setBranch(branch);
        }   

        closing.setDate(response.getDate());
        closing.setClosingTime(response.getClosingTime());

        closing.setInitialCash(
                response.getInitialCash() != null
                        ? response.getInitialCash().doubleValue()
                        : 0.0
        );

        closing.setFinalCash(
                response.getFinalCash() != null
                        ? response.getFinalCash().doubleValue()
                        : 0.0
        );

        closing.setTotalSales(
                response.getTotalSales() != null
                        ? response.getTotalSales().doubleValue()
                        : 0.0
        );

        closing.setTotalPayments(
                response.getTotalPayments() != null
                        ? response.getTotalPayments().doubleValue()
                        : 0.0
        );

        closing.setTotalCash(
                response.getTotalCash() != null
                        ? response.getTotalCash().doubleValue()
                        : 0.0
        );

        closing.setTotalTransfer(
                response.getTotalTransfer() != null
                        ? response.getTotalTransfer().doubleValue()
                        : 0.0
        );

        closing.setTotalCard(
                response.getTotalCard() != null
                        ? response.getTotalCard().doubleValue()
                        : 0.0
        );

        closing.setTotalIncome(
                response.getTotalIncome() != null
                        ? response.getTotalIncome().doubleValue()
                        : 0.0
        );

        closing.setDeposit(
                response.getDeposit() != null
                        ? response.getDeposit().doubleValue()
                        : 0.0
        );

        closing.setDifference(
                response.getDifference() != null
                        ? response.getDifference().doubleValue()
                        : 0.0
        );

        closing.setObservations(
                response.getObservations()
        );

        closing.setNewSalesDetail(
                response.getNewSalesDetail()
        );

        closing.setDeliveredSalesDetail(
                response.getDeliveredSalesDetail()
        );

        closing.setPaymentsDetail(
                response.getPaymentsDetail()
        );
        System.out.println("=== AFTER CONVERT SETTERS ===");
System.out.println("NewSalesDetail: " + closing.getNewSalesDetail());
System.out.println("DeliveredSalesDetail: " + closing.getDeliveredSalesDetail());
System.out.println("PaymentsDetail: " + closing.getPaymentsDetail());
        closing.setNewSalesCount( response.getNewSalesCount() );

        return closing;
    }
    
    public boolean existsToday()
        throws IOException, InterruptedException {

        Integer branchId = Session.getSucursal().getId();

        return get(
            BASE_URL + "/exists?branchId=" + branchId,
            Boolean.class
        );
    }
    
    public CashClosing getLatestClosing()
        throws IOException, InterruptedException {

        Integer branchId = Session.getSucursal().getId();

        CashClosingResponse response = get(
            BASE_URL + "/latest?branchId=" + branchId,
            CashClosingResponse.class
        );

        return convert(response);
    }
    
    public CashClosing saveClosing(CashClosing closing)
        throws IOException, InterruptedException {

        Integer branchId = Session.getSucursal().getId();

        CashClosingRequest request = new CashClosingRequest();

        request.setInitialCash(closing.getInitialCash());
        request.setFinalCash(closing.getFinalCash());
        request.setTotalSales(closing.getTotalSales());
        request.setTotalPayments(closing.getTotalPayments());
        request.setTotalCash(closing.getTotalCash());
        request.setTotalTransfer(closing.getTotalTransfer());
        request.setTotalCard(closing.getTotalCard());
        request.setDeposit(closing.getDeposit());
        request.setDifference(closing.getDifference());
        request.setObservations(closing.getObservations());

        CashClosingResponse response = post(
            BASE_URL + "?branchId=" + branchId,
            request,
            CashClosingResponse.class
        );

        return convert(response);
    }
}
