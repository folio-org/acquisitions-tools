# FOLIO Purchase Order Line Location Synchronization Scripts

## The Problem: Purchase Order Line Locations Not Syncing with Inventory Moves

Due to a certain bug, some POLs with synchronized receiving workflows might have inconsistent location data. 
Specifically, when an item linked to a received piece is moved to a new Holdings record in Inventory, 
the `locations` array on the corresponding Purchase Order Line (POL) may not update automatically\
In order to maintain data integrity, two scripts have been created to help identify and correct these discrepancies.

## Two Scripts for Preview and Update
Two SQL scripts are provided to address this:
1.  **`select_pol_holdings_links.sql` (Preview Script):**
    *   **What it does:** This is a `SELECT` query that identifies POLs where the `locations` array (based on current piece data) differs from what's stored. 
                          It shows the "PO Line ID", its "Old Locations" (current stored value), and the "New Locations" (recalculated value).
    *   **Impact:** This script is **read-only**. It **does not make any changes** to your database. Its sole purpose is to let you see what data would be affected by an update.
2.  **`update_pol_holdings_links.sql` (Update Script):**
    *   **What it does:** This script applies the same recalculation logic as the Preview Script. It then `UPDATE`s the 
                          `locations` array on the affected POLs with the recalculated values. It also logs each update it performs.
    *   **Impact:** This script **modifies data** in your database.

## Configure Schema/Tenant ID
Both scripts need to target the correct database schema for your FOLIO environment. The scripts use a placeholder `${tenant_id}_mod_orders_storage` (e.g., `FROM ${tenant_id}_mod_orders_storage.po_line`).
**Before running ANY script:**
1.  Open each script file.
2.  **Carefully review and update the schema name** to match your specific FOLIO environment.
    *   Replace `${tenant_id}` with your actual tenant ID (e.g., `diku` to make it `diku_mod_orders_storage`).
    *   **Ensure the schema name is consistent and correct in both scripts for your target environment.**

## How to Use These Scripts (Recommended Workflow)
1.  **Backup Your Database:** This is the most important step before running any script that modifies data. Ensure you have a reliable backup.
2.  **Prepare Scripts:**
    *   Open both `select_pol_holdings_links.sql` and `update_pol_holdings_links.sql`.
    *   In each script, verify and set the correct schema/tenant ID as described above.
3.  **Run the Preview Script (`select_pol_holdings_links.sql`):**
    *   Execute the modified `select_pol_holdings_links.sql` against your database.
    *   Review the output carefully. It will list the POLs that would be updated, showing their current ("Old Locations") and proposed ("New Locations") data. This tells you what the Update Script *will do*.
4.  **Run the Update Script (`update_pol_holdings_links.sql`):**
    *   **If you are satisfied with the preview and have a backup**, execute the modified `update_pol_holdings_links.sql` against your database.
    *   The script will log each update it performs using `RAISE NOTICE`.
    *   A final notice will indicate the total number of POLs updated.
5.  **Verify Changes (Optional):**
    *   After the update script has finished, you can re-run the `select_pol_holdings_links.sql` (Preview Script).
    *   Ideally, it should now return no rows (or significantly fewer if some edge cases remain), indicating the discrepancies have been corrected.