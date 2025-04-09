package org.folio.acquisitions_tools.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class DatabaseService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public void updatePoLineLocationsByLocation(String tenant, Map<String, String> tenantsGroupedByLocation) {
    for (Map.Entry<String, String> location : tenantsGroupedByLocation.entrySet()) {
      String locationId = location.getKey();
      String locationTenantId = location.getValue();

      String sql = """
            UPDATE %s_mod_orders_storage.po_line
            SET jsonb = jsonb_set(
              jsonb,
              '{locations}',
              (
                SELECT jsonb_agg(
                  CASE
                    WHEN location->>'locationId' = '%s' AND NOT (location ? 'tenantId') THEN
                      jsonb_set(location, '{tenantId}', '"%s"'::jsonb)
                    ELSE location
                  END
                )
                FROM jsonb_array_elements(jsonb->'locations') AS location
              )
            )
            WHERE
                jsonb->'locations' @> '[{"locationId": "%s"}]'
                AND NOT (
                    SELECT bool_and(elem ? 'tenantId')
                    FROM jsonb_array_elements(jsonb->'locations') AS elem
                    WHERE elem->>'locationId' = '%s'
                )
          """;

      int updatedRecords = jdbcTemplate.update(String.format(sql, tenant, locationId, locationTenantId, locationId, locationId));
      log.info("updatePoLineLocationsByLocation:: '{}' Updated PoLines for location: {} and locationTenant: {} in tenant: {}",
          updatedRecords, location, locationTenantId, tenant);
    }
  }

  public void updatePoLineLocationsByHolding(String tenant, Map<String, String> tenantsGroupedByHolding) {
    for (Map.Entry<String, String> holding : tenantsGroupedByHolding.entrySet()) {
      String holdingId = holding.getKey();
      String holdingTenantId = holding.getValue();

      String sql = """
          UPDATE %s_mod_orders_storage.po_line
          SET jsonb = jsonb_set(
           jsonb,
           '{locations}',
           (
             SELECT jsonb_agg(
               CASE
                 WHEN location->>'holdingId' = '%s' AND NOT (location ? 'tenantId') THEN
                   jsonb_set(location, '{tenantId}', '"%s"'::jsonb)
                 ELSE location
               END
             )
             FROM jsonb_array_elements(jsonb->'locations') AS location
           )
          )
          WHERE
             jsonb->'locations' @> '[{"holdingId": "%s"}]'
             AND NOT (
                 SELECT bool_and(elem ? 'tenantId')
                 FROM jsonb_array_elements(jsonb->'locations') AS elem
                 WHERE elem->>'holdingId' = '%s'
             )
          """;
      int updatedRecords = jdbcTemplate.update(String.format(sql, tenant, holdingId, holdingTenantId, holdingId, holdingId));
      log.info("updatePoLineLocationsByHolding:: '{}' Updated PoLines for holding: {} and holdingTenant: {} in tenant: {}",
          updatedRecords, holding, holdingTenantId, tenant);
    }
  }

  public void updatePiecesByLocation(String tenant, Map<String, String> tenantsGroupedByLocation) {
    for (Map.Entry<String, String> location : tenantsGroupedByLocation.entrySet()) {
      String locationId = location.getKey();
      String locationTenantId = location.getValue();

      String sql = """
           UPDATE %s_mod_orders_storage.pieces
           SET jsonb = jsonb_set(
               jsonb,
               '{receivingTenantId}',
               '"%s"'::jsonb
           )
           WHERE
               jsonb->>'locationId' = '%s'
               AND NOT (jsonb ? 'receivingTenantId')
          """;

      int updatedRecords = jdbcTemplate.update(String.format(sql, tenant, locationTenantId, locationId));
      log.info("updatePiecesByLocation:: '{}' Updated Pieces for location: {} and locationTenant: {} in tenant: {}",
          updatedRecords, location, locationTenantId, tenant);
    }
  }

  public void updatePiecesByHolding(String tenant, Map<String, String> tenantsGroupedByHolding) {
    for (Map.Entry<String, String> holding : tenantsGroupedByHolding.entrySet()) {
      String holdingId = holding.getKey();
      String holdingTenantId = holding.getValue();

      String sql = """
           UPDATE %s_mod_orders_storage.pieces
           SET jsonb = jsonb_set(
               jsonb,
               '{receivingTenantId}',
               '"%s"'::jsonb
           )
           WHERE
               jsonb->>'holdingId' = '%s'
               AND NOT (jsonb ? 'receivingTenantId')
          """;

      int updatedRecords = jdbcTemplate.update(String.format(sql, tenant, holdingTenantId, holdingId));
      log.info("updatePiecesByHoldings:: '{}' Updated Pieces for holding: {} and holdingTenant: {} in tenant: {}",
          updatedRecords, holding, holdingTenantId, tenant);
    }
  }
} 