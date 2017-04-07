/*
 * Copyright (c) 2016 Kevin Herron
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 *
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *   http://www.eclipse.org/org/documents/edl-v10.html.
 */

package org.eclipse.kura.demo.opcua.server;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.milo.opcua.sdk.core.AccessLevel;
import org.eclipse.milo.opcua.sdk.server.OpcUaServer;
import org.eclipse.milo.opcua.sdk.server.namespaces.OpcUaNamespace;
import org.eclipse.milo.opcua.sdk.server.nodes.AttributeContext;
import org.eclipse.milo.opcua.sdk.server.nodes.ServerNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaFolderNode;
import org.eclipse.milo.opcua.sdk.server.nodes.UaVariableNode;
import org.eclipse.milo.opcua.sdk.server.nodes.delegates.AttributeDelegate;
import org.eclipse.milo.opcua.stack.core.AttributeId;
import org.eclipse.milo.opcua.stack.core.Identifiers;
import org.eclipse.milo.opcua.stack.core.StatusCodes;
import org.eclipse.milo.opcua.stack.core.UaException;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.QualifiedName;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UByte;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.UShort;
import org.eclipse.milo.opcua.stack.core.types.enumerated.NodeClass;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.eclipse.milo.opcua.stack.core.types.structured.ReadValueId;
import org.eclipse.milo.opcua.stack.core.types.structured.WriteValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoNamespace extends OpcUaNamespace {

    private static final Logger logger = LoggerFactory.getLogger(DemoNamespace.class);
    public static final String DEMO_NAMESPACE_URI = "urn:eclipse:kura:demo";

    private OpcUaServer server;
    private UShort namespaceIndex;

    public DemoNamespace(OpcUaServer server, UShort namespaceIndex) {
        super(server);
        this.server = server;
        this.namespaceIndex = namespaceIndex;
    }

    public UaFolderNode createFolder(String name) throws UaException {
        NodeId folderNodeId = new NodeId(namespaceIndex, name);

        UaFolderNode folderNode = new UaFolderNode(server.getNodeMap(), folderNodeId,
                new QualifiedName(namespaceIndex, name), LocalizedText.english(name));

        server.getNodeMap().addNode(folderNode);

        server.getUaNamespace().addReference(Identifiers.ObjectsFolder, Identifiers.Organizes, true,
                folderNodeId.expanded(), NodeClass.Object);

        return folderNode;
    }

    public UaVariableNode addNode(String name, UaFolderNode parent, NodeId typeId, AttributeDelegate callback) {
        UaVariableNode node = new UaVariableNode.UaVariableNodeBuilder(server.getNodeMap())
                .setNodeId(new NodeId(namespaceIndex, name))
                .setAccessLevel(UByte.valueOf(AccessLevel.getMask(AccessLevel.READ_WRITE)))
                .setUserAccessLevel(UByte.valueOf(AccessLevel.getMask(AccessLevel.READ_WRITE)))
                .setBrowseName(new QualifiedName(namespaceIndex, name)).setDisplayName(LocalizedText.english(name))
                .setDataType(typeId).setTypeDefinition(Identifiers.BaseDataVariableType).build();

        node.setAttributeDelegate(callback);

        server.getNodeMap().addNode(node);
        parent.addOrganizes(node);

        return node;
    }

    @Override
    public UShort getNamespaceIndex() {
        return namespaceIndex;
    }

    @Override
    public String getNamespaceUri() {
        return DEMO_NAMESPACE_URI;
    }

    @Override
    public synchronized void read(ReadContext arg0, Double arg1, TimestampsToReturn arg2, List<ReadValueId> arg3) {
        super.read(arg0, arg1, arg2, arg3);
    }

    @Override
    public synchronized void write(WriteContext context, List<WriteValue> writeValues) {
        List<StatusCode> results = new ArrayList<StatusCode>(writeValues.size());

        for (WriteValue writeValue : writeValues) {
            ServerNode node = server.getNodeMap().get(writeValue.getNodeId());

            if (node != null) {
                try {
                    node.writeAttribute(new AttributeContext(context), writeValue.getAttributeId(),
                            writeValue.getValue(), writeValue.getIndexRange());

                    results.add(StatusCode.GOOD);

                    logger.debug("Wrote value {} to {} attribute of {}", writeValue.getValue().getValue(),
                            AttributeId.from(writeValue.getAttributeId()).map(Object::toString).orElse("unknown"),
                            node.getNodeId());
                } catch (UaException e) {
                    logger.error("Unable to write value={}", writeValue.getValue(), e);
                    results.add(e.getStatusCode());
                }
            } else {
                results.add(new StatusCode(StatusCodes.Bad_NodeIdUnknown));
            }
        }

        context.complete(results);
    }
}