package com.dispocol.dispofast.modules.iam.application.interfaces;

import com.dispocol.dispofast.modules.iam.api.dtos.LoginRequestDTO;
import com.dispocol.dispofast.modules.iam.api.dtos.LoginResponseDTO;

public interface AuthService {

  /**
   * Authenticates a user and returns a JWT token
   *
   * @param loginRequest The login request DTO containing email and password
   * @return A LoginResponseDTO containing the JWT token and user info
   */
  LoginResponseDTO login(LoginRequestDTO loginRequest);

  /** Logs out the current user */
  void logout();
}
