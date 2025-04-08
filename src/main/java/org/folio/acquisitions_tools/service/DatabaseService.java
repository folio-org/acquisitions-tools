package org.folio.acquisitions_tools.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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
                 WHEN location ? 'tenantId' THEN location
                 ELSE jsonb_set(location, '{tenantId}', '"%s"'::jsonb)
               END
             )
             FROM jsonb_array_elements(jsonb->'locations') AS location
           )
         )
         WHERE\s
             jsonb->'locations' @> '[{"locationId": "%s"}]'
             AND NOT (
                 SELECT bool_and(elem ? 'tenantId')
                 FROM jsonb_array_elements(jsonb->'locations') AS elem
             )
        \s""";

      jdbcTemplate.update(String.format(sql, tenant, locationTenantId, locationId));
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
                 WHEN location ? 'tenantId' THEN location
                 ELSE jsonb_set(location, '{tenantId}', '"%s"'::jsonb)
               END
             )
             FROM jsonb_array_elements(jsonb->'locations') AS location
           )
         )
         WHERE\s
             jsonb->'locations' @> '[{"holdingId": "%s"}]'
             AND NOT (
                 SELECT bool_and(elem ? 'tenantId')
                 FROM jsonb_array_elements(jsonb->'locations') AS elem
             )
        \s""";

      jdbcTemplate.update(String.format(sql, tenant, holdingTenantId, holdingId));
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
         WHERE\s
             jsonb->>'locationId' = '%s'
             AND NOT (jsonb ? 'receivingTenantId')
        \s""";

      jdbcTemplate.update(String.format(sql, tenant, locationTenantId, locationId));
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
         WHERE\s
             jsonb->>'holdingId' = '%s'
             AND NOT (jsonb ? 'receivingTenantId')
        \s""";

      jdbcTemplate.update(String.format(sql, tenant, holdingTenantId, holdingId));
    }
  }
} 