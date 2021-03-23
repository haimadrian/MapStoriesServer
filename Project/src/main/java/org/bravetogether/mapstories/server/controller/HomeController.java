package org.bravetogether.mapstories.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Haim Adrian
 * @since 22-Mar-21
 */
@RestController
public class HomeController {
   @GetMapping("/")
   public ResponseEntity<?> homePage() {
      return ResponseEntity.ok("<h1>Welcome to Map Stories server</h1>\n" +
            "You have to sign in using Map Stories application in order to access services.");
   }
}

