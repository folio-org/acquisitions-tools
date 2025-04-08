package org.folio.acquisitions_tools.service;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LocationService {
  private final RestTemplate restTemplate = new RestTemplate();

  @Autowired
  private LoginService loginService;

  @Autowired
  private DatabaseService databaseService;

  @Value("${folio.api.url}")
  private String okapiUrl;

  @Value("${folio.api.locations.endpoint}")
  private String locationsEndpoint;

  @Value("${folio.api.tenant}")
  private String tenant;

  public Map<String, String> getLocationIdsAndTenants() {
    HttpEntity<String> request = new HttpEntity<>(loginService.getHeaders());
    ResponseEntity<JsonNode> response = restTemplate.exchange(
      okapiUrl + locationsEndpoint + "?tenantId=" + tenant,
      HttpMethod.GET,
      request,
      JsonNode.class
    );

    JsonNode locations = Objects.requireNonNull(response.getBody()).get("locations");

    return StreamSupport.stream(locations.spliterator(), false)
      .collect(Collectors.toMap(
        loc -> loc.get("id").asText(),
        loc -> loc.get("tenantId").asText()
      ));
  }

  public void updatePoLineLocations() {
    Map<String, String> tenantsGroupedByLocation = getLocationIdsAndTenants();
    databaseService.updatePoLineLocations(tenant, tenantsGroupedByLocation);
  }

  public void updatePieces() {
    Map<String, String> tenantsGroupedByLocation = getLocationIdsAndTenants();
    databaseService.updatePieces(tenant, tenantsGroupedByLocation);
  }
} 