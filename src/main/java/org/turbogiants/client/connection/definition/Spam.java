package org.turbogiants.client.connection.definition;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;

public class Spam {

    private static final Logger LOGGER = LogManager.getRootLogger();

    public static OutPacket Handler_TCS_SPAM_WARNING_NOT(boolean isSend) {
        if(isSend)
            return new OutPacket(PacketEnum.TCS_SPAM_WARNING_NOT);
        else{
            LOGGER.warn("Spam Warning");
        }
        return null;
    }
}
