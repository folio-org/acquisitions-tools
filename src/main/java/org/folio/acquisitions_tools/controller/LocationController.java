package org.folio.acquisitions_tools.controller;

import lombok.RequiredArgsConstructor;
import org.folio.acquisitions_tools.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/locations")
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
} 