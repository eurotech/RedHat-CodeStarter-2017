package org.eclipse.kura.demo.opcua.server.i2c;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.kura.core.util.ProcessUtil;
import org.eclipse.kura.core.util.SafeProcess;
import org.eclipse.kura.demo.opcua.server.Sensor;
import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.server.api.nodes.VariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.AttributeContext;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class GroveTemperatureSensor extends Sensor {

	private static final int REFRESH_INTERVAL = 10000;

	private static final Logger logger = LoggerFactory.getLogger(GroveTemperatureSensor.class);

	private long lastRead = 0L;
	private double lastReadVal;

	private static final String PYTHON_CODE = "import smbus\n" + "import time\n" + "bus = smbus.SMBus(1)\n"
			+ "bus.write_i2c_block_data(0x44, 0x2C, [0x06])\n" + "time.sleep(0.5)\n"
			+ "data = bus.read_i2c_block_data(0x44, 0x00, 6)\n" + "temp = data[0] * 256 + data[1]\n"
			+ "cTemp = -45 + (175 * temp / 65535.0)\n" + "fTemp = -49 + (315 * temp / 65535.0)\n"
			+ "humidity = 100 * (data[3] * 256 + data[4]) / 65535.0\n" + "print \"%.2f\" %cTemp\n";

	public GroveTemperatureSensor(String name) {
		super(name, Identifiers.Double);
	}

	@Override
	public void init() throws IOException {
		Files.write(PYTHON_CODE, new File("/tmp/temperature.py"), Charsets.UTF_8);
	}

	@Override
	public UByte getAccessLevel(AttributeContext context, VariableNode node) throws UaException {
		return UByte.valueOf(AccessLevel.getMask(AccessLevel.READ_ONLY));
	}

	@Override
	public UByte getUserAccessLevel(AttributeContext context, VariableNode node) throws UaException {
		return UByte.valueOf(AccessLevel.getMask(AccessLevel.READ_ONLY));
	}

	@Override
	public DataValue getValue(AttributeContext context, VariableNode node) throws UaException {
		SafeProcess proc = null;
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastRead >= REFRESH_INTERVAL) {
			try {
				proc = ProcessUtil.exec("python /tmp/temperature.py");
				proc.waitFor();

				try (BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()))) {
					String line = br.readLine();
					lastReadVal = Double.parseDouble(line);
					lastRead = currentTime;
					return new DataValue(new Variant(lastReadVal));
				}
			} catch (Exception e) {
				logger.warn("got exception", e);
				throw new UaException(e);
			} finally {
				if (proc != null) {
					ProcessUtil.destroy(proc);
				}
			}
		} else {
			return new DataValue(new Variant(lastReadVal));
		}
	}

	@Override
	public void close() throws Exception {
	}
}
