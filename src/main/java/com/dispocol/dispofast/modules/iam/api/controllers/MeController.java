package com.dispocol.dispofast.modules.iam.api.controllers;

import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class MeController {

  @GetMapping("/me")
  public Map<String, String> getMethodName(Authentication authentication) {
    return Map.of(
        "username", authentication.getName(),
        "authorities", authentication.getAuthorities().toString());
  }
}
