package org.folio.acquisitions_tools.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LoginService {
  @Autowired
  private RestTemplate restTemplate;

  @Value("${folio.api.url}")
  private String folioUrl;

  @Value("${folio.api.tenant}")
  private String tenant;

  @Value("${folio.api.username}")
  private String username;

  @Value("${folio.api.password}")
  private String password;

  private String getToken() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("x-okapi-tenant", tenant);
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, String> loginRequest = new HashMap<>();
    loginRequest.put("username", username);
    loginRequest.put("password", password);

    HttpEntity<Map<String, String>> request = new HttpEntity<>(loginRequest, headers);
    ResponseEntity<JsonNode> response = restTemplate.postForEntity(
        folioUrl + "/authn/login", request, JsonNode.class
    );

    return Objects.requireNonNull(response.getBody()).get("okapiToken").asText();
  }

  public HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.set("x-okapi-tenant", tenant);
    headers.set("x-okapi-token", getToken());
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }
} 