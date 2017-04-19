package org.eclipse.kura.demo.opcua.server;

import static org.eclipse.kura.demo.opcua.server.emulation.Simulation.block;
import static org.eclipse.kura.demo.opcua.server.emulation.Simulation.boolSquareWave;
import static org.eclipse.kura.demo.opcua.server.emulation.Simulation.intSquareWave;
import static org.eclipse.kura.demo.opcua.server.emulation.Simulation.loop;
import static org.eclipse.kura.demo.opcua.server.emulation.Simulation.nonNegative;
import static org.eclipse.kura.demo.opcua.server.emulation.Simulation.randomLong;
import static org.eclipse.kura.demo.opcua.server.emulation.Simulation.sin;
import static org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig.USER_TOKEN_POLICY_ANONYMOUS;

import java.io.File;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.eclipse.kura.configuration.ConfigurableComponent;
import org.eclipse.kura.demo.opcua.server.emulation.SimulatedInputSensor;
import org.eclipse.kura.demo.opcua.server.emulation.SimulatedOutputSensor;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.application.DefaultCertificateManager;
import org.eclipse.milo.opcua.stack.core.application.DefaultCertificateValidator;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoServerComponent implements ConfigurableComponent {

    private static final String APPLICATION_URN = "urn:eclipse:milo:examples:server";
    private static final String SERVER_PORT_PROPERTY_NAME = "server.port";

    private static final Logger logger = LoggerFactory.getLogger(DemoServerComponent.class);

    private OpcUaServer server;
    private int port;
    private DemoNamespace namespace;

    private HashSet<Sensor> sensors = new HashSet<>();

    public void activate(Map<String, Object> properties) {
        logger.info("activating...");

        Function<Integer, Integer> clamp = (in) -> in == 0 ? 0 : 1;

        sensors.add(new SimulatedInputSensor<Double>("temperatureSensor", Identifiers.Double,
                loop(block(sin(25.0f, 3.0f), nonNegative(randomLong(30 * 1000, 5 * 1000))))));

        sensors.add(new SimulatedInputSensor<Integer>("lightSensor", Identifiers.Integer,
                intSquareWave(0, 500, nonNegative(randomLong(10 * 1000, 1000)), nonNegative(randomLong(5 * 1000, 1000)),
                        nonNegative(randomLong(5 * 1000, 1000)))));

        sensors.add(new SimulatedOutputSensor<Integer>("buzzer", Identifiers.Integer, 0, clamp));

        sensors.add(new SimulatedOutputSensor<Integer>("led", Identifiers.Integer, 0, clamp));

        sensors.add(new SimulatedInputSensor<Boolean>("waterSensor", Identifiers.Boolean,
                boolSquareWave(nonNegative(randomLong(10 * 1000, 1000)), nonNegative(randomLong(10 * 1000, 1000)))));

        sensors.add(new SimulatedOutputSensor<Integer>("fan", Identifiers.Integer, 0, clamp));

        Iterator<Sensor> it = sensors.iterator();

        while (it.hasNext()) {
            try {
                Sensor sensor = it.next();
                logger.info("initializing {}...", sensor.getName());
                sensor.init();
                logger.info("initializing {}...done", sensor.getName());
            } catch (Exception e) {
                logger.warn("failed to initialize sensor", e);
                it.remove();
            }
        }

        updated(properties);
    }

    public void updated(Map<String, Object> properties) {
        logger.info("updadting..");
        this.port = (Integer) properties.getOrDefault(SERVER_PORT_PROPERTY_NAME, 1234);

        try {
            restartServer();
        } catch (Exception e) {
            logger.warn("failed to start server: ", e);
        }
    }

    private void shutdownServer() throws InterruptedException, ExecutionException {
        if (server != null) {
            server.shutdown().get();
            server = null;
        }
    }

    private void restartServer() throws InterruptedException, ExecutionException, UaException {
        shutdownServer();

        OpcUaServerConfig serverConfig = OpcUaServerConfig.builder().setApplicationUri(APPLICATION_URN)
                .setBindAddresses(Arrays.asList("0.0.0.0")).setBindPort(port).setProductUri(APPLICATION_URN)
                .setSecurityPolicies(EnumSet.of(SecurityPolicy.None))
                .setCertificateManager(new DefaultCertificateManager())
                .setCertificateValidator(
                        new DefaultCertificateValidator(new File(System.getProperty("java.io.tmpdir"), "security")))
                .setUserTokenPolicies(Arrays.asList(USER_TOKEN_POLICY_ANONYMOUS)).build();

        this.server = new OpcUaServer(serverConfig);

        this.server.getNamespaceManager().registerAndAdd(DemoNamespace.DEMO_NAMESPACE_URI,
                index -> namespace = new DemoNamespace(server, index));

        UaFolderNode sensorsFolder = namespace.createFolder("sensors");

        for (Sensor sensor : sensors) {
            namespace.addNode(sensor.getName(), sensorsFolder, sensor.getDataType(), sensor);
        }

        this.server.startup().get();
    }

    public void deactivate() {
        logger.info("deactivating..");
        try {
            shutdownServer();
        } catch (Exception e) {
            logger.warn("failed to shutdown server", e);
        }

        for (Sensor sensor : sensors) {
            try {
                logger.info("shutting down {}...", sensor.getName());
                sensor.close();
                logger.info("shutting down {}...done", sensor.getName());
            } catch (Exception e) {
                logger.warn("failed to shut down {}", sensor.getName());
            }
        }
        sensors.clear();
    }
}
