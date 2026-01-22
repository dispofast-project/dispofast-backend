package com.dispocol.dispofast.modules.iam.api;

import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController()
public class MeController {
    
    @GetMapping("/api/me")
    public Map<String,String> getMethodName(Authentication authentication) {
        return Map.of(
            "username", authentication.getName(),
            "authorities", authentication.getAuthorities().toString()
        );
    }
    
}
