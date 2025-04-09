# Acquisitions Tools

This application provides tools for managing FOLIO acquisitions data.

## Configuration

The application requires the following environment variables to be set for database and FOLIO API connections:

**Database:**

- `DB_HOST`: Database host (default: `localhost`)
- `DB_PORT`: Database port (default: `5432`)
- `DB_DATABASE`: Database name (default: `okapi_modules`)
- `DB_USERNAME`: Database username (default: `folio_admin`)
- `DB_PASSWORD`: Database password (default: `folio_admin`)

**FOLIO API:**

- `FOLIO_URL`: Base URL of the FOLIO instance
- `FOLIO_TENANT`: FOLIO tenant ID
- `FOLIO_USERNAME`: FOLIO API username
- `FOLIO_PASSWORD`: FOLIO API password

The application runs on port `8080` by default.

## API Usage

The following endpoints are available under the `/api/locations` path:

### Update PO Line Locations

- **Endpoint:** `/update-polines`
- **Method:** `POST`
- **Description:** Triggers an update process for PO Line locations based on FOLIO API data.
- **Example Request:**
  ```bash
  curl -X POST http://localhost:8080/api/locations/update-polines
  ```
- **Success Response:** `200 OK` with body `"PO Line locations update completed successfully"`
- **Error Response:** `500 Internal Server Error` with details

### Update Pieces

- **Endpoint:** `/update-pieces`
- **Method:** `POST`
- **Description:** Triggers an update process for Pieces based on FOLIO API data.
- **Example Request:**
  ```bash
  curl -X POST http://localhost:8080/api/locations/update-pieces
  ```
- **Success Response:** `200 OK` with body `"Pieces update completed successfully"`
- **Error Response:** `500 Internal Server Error` with details
