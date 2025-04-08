package org.folio.acquisitions_tools.service;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
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

  @Value("${folio.api.holdings.endpoint}")
  private String holdingsEndpoint;

  @Value("${folio.api.tenant}")
  private String tenant;

  public Map<String, String> getLocationIdsAndTenants() {
    HttpEntity<String> request = new HttpEntity<>(loginService.getHeaders());
    ResponseEntity<JsonNode> response = restTemplate.exchange(
      okapiUrl + locationsEndpoint,
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

  public Map<String, String> getHoldingsAndTenants() {
    HttpEntity<String> request = new HttpEntity<>(loginService.getHeaders());
    ResponseEntity<JsonNode> response = restTemplate.exchange(
      okapiUrl + holdingsEndpoint,
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
    log.info("updatePoLineLocations:: Updating poLine locations by locationId");
    databaseService.updatePoLineLocationsByLocation(tenant, tenantsGroupedByLocation);

    Map<String, String> tenantsGroupedByHolding = getHoldingsAndTenants();
    log.info("updatePoLineLocations:: Updating poLine locations by holdingId");
    databaseService.updatePoLineLocationsByHolding(tenant, tenantsGroupedByHolding);
  }

  public void updatePieces() {
    Map<String, String> tenantsGroupedByLocation = getLocationIdsAndTenants();
    log.info("updatePieces:: Updating pieces locations by locationId");
    databaseService.updatePiecesByLocation(tenant, tenantsGroupedByLocation);

    Map<String, String> tenantsGroupedByHolding = getHoldingsAndTenants();
    log.info("updatePieces:: Updating pieces locations by holdingId");
    databaseService.updatePiecesByHolding(tenant, tenantsGroupedByHolding);
  }
} 