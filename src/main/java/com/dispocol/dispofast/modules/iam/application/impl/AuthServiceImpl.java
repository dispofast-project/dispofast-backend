package com.dispocol.dispofast.modules.iam.application.impl;

import org.springframework.stereotype.Service;

import com.dispocol.dispofast.modules.iam.application.interfaces.AuthService;
import com.dispocol.dispofast.modules.iam.domain.AppUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    @Override
    public AppUser login(String email, String password) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }

    @Override
    public void logout() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'logout'");
    }
    
}
