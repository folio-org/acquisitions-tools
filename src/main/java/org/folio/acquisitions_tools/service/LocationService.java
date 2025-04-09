package org.folio.acquisitions_tools.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class LocationService {
  private final RestTemplate restTemplate = new RestTemplate();

  @Autowired
  private LoginService loginService;

  @Autowired
  private DatabaseService databaseService;

  @Value("${folio.api.url}")
  private String folioUrl;

  @Value("${folio.api.locations.endpoint}")
  private String locationsEndpoint;

  @Value("${folio.api.holdings.endpoint}")
  private String holdingsEndpoint;

  @Value("${folio.api.tenant}")
  private String tenant;

  public Map<String, String> getLocationIdsAndTenants() {
    String endpoint = locationsEndpoint + "?limit=1000";

    HttpEntity<String> request = new HttpEntity<>(loginService.getHeaders());
    ResponseEntity<JsonNode> response = restTemplate.exchange(
        folioUrl + endpoint, HttpMethod.GET, request, JsonNode.class
    );

    JsonNode locations = Objects.requireNonNull(response.getBody()).get("locations");

    return StreamSupport.stream(locations.spliterator(), false)
        .collect(Collectors.toMap(
            loc -> loc.get("id").asText(),
            loc -> loc.get("tenantId").asText()
        ));
  }

  public Map<String, String> getHoldingsAndTenants() {
    String endpoint = holdingsEndpoint + "?limit=1000";

    HttpEntity<String> request = new HttpEntity<>(loginService.getHeaders());
    ResponseEntity<JsonNode> response = restTemplate.exchange(
        folioUrl + endpoint, HttpMethod.GET, request, JsonNode.class
    );

    JsonNode holdings = Objects.requireNonNull(response.getBody()).get("holdings");

    Map<String, List<String>> duplicateHoldings = new HashMap<>();
    Map<String, String> allHoldings = new HashMap<>();

    StreamSupport.stream(holdings.spliterator(), false).forEach(holding -> {
      String id = holding.get("id").asText();
      String tenantId = holding.get("tenantId").asText();
      if (allHoldings.containsKey(id)) {
        if (!duplicateHoldings.containsKey(id)) {
          duplicateHoldings.put(id, new ArrayList<>(List.of(allHoldings.get(id))));
        }
        duplicateHoldings.get(id).add(tenantId);
      } else {
        allHoldings.put(id, tenantId);
      }
    });

    if (!duplicateHoldings.isEmpty()) {
      log.warn("Found {} holdings with duplicate IDs:", duplicateHoldings.size());
      duplicateHoldings.forEach((id, tenants) -> {
        log.warn("Holding ID: {} appears in tenants: {}", id, String.join(", ", tenants));
      });
    }

    // Create a new result map containing only non-duplicate holdings
    Map<String, String> result = new HashMap<>(allHoldings);
    duplicateHoldings.keySet().forEach(result::remove);

    log.info("Removed {} duplicate holdings, returning {} unique holdings",
        duplicateHoldings.size(), result.size());

    return result;
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