package com.dispocol.dispofast.modules.iam.application.interfaces;

import com.dispocol.dispofast.modules.iam.domain.AppUser;

public interface AuthService {
    
    /**
     * Authetication for a user using email and password
     * 
     * @param email user's email
     * @param password user's password
     * @return the authenticated user
     */
    AppUser login(String email, String password);

    /**
     * Logs out the current user
     */
    void logout();
}
