package com.example.springbootfrontendsimple.web;

import javax.annotation.security.RolesAllowed;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties.Jwt;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Controller
@RequestMapping("/secured")
public class SecuredController {
    private final RestTemplate restTemplate = new RestTemplateBuilder().build();
    @Autowired
    private HttpServletRequest request;

    @GetMapping
    @RolesAllowed({"admin"})
    public String index() {
        return "/secured/index";
    }

    @RequestMapping("/user")
    @RolesAllowed({"admin"})
    public String user(@CurrentSecurityContext SecurityContext securityContext) {
        log.info(securityContext.getAuthentication().getPrincipal().toString());

        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) request.getUserPrincipal();
        KeycloakPrincipal principal=(KeycloakPrincipal)token.getPrincipal();
        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
//        AccessToken accessToken = session.getToken();
        log.info(session.getTokenString());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + session.getTokenString());
        ResponseEntity<String> exchange = restTemplate.exchange("https://localhost:8081/",
            HttpMethod.POST,
            new HttpEntity<>(httpHeaders),
            String.class);
        log.info(exchange.getBody());

        return "/secured/user";
    }

    @GetMapping("/admin")
    @RolesAllowed({"admin"})
    public String admin() {
        return "/secured/admin";
    }
}
