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
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class LocationService {
  @Autowired
  private RestTemplate restTemplate;

  @Autowired
  private LoginService loginService;

  @Autowired
  private DatabaseService databaseService;

  @Value("${folio.api.url}")
  private String folioUrl;

  @Value("${folio.api.tenant}")
  private String tenant;

  /**
   * Generic method to fetch entities and their tenants from a search endpoint
   *
   * @param entityType The type of entity to fetch (e.g., "locations", "holdings")
   * @param entityNodeName The name of the node in the response that contains the entities
   * @return A map of entity IDs to tenant IDs with duplicates removed
   */
  private Map<String, String> getEntitiesAndTenants(String entityType, String entityNodeName) {
    String endpoint = "/search/consortium/" + entityType + "?limit=1000";

    HttpEntity<String> request = new HttpEntity<>(loginService.getHeaders());
    ResponseEntity<JsonNode> response = restTemplate.exchange(
        folioUrl + endpoint, HttpMethod.GET, request, JsonNode.class
    );

    JsonNode entities = Objects.requireNonNull(response.getBody()).get(entityNodeName);

    Map<String, List<String>> duplicateEntities = new HashMap<>();
    Map<String, String> allEntities = new HashMap<>();

    StreamSupport.stream(entities.spliterator(), false).forEach(entity -> {
      String id = entity.get("id").asText();
      String tenantId = entity.get("tenantId").asText();
      if (allEntities.containsKey(id)) {
        if (!duplicateEntities.containsKey(id)) {
          duplicateEntities.put(id, new ArrayList<>(List.of(allEntities.get(id))));
        }
        duplicateEntities.get(id).add(tenantId);
      } else {
        allEntities.put(id, tenantId);
      }
    });

    if (!duplicateEntities.isEmpty()) {
      log.warn("Found {} {} with duplicate IDs:", duplicateEntities.size(), entityType);
      duplicateEntities.forEach((id, tenants) -> {
        // Remove trailing 's' for singular form in log message
        String entitySingular = entityType.endsWith("s") ?
            entityType.substring(0, entityType.length() - 1) : entityType;
        log.warn("{} ID: {} appears in tenants: {}", entitySingular,
            id, String.join(", ", tenants));
      });
    }

    // Create a new result map containing only non-duplicate entities
    Map<String, String> result = new HashMap<>(allEntities);
    duplicateEntities.keySet().forEach(result::remove);

    log.info("Removed {} duplicate {}, returning {} unique {}",
        duplicateEntities.size(), entityType, result.size(), entityType);

    return result;
  }

  public Map<String, String> getLocationIdsAndTenants() {
    return getEntitiesAndTenants("locations", "locations");
  }

  public Map<String, String> getHoldingsAndTenants() {
    return getEntitiesAndTenants("holdings", "holdings");
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

  public void syncPoLineLocations(String tenantId) {
    log.info("syncPoLineLocations:: Synchronizing poLine locations with existing pieces for tenant: {}", tenantId);
    databaseService.syncPoLineLocationsWithPieces(tenantId);
  }

} 