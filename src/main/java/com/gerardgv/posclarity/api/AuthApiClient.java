package com.gerardgv.posclarity.api;

import com.gerardgv.posclarity.api.dto.auth.LoginRequest;
import com.gerardgv.posclarity.api.dto.auth.LoginResponse;
import java.io.IOException;

public class AuthApiClient extends BaseApiClient {
    
    private static final String BASE_URL =
            ApiConfig.BASE_URL + "/auth";
    
    public LoginResponse login(LoginRequest request) 
            throws IOException, InterruptedException{
        
        return post(BASE_URL +"/login", request,LoginResponse.class);
    }
}
