package org.turbogiants.server.connection.definition;

import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;

public class Spam {
    public static OutPacket Handler_TCS_SPAM_WARNING_NOT() {
        return new OutPacket(PacketEnum.TCS_SPAM_WARNING_NOT);
    }
}
