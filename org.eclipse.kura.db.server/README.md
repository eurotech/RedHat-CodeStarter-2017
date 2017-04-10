## Instructions

1. Install this bundle

2. Open TCP port 9001 in the firewall configuration

3. Download hsqldb-2.3.0.jar from Maven Central

   ```
   <groupId>org.hsqldb</groupId>
   <artifactId>hsqldb</artifactId>
   <version>2.3.0</version>
   ```

4. Run the downloaded jar above as an executable jar (double click on it or use `java -jar`)

5. Enter the following parameters for connecting to the DB server:

  * **Type**: `HSQL Database Engine Server`
  * **Driver**: `org.hsqldb.jdbcDriver`
  * **URL**: `jdbc:hsqldb:hsql://<host>/kuradb` (example: `jdbc:hsqldb:hsql://10.200.12.217/kuradb`)
  * **User**: `SA`
  * **Password**: leave empty

6. Wires data is stored by default in the "WR_data" table, try performing the following query:

   ```
   SELECT * FROM "WR_data" ORDER BY TIMESTAMP DESC LIMIT 10;
   ```
