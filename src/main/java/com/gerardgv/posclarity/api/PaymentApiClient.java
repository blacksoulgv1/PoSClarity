package com.gerardgv.posclarity.api;


import com.gerardgv.posclarity.api.dto.sale.PaymentCreateRequest;
import com.gerardgv.posclarity.api.dto.sale.PaymentResponse;
import com.gerardgv.posclarity.models.Payment;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class PaymentApiClient extends BaseApiClient {
    
    private static final String BASE_URL = ApiConfig.BASE_URL +"/payments";
    
    public List<Payment> getBySale(int saleId)
            throws IOException, InterruptedException {

        List<PaymentResponse> responses =
                getList(
                        BASE_URL + "/sale/" + saleId,
                        PaymentResponse.class
                );

        List<Payment> payments = new ArrayList<>();

        for (PaymentResponse response : responses) {
            payments.add(convertirPayment(response));
        }

        return payments;
    }
    
     public Payment create(
            int saleId,
            String paymentMethod,
            double amount,
            String reference
    ) throws IOException, InterruptedException {

        PaymentCreateRequest request =
                new PaymentCreateRequest();

        request.setSaleId(saleId);
        request.setPaymentMethod(paymentMethod);

        request.setAmount(
                BigDecimal.valueOf(amount)
                        .setScale(2, RoundingMode.HALF_UP)
        );

        request.setReference(reference);

        PaymentResponse response =
                post(
                        BASE_URL,
                        request,
                        PaymentResponse.class
                );

        return convertirPayment(response);
    }
    
    private Payment convertirPayment(PaymentResponse response) {

        Payment payment = new Payment();

        payment.setId(
                response.getId() != null
                        ? response.getId()
                        : 0
        );
        payment.setSaleId(
                response.getSaleId() != null
                        ? response.getSaleId()
                        : 0
        );
        payment.setPaymentMethod(response.getPaymentMethod());
        payment.setAmount(
                response.getAmount() != null
                        ? response.getAmount().doubleValue()
                        : 0.0
        );
        payment.setReference(response.getReference());
        payment.setPaymentType(response.getPaymentType());
        payment.setDate(response.getDate());

        return payment;
    }
}
