/*
 * Copyright (c) 2022 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.training.microservices.system;

import com.nrkei.training.microservices.filter.Validation;
import com.nrkei.training.microservices.packet.Packet;
import com.nrkei.training.microservices.rapid.RapidsConnection;
import com.nrkei.training.microservices.rapid.rabbitmq.RabbitMqRapids;
import com.nrkei.training.microservices.river.River.SystemListener;
import com.nrkei.training.microservices.river.Status;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JMonitor implements SystemListener {
    @NotNull
    @Override
    public String getName() {
        return "%s[%d]".formatted(getClass().getSimpleName(), hashCode());
    }


    public static void main(String[] args) {
        String host = args[0];
        String port = args[1];
        new RabbitMqRapids(host, port).register(new JMonitor());         // Hook up to the river to start receiving traffic
    }

    @NotNull
    @Override
    // No rules for a Monitor. Options for filtering shown for documentation purpose
    public List<Validation> getRules() {
        return List.of(
//                new KeyValueValidation("key1", "value1"), // Reject packet unless key1 has value1
//                new KeyValueValidation("key2", 42.7),   // Reject packet unless key2 has value 42.7
//                new KeyValueValidation("key3", true),   // Reject packet unless key3 has value true
//                new KeyExistanceValidation("key4"),   // Reject packet if key4 does not exist
//                new KeyExistanceValidation("key5"),   // Reject packet if key5 does not exist
//                new KeyAbsenseValidation("key6"),     // Reject packet if key6 exists; key6 = null, key6 = "", or key6 = [] all considered "missing"
//                new KeyAbsenseValidation("key7")     // Reject packet if key7 exists
        );
    }

    @Override
    public void packet(@NotNull RapidsConnection connection, @NotNull Packet packet, @NotNull Status infoWarnings) {
        System.out.printf(" [*] %s\n", infoWarnings);
    }

    @Override
    public void rejectedPacket(@NotNull RapidsConnection connection, @NotNull Packet packet, @NotNull Status problems) {
        System.out.printf(" [x] %s\n", problems);
    }

    @Override
    public void invalidFormat(@NotNull RapidsConnection connection, @NotNull String invalidString, @NotNull Status problems) {
        System.out.printf(" [x] Invalid JSON: %s\n", invalidString);
    }

    @Override
    public void loopDetected(@NotNull RapidsConnection connection, @NotNull Packet packet, @NotNull Status problems) {
        System.out.printf(" [x] Loop detected! Packet is: %s", packet.toJsonString());
    }

    @Override
    public boolean isStillAlive(@NotNull RapidsConnection connection) {
        return true;
    }
}
