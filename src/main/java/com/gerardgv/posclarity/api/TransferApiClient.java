package com.gerardgv.posclarity.api;

import com.gerardgv.posclarity.api.dto.transfer.TransferDetailRequest;
import com.gerardgv.posclarity.api.dto.transfer.TransferRequest;
import com.gerardgv.posclarity.models.TransferDetail;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TransferApiClient extends BaseApiClient {
    
    private static final String BASE_URL =
            ApiConfig.BASE_URL + "/transfers";

    /*
    Realizar Traspaso.
    */
    public boolean realizarTransfer(
            int idSucursalOrigen,
            int idSucursalDestino,
            List<TransferDetail> details)
            throws IOException, InterruptedException {

        TransferRequest request = new TransferRequest();

        request.setOriginBranchId(idSucursalOrigen);
        request.setDestinationBranchId(idSucursalDestino);

        List<TransferDetailRequest> detailRequests  =
                new ArrayList<>();

        for (TransferDetail detail : details) {
            

            TransferDetailRequest detailRequest  =
                    new TransferDetailRequest();

            detailRequest.setProductId(
                    detail.getProductId()
            );

            detailRequest.setQuantity(
                    detail.getQuantity()
            );

            detailRequests.add(detailRequest);
        }

        request.setDetails(detailRequests);

        post(
                BASE_URL,
                request,
                Void.class
        );

        return true;
    }
    
}
