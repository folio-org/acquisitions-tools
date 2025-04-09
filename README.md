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
- `FOLIO_TENANT`: FOLIO tenant ID (central tenant)
- `FOLIO_USERNAME`: FOLIO API username
- `FOLIO_PASSWORD`: FOLIO API password

The application runs on port `8080` by default.

## Running the Application

1. **Build the application:**
   Navigate to the project root directory and run the following Maven command to build the executable JAR:

   ```bash
   mvn package
   ```

   This will create a JAR file in the `target` directory (e.g., `target/acquisitions-tools-0.0.1-SNAPSHOT.jar`).

2. **Run the application:**
   Execute the JAR file using the `java -jar` command. You **must** provide the required environment variables:

   ```bash
   export DB_HOST=your_db_host
   export DB_PORT=your_db_port
   export DB_DATABASE=your_db_name
   export DB_USERNAME=your_db_user
   export DB_PASSWORD=your_db_password
   export FOLIO_URL=your_folio_url
   export FOLIO_TENANT=your_folio_tenant
   export FOLIO_USERNAME=your_folio_username
   export FOLIO_PASSWORD=your_folio_password

   java -jar target/acquisitions-tools-*.jar
   ```

   Replace the placeholder values (`your_db_host`, etc.) with your actual configuration.

   Alternatively, you can pass variables directly on the command line (though exporting them is often cleaner for
   multiple variables):

   ```bash
   DB_HOST=your_db_host DB_PORT=your_db_port ... java -jar target/acquisitions-tools-*.jar
   ```

   Example command to run application

   ```bash
   DB_HOST=your_db_host DB_PORT=your_db_port DB_DATABASE=your_db_name DB_USERNAME=your_db_user DB_PASSWORD=your_db_password FOLIO_URL=your_folio_url FOLIO_TENANT=your_folio_tenant FOLIO_USERNAME=your_folio_username FOLIO_PASSWORD=your_folio_password java -jar target/acquisitions-tools-1.0.0-SNAPSHOT.jar
   ```

### Changing the Port

To run the application on a different port, set the `SERVER_PORT` environment variable _before_ running the `java -jar`
command:

```bash
export SERVER_PORT=8081
java -jar target/acquisitions-tools-*.jar
```

Or, pass it as a command-line argument:

```bash
java -jar target/acquisitions-tools-*.jar --server.port=8081
```

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
