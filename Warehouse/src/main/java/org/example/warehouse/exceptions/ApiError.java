package org.example.warehouse.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

public record ApiError(
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        OffsetDateTime timestamp,
        int status,
        HttpStatus error,
        String message,
        String path
) {
    public static ApiError of(HttpStatus status, String message, String path) {
        return new ApiError(OffsetDateTime.now(), status.value(), status, message, path);
    }
}
