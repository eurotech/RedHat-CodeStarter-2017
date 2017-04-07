package org.eclipse.kura.demo.opcua.server;

import java.io.IOException;

import org.eclipse.milo.opcua.sdk.server.nodes.delegates.AttributeDelegate;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

public abstract class Sensor implements AttributeDelegate, AutoCloseable {

    private String name;
    private NodeId dataType;

    public Sensor(String name, NodeId integer) {
        this.name = name;
        this.dataType = integer;
    }

    public String getName() {
        return name;
    }

    public NodeId getDataType() {
        return dataType;
    }

    public abstract void init() throws IOException;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Sensor other = (Sensor) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
