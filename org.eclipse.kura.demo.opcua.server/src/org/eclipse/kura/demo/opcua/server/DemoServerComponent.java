package org.eclipse.kura.demo.opcua.server;

import static org.eclipse.milo.opcua.sdk.server.api.config.OpcUaServerConfig.USER_TOKEN_POLICY_ANONYMOUS;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.eclipse.kura.configuration.ConfigurableComponent;
import org.eclipse.kura.demo.opcua.server.gpio.GpioOutAttributeDelegate;
import org.eclipse.kura.demo.opcua.server.i2c.GroveDigitalLightSensorAttributeDelegate;
import org.eclipse.kura.gpio.GPIOService;
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

    private GpioOutAttributeDelegate led;
    private GpioOutAttributeDelegate buzzer;
    private GroveDigitalLightSensorAttributeDelegate lightSensor;

    private GPIOService gpioService;

    public void setGpioService(GPIOService gpioService) {
        this.gpioService = gpioService;
    }

    public void unsetGpioService(GPIOService gpioService) {
        this.gpioService = null;
    }

    public void activate(Map<String, Object> properties) {
        logger.info("activating...");
        try {
            led = new GpioOutAttributeDelegate(18, gpioService);
            buzzer = new GpioOutAttributeDelegate(17, gpioService);
            lightSensor = new GroveDigitalLightSensorAttributeDelegate();
        } catch (IOException e) {
            logger.warn("failed initialize deivices", e);
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

        UaFolderNode gpioFolder = namespace.createFolder("gpio");

        if (led != null) {
            namespace.addNode("gpio/led", gpioFolder, Identifiers.Boolean, led);
        }
        if (buzzer != null) {
            namespace.addNode("gpio/buzzer", gpioFolder, Identifiers.Boolean, buzzer);
        }

        UaFolderNode i2cFolder = namespace.createFolder("i2c");

        if (lightSensor != null) {
            namespace.addNode("i2c/lightSensor", i2cFolder, Identifiers.Integer, lightSensor);
        }

        this.server.startup().get();
    }

    public void deactivate() {
        logger.info("deactivating..");
        try {
            if (led != null) {
                led.shutdown();
            }
            if (buzzer != null) {
                buzzer.shutdown();
            }
            if (lightSensor != null) {
                lightSensor.shutdown();
            }
            shutdownServer();
        } catch (Exception e) {
            logger.warn("failed to shutdown server", e);
        }
    }
}
