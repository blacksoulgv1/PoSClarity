package com.gerardgv.posclarity.api;

import com.gerardgv.posclarity.api.dto.installation.*;
import java.io.IOException;
import java.math.BigDecimal;

public class InstallationApiClient extends BaseApiClient {
    
    private static final String BASE_URL =
            ApiConfig.BASE_URL + "/installations";

    public int createInstallation(
            String name,
            String address,
            String phone,
            BigDecimal initialCash) 
            throws IOException, InterruptedException {

        InstallationRequest request = new InstallationRequest();

        request.setName(name);
        request.setAddress(address);
        request.setPhone(phone);
        request.setInitialCash(initialCash);

        InstallationResponse response = post(
                BASE_URL,
                request,
                InstallationResponse.class
        );

        return response.getBranchId();
    }
    
}
