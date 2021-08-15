package org.turbogiants.client.connection.definition;

import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;

/**
 * Test heartbeat
 * <p>
 * todo: create a specific process to do heartbeat
 */

public class Heartbeat {


    public static OutPacket Handler_TCS_HEARTBEAT_NOT() {
        return new OutPacket(PacketEnum.TCS_HEARTBEAT_REQ);
    }

    public static OutPacket Handler_TCS_HEARTBEAT_ACK() {
        return new OutPacket(PacketEnum.TCS_HEARTBEAT_REQ);
    }
}
