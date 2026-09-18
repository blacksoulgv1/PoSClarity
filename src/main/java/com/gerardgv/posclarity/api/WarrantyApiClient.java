package com.gerardgv.posclarity.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.gerardgv.posclarity.models.*;
import java.io.IOException;
import java.util.*;


public class WarrantyApiClient extends BaseApiClient {
    
    public List<Warranty> getActiveWarranties(int branchId) 
              throws IOException, InterruptedException {

        String url = ApiConfig.BASE_URL
                + "/warranties?branchId="
                + branchId;

        return getList(
                url,
                Warranty.class
        );
    }

    public Warranty createWarranty(
            Warranty warranty,
            List<WarrantyDetail> details,
            WarrantyGraduation originalGraduation,
            WarrantyGraduation newGraduation) 
            throws IOException, InterruptedException {

        Map<String, Object> body = new HashMap<>();

        body.put("saleId",warranty.getSaleId());
        body.put("clientId",warranty.getClientId());
        body.put("branchId",warranty.getBranchId());
        body.put("reason",warranty.getReason());
        body.put("action",warranty.getAction());
        body.put("observations",warranty.getObservations());
        body.put("userId", warranty.getUserId());

        List<Map<String, Object>> detailList = new ArrayList<>();

        for (WarrantyDetail detail : details) {

            Map<String, Object> detailMap = new HashMap<>();

            detailMap.put("detailId",detail.getDetailId());
            detailMap.put("quantity",detail.getQuantity());
            detailList.add(detailMap);
        }

        body.put("details",detailList);

        if (originalGraduation != null) {

            body.put(
                    "originalGraduation",
                    graduationToMap(
                            originalGraduation
                    )
            );
        }

        if (newGraduation != null) {

            body.put(
                    "newGraduation",
                    graduationToMap(
                            newGraduation
                    )
            );
        }

        return post(
                ApiConfig.BASE_URL + "/warranties",
                body,
                Warranty.class
        );
    }

    public void receiveWarranty(int warrantyId,int branchId) 
            throws IOException, InterruptedException {

        patch(
                ApiConfig.BASE_URL
                        + "/warranties/"
                        + warrantyId
                        + "/receive?branchId="
                        + branchId,
                null,
                Void.class
        );
    }

    public void deliverWarranty(int warrantyId,int branchId) 
            throws IOException, InterruptedException {

        patch(
                ApiConfig.BASE_URL
                        + "/warranties/"
                        + warrantyId
                        + "/deliver?branchId="
                        + branchId,
                null,
                Void.class
        );
    }

    private Map<String, Object> graduationToMap(
            WarrantyGraduation graduation) {

        Map<String, Object> map = new HashMap<>();

        map.put("odSphere",graduation.getOdSphere());
        map.put("odCylinder",graduation.getOdCylinder());
        map.put("odAxis",graduation.getOdAxis());
        map.put("odAdd",graduation.getOdAdd());
        map.put("oiSphere",graduation.getOiSphere());
        map.put("oiCylinder",graduation.getOiCylinder());
        map.put("oiAxis",graduation.getOiAxis());
        map.put("oiAdd",graduation.getOiAdd());
        return map;
    }
      
}
