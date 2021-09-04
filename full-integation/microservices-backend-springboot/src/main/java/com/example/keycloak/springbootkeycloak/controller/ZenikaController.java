package com.example.keycloak.springbootkeycloak.controller;

import javax.annotation.security.RolesAllowed;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ZenikaController {

    @PostMapping("/")
    @RolesAllowed({"admin", "users"})
    public ResponseEntity<String> save() {
      return new ResponseEntity<>(
          "Valid user", HttpStatus.OK);
    }

  @RequestMapping("/hello")
  public ResponseEntity<String> insecure() {
    return new ResponseEntity<>(
        "insecure call", HttpStatus.OK);
  }

}
