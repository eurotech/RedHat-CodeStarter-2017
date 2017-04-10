package org.eclipse.kura.db.server;

import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbServerComponent {

    private static final Logger logger = LoggerFactory.getLogger(DbServerComponent.class);

    private Server server;

    private void startServer() throws Exception {
        logger.info("starting server...");
        this.server = new Server();
        server.setAddress("0.0.0.0");
        server.setPort(9001);
        HsqlProperties p = new HsqlProperties();
        p.setProperty("server.database.0", "mem:kuradb");
        p.setProperty("server.dbname.0", "kuradb");
        server.setProperties(p);
        server.start();
        logger.info("starting server...done");
    }

    private void shutdownServer() {
        logger.info("shutting down server...");
        this.server.shutdown();
        logger.info("shutting down server...done");
    }

    public void activate() {
        try {
            startServer();
        } catch (Exception e) {
            logger.warn("Unexpected exception while starting server", e);
        }
    }

    public void deactivate() {
        shutdownServer();
    }
}
