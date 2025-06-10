package org.folio.acquisitions_tools.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.folio.acquisitions_tools.service.LocationService;
import org.folio.acquisitions_tools.util.HelperUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/locations")
@Log4j2
@RequiredArgsConstructor
public class LocationController {

  private final LocationService locationService;

  @PostMapping("/update-polines")
  public ResponseEntity<String> updatePoLineLocations() {
    try {
      locationService.updatePoLineLocations();
      return ResponseEntity.ok("PO Line locations update completed successfully");
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error updating PO Line locations: " + e.getMessage());
    }
  }

  @PostMapping("/update-pieces")
  public ResponseEntity<String> updatePieces() {
    try {
      locationService.updatePieces();
      return ResponseEntity.ok("Pieces update completed successfully");
    } catch (Exception e) {
      return ResponseEntity.internalServerError().body("Error updating pieces: " + e.getMessage());
    }
  }

  @PostMapping("/sync-polines")
  public ResponseEntity<String> syncPoLineLocations(@RequestParam String tenantId) {
    try {
      HelperUtils.validateTenantId(tenantId);
      locationService.syncPoLineLocations(tenantId);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      log.error("Error synchronizing PO Line locations with existing pieces for tenant {}", tenantId, e);
      return ResponseEntity.internalServerError().body("Error synchronizing PO Line locations with existing pieces: " + e.getMessage());
    }
  }

} 