package org.bravetogether.mapstories.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Haim Adrian
 * @since 21-Mar-21
 */
public class ControllerErrorHandler {
   public static ResponseEntity<?> returnInternalServerError(Throwable t) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error has occurred. Reason: " + t.getMessage());
   }
}

