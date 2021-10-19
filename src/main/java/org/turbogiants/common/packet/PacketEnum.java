package org.turbogiants.common.packet;

/**
 * This enum lists all the valid Packet IDs available under telebrown server
 * @author https://github.com/Raitou
 * @version 1.4
 * @since 1.0
 */
public enum PacketEnum {
    /**
     * Handshake - these packet headers are designated to check genuine clients if they are not they will be
     * automatically disconnected by the network within a set period of time.
     */
    TCS_HANDSHAKE_NOT(0),
    /**
     * Handshake - these packet headers are designated to check genuine clients if they are not they will be
     * automatically disconnected by the network within a set period of time.
     */
    TCS_HANDSHAKE_REQ(1),
    /**
     * Handshake - these packet headers are designated to check genuine clients if they are not they will be
     * automatically disconnected by the network within a set period of time.
     */
    TCS_HANDSHAKE_ACK(2),

    /**
     * Heartbeat - these packet headers are designated to check whether the client is still alive or not if they didn't
     * reply within a certain amount of time they will also be disconnected from the network and needs to do a handshake
     * again.
     */
    TCS_HEARTBEAT_NOT(3),
    /**
     * Heartbeat - these packet headers are designated to check whether the client is still alive or not if they didn't
     * reply within a certain amount of time they will also be disconnected from the network and needs to do a handshake
     * again.
     */
    TCS_HEARTBEAT_REQ(4),
    /**
     * Heartbeat - these packet headers are designated to check whether the client is still alive or not if they didn't
     * reply within a certain amount of time they will also be disconnected from the network and needs to do a handshake
     * again.
     */
    TCS_HEARTBEAT_ACK(5),

    /**
     * Setting User Identification - these packet headers are designated to set user identification, if same
     * identification which is not possible is online at the same time both user will be disconnected from the network.
     * If a collision is indeed happened a lot please report it.
     */
    TCS_USER_SET_ID_REQ(6),
    /**
     * Setting User Identification - these packet headers are designated to set user identification, if same
     * identification which is not possible is online at the same time both user will be disconnected from the network.
     * If a collision is indeed happened a lot please report it.
     */
    TCS_USER_SET_ID_ACK(7),

    /**
     * Communication Message Notify - this packet header notifies the user as of @version 1.4 this is now in need of
     * manual request to get a message as they now are all stored via the MySQL database. This will be reliable esp for
     * users that has network connectivity issues.
     */
    TCS_COMM_MESSAGE_NOT(8),

    /**
     * Communication Message Package 1 - these packet headers is used for sending messages to the server.
     */
    TCS_COMM_MESSAGE_REQ(9),
    /**
     * Communication Message Package 1 - these packet headers is used for sending messages to the server.
     */
    TCS_COMM_MESSAGE_ACK(10),

    /**
     * Spam Warning - this packet header is used when the AESCrypto keys doesn't match what server had for the client.
     * This could be fixed by doing queue in the client as server always reply for the next key. If ever the client
     * did not receive it because of network connectivity issues, proceed to reconnection and do handshake.
     */
    TCS_SPAM_WARNING_NOT(11),

    /**
     * User Online - this packet header is used to check whether a user is still online via its ID, it will not be
     * reliable especially if the ID that needs to be checked has network connectivity issues.
     */
    TCS_USER_IS_ONLINE_REQ(12),
    /**
     * User Online - this packet header is used to check whether a user is still online via its ID, it will not be
     * reliable especially if the ID that needs to be checked has network connectivity issues.
     */
    TCS_USER_IS_ONLINE_ACK(13),

    /**
     * Communication Message Package 2 - these packet headers is used for receiving messages rom the server.
     */
    TCS_COMM_2_MESSAGE_REQ(14),
    /**
     * Communication Message Package 2 - these packet headers is used for receiving messages rom the server.
     */
    TCS_COMM_2_MESSAGE_ACK(15),

    /**
     * Communication Message Package 3 - these packet headers is used to notify the server the message/s has/have been
     * received successfully.
     */
    TCS_COMM_3_MESSAGE_REQ(16),
    /**
     * Communication Message Package 3 - these packet headers is used to notify the server the message/s has/have been
     * received successfully.
     */
    TCS_COMM_3_MESSAGE_ACK(17),

    ;

    private final short packetID;

    /**
     * PacketEnum constructor - this requires a packetID which must be under the PacketEnum
     * @param packetID - packetID from the PacketEnum
     */
    PacketEnum(int packetID) {
        this.packetID = (short) packetID;
    }

    /**
     * checkHeaderByOP - this function returns a PacketEnum when exists returns a PacketEnum else returns a null
     * @param packetID - this requires an ID that will be checked if the ID exists in the packetEnum
     * @return PacketEnum - this returns a PacketEnum header if it ever exists.
     */
    public static PacketEnum checkHeaderByOP(int packetID) {
        for (PacketEnum serverEnum : PacketEnum.values()) {
            if (serverEnum.getPacketID() == packetID) {
                return serverEnum;
            }
        }
        return null;
    }

    /**
     * Standard Getter to get PacketID
     * @return packetID
     */
    public short getPacketID() {
        return packetID;
    }
}
