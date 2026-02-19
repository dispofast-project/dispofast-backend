package com.dispocol.dispofast.shared.error.configuration;

import java.time.Instant;

public record GlobalErrorResponse(
    Instant timestamp, int status, String error, String message, String path) {}
