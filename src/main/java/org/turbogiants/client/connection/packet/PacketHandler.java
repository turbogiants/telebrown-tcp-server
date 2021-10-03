package org.turbogiants.client.connection.packet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.common.MessageInfo;
import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;
import org.turbogiants.common.packet.definitions.*;

import java.util.Date;

import static org.turbogiants.client.connection.network.ClientInit.socketChannel;

public class PacketHandler {

    //test var
    private static final int iAccountID = 1200;


    private static final Logger LOGGER = LogManager.getRootLogger();

    public static OutPacket Handler_TCS_USER_IS_ONLINE_REQ(int id){
        TCS_USER_IS_ONLINE_REQ tcsUserIsOnlineReq = new TCS_USER_IS_ONLINE_REQ();
        tcsUserIsOnlineReq.setUserID(id);
        return tcsUserIsOnlineReq.serialize(PacketEnum.TCS_USER_IS_ONLINE_REQ);
    }

    public static void Handler_TCS_USER_IS_ONLINE_ACK(InPacket inPacket){
        TCS_USER_IS_ONLINE_ACK tcsUserIsOnlineAck = new TCS_USER_IS_ONLINE_ACK();
        tcsUserIsOnlineAck.deserialize(inPacket);
        int iOK = tcsUserIsOnlineAck.getiOK();
        if(TCS_USER_IS_ONLINE_ACK.Status.getStatusByID(iOK) == TCS_USER_IS_ONLINE_ACK.Status.USER_ONLINE)
            LOGGER.info("Client Online");
        else
            LOGGER.info("Client Offline");
    }

    public static OutPacket Handler_TCS_COMM_MESSAGE_REQ(int destID, String message) {
        TCS_COMM_MESSAGE_REQ tcsCommMessageReq = new TCS_COMM_MESSAGE_REQ();
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setiDestID(destID);
        messageInfo.setiOwnerID(iAccountID);
        messageInfo.setlUnixTime(System.currentTimeMillis());
        messageInfo.setStrMessage(message);
        tcsCommMessageReq.setMessageInfo(messageInfo);
        return tcsCommMessageReq.serialize(PacketEnum.TCS_COMM_MESSAGE_REQ);
    }

    public static void Handler_TCS_COMM_MESSAGE_ACK(InPacket inPacket) {
        TCS_COMM_MESSAGE_ACK tcsCommMessageAck = new TCS_COMM_MESSAGE_ACK();
        tcsCommMessageAck.deserialize(inPacket);
        int iOK = tcsCommMessageAck.getiOK();
        if(TCS_COMM_MESSAGE_ACK.Status.getStatusByID(iOK) == TCS_COMM_MESSAGE_ACK.Status.MESSAGE_SENT_SUCCESS){
            LOGGER.info("Message sent");
        } else if (TCS_COMM_MESSAGE_ACK.Status.getStatusByID(iOK) == TCS_COMM_MESSAGE_ACK.Status.MESSAGE_SENT_FAILED){
            LOGGER.info("Can't send message atm");
        }
    }

    public static void Handler_TCS_COMM_MESSAGE_NOT(InPacket inPacket) {
        TCS_COMM_MESSAGE_NOT tcsCommMessageNot = new TCS_COMM_MESSAGE_NOT();
        tcsCommMessageNot.deserialize(inPacket);
        MessageInfo messageInfo = tcsCommMessageNot.getMessageInfo();
        int iOwnerID = messageInfo.getiOwnerID();
        long lUnixTime = messageInfo.getlUnixTime();
        String strMessage = messageInfo.getStrMessage();
        Date date = new Date(lUnixTime);
        LOGGER.info("Message from (" + date.toString() + ")" + iOwnerID + " : " + strMessage);
    }

    public static OutPacket Handler_TCS_USER_SET_ID_REQ(int iID) {
        TCS_USER_SET_ID_REQ userSetIdReq = new TCS_USER_SET_ID_REQ();
        userSetIdReq.setUserID(iID);
        return userSetIdReq.serialize(PacketEnum.TCS_USER_SET_ID_REQ);
    }

    public static void Handler_TCS_USER_SET_ID_ACK(InPacket inPacket) {
        TCS_USER_SET_ID_ACK tcsUserSetIdAck = new TCS_USER_SET_ID_ACK();
        tcsUserSetIdAck.deserialize(inPacket);
        int iOK = tcsUserSetIdAck.getiOK();
        if(TCS_USER_SET_ID_ACK.Status.getStatusByID(iOK) == TCS_USER_SET_ID_ACK.Status.SET_ID_SUCCESS){
            LOGGER.info("SetID Success!");
        }
    }

    public static OutPacket Handler_TCS_HANDSHAKE_NOT() {
        TCS_HANDSHAKE_REQ tcsHandshakeReq = new TCS_HANDSHAKE_REQ();
        return tcsHandshakeReq.serialize(PacketEnum.TCS_HANDSHAKE_REQ);
    }

    public static void Handler_TCS_HANDSHAKE_ACK(InPacket inPacket) {
        TCS_HANDSHAKE_ACK tcsHandshakeAck = new TCS_HANDSHAKE_ACK();
        tcsHandshakeAck.deserialize(inPacket);
        int iOK = tcsHandshakeAck.getiOK();
        if(TCS_HANDSHAKE_ACK.Status.getStatusByID(iOK) == TCS_HANDSHAKE_ACK.Status.HANDSHAKE_SUCCESS){
            LOGGER.info("Handshake Success!");
            socketChannel.writeAndFlush(PacketHandler.Handler_TCS_USER_SET_ID_REQ(iAccountID));
        }
    }

    public static OutPacket Handler_TCS_HEARTBEAT_NOT() {
        return new OutPacket(PacketEnum.TCS_HEARTBEAT_REQ);
    }

    public static void Handler_TCS_HEARTBEAT_ACK() {
    }

    public static OutPacket Handler_TCS_SPAM_WARNING_NOT(boolean isSend) {
        if(isSend)
            return new OutPacket(PacketEnum.TCS_SPAM_WARNING_NOT);
        else{
            LOGGER.warn("Spam Warning");
        }
        return null;
    }
}
