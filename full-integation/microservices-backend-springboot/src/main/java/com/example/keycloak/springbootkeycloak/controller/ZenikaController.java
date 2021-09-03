package com.example.keycloak.springbootkeycloak.controller;

import javax.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZenikaController {

    @PostMapping("/")
    @RolesAllowed({"admin", "users"})
    public ResponseEntity<String> save() {
      return new ResponseEntity<>(
          "Valid user", HttpStatus.OK);
    }

}
