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
    private static final int iAccountID = 1300;


    private static final Logger LOGGER = LogManager.getRootLogger();

    private static PacketHandler instance = null;

    public static PacketHandler getInstance(){
        if(instance == null) {
            instance = new PacketHandler();
        }
        return instance;
    }

    public void Handler_TCS_COMM_2_MESSAGE_ACK(InPacket inPacket) {
        TCS_COMM_2_MESSAGE_ACK tcsComm2MessageAck = new TCS_COMM_2_MESSAGE_ACK();
        tcsComm2MessageAck.deserialize(inPacket);
        int iOK = tcsComm2MessageAck.getOK();
        if(TCS_COMM_2_MESSAGE_ACK.Status.getStatusByID(iOK) == TCS_COMM_2_MESSAGE_ACK.Status.MESSAGE_RECEIVED_SUCCESS){
            LOGGER.info("All messages have been retrieved");
        } else if (TCS_COMM_2_MESSAGE_ACK.Status.getStatusByID(iOK) == TCS_COMM_2_MESSAGE_ACK.Status.MESSAGE_RECEIVED_FAILED){
            LOGGER.info("For some reason messages can't be retrieved atm.");
        }

    }

    public OutPacket Handler_TCS_COMM_3_MESSAGE_REQ(MessageInfo messageInfo) {
        TCS_COMM_3_MESSAGE_REQ tcsComm3MessageReq = new TCS_COMM_3_MESSAGE_REQ();
        tcsComm3MessageReq.setMessageInfo(messageInfo);
        return tcsComm3MessageReq.serialize(PacketEnum.TCS_COMM_3_MESSAGE_REQ);
    }

    public void Handler_TCS_COMM_3_MESSAGE_ACK(InPacket inPacket) {
        TCS_COMM_3_MESSAGE_ACK tcsComm3MessageAck = new TCS_COMM_3_MESSAGE_ACK();
        tcsComm3MessageAck.deserialize(inPacket);
        int iOK = tcsComm3MessageAck.getOK();
        if(TCS_COMM_3_MESSAGE_ACK.Status.getStatusByID(iOK) == TCS_COMM_3_MESSAGE_ACK.Status.MESSAGE_RECEIVED_SUCCESS){
            LOGGER.info("A message have been received successfully");
        } else if (TCS_COMM_3_MESSAGE_ACK.Status.getStatusByID(iOK) == TCS_COMM_3_MESSAGE_ACK.Status.MESSAGE_RECEIVED_FAILED){
            LOGGER.info("For some reason a message can't be received.");
        }
    }

    public OutPacket Handler_TCS_COMM_2_MESSAGE_REQ(){
        TCS_COMM_2_MESSAGE_REQ tcsComm2MessageReq = new TCS_COMM_2_MESSAGE_REQ();
        tcsComm2MessageReq.setReceiverID(iAccountID);
        return tcsComm2MessageReq.serialize(PacketEnum.TCS_COMM_2_MESSAGE_REQ);
    }


    public OutPacket Handler_TCS_USER_IS_ONLINE_REQ(int id){
        TCS_USER_IS_ONLINE_REQ tcsUserIsOnlineReq = new TCS_USER_IS_ONLINE_REQ();
        tcsUserIsOnlineReq.setUserID(id);
        return tcsUserIsOnlineReq.serialize(PacketEnum.TCS_USER_IS_ONLINE_REQ);
    }

    public void Handler_TCS_USER_IS_ONLINE_ACK(InPacket inPacket){
        TCS_USER_IS_ONLINE_ACK tcsUserIsOnlineAck = new TCS_USER_IS_ONLINE_ACK();
        tcsUserIsOnlineAck.deserialize(inPacket);
        int iOK = tcsUserIsOnlineAck.getiOK();
        if(TCS_USER_IS_ONLINE_ACK.Status.getStatusByID(iOK) == TCS_USER_IS_ONLINE_ACK.Status.USER_ONLINE)
            LOGGER.info("Client Online");
        else
            LOGGER.info("Client Offline");
    }

    public OutPacket Handler_TCS_COMM_MESSAGE_REQ(int destID, String message) {
        TCS_COMM_MESSAGE_REQ tcsCommMessageReq = new TCS_COMM_MESSAGE_REQ();
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setDestID(destID);
        messageInfo.setOwnerID(iAccountID);
        messageInfo.setUnixTime(System.currentTimeMillis());
        messageInfo.setMessage(message);
        tcsCommMessageReq.setMessageInfo(messageInfo);
        return tcsCommMessageReq.serialize(PacketEnum.TCS_COMM_MESSAGE_REQ);
    }

    public void Handler_TCS_COMM_MESSAGE_ACK(InPacket inPacket) {
        TCS_COMM_MESSAGE_ACK tcsCommMessageAck = new TCS_COMM_MESSAGE_ACK();
        tcsCommMessageAck.deserialize(inPacket);
        int iOK = tcsCommMessageAck.getOK();
        if(TCS_COMM_MESSAGE_ACK.Status.getStatusByID(iOK) == TCS_COMM_MESSAGE_ACK.Status.MESSAGE_SENT_SUCCESS){
            LOGGER.info("Message sent");
        } else if (TCS_COMM_MESSAGE_ACK.Status.getStatusByID(iOK) == TCS_COMM_MESSAGE_ACK.Status.MESSAGE_SENT_FAILED){
            LOGGER.info("Can't send message atm");
        }
    }

    public OutPacket Handler_TCS_COMM_MESSAGE_NOT(InPacket inPacket) {
        TCS_COMM_MESSAGE_NOT tcsCommMessageNot = new TCS_COMM_MESSAGE_NOT();
        tcsCommMessageNot.deserialize(inPacket);
        MessageInfo messageInfo = tcsCommMessageNot.getMessageInfo();
        int iOwnerID = messageInfo.getOwnerID();
        long lUnixTime = messageInfo.getUnixTime();
        String strMessage = messageInfo.getMessage();
        Date date = new Date(lUnixTime);
        LOGGER.info("Message from (" + date.toString() + ")" + iOwnerID + " : " + strMessage);
        return Handler_TCS_COMM_3_MESSAGE_REQ(messageInfo);
    }

    public OutPacket Handler_TCS_USER_SET_ID_REQ(int iID) {
        TCS_USER_SET_ID_REQ userSetIdReq = new TCS_USER_SET_ID_REQ();
        userSetIdReq.setUserID(iID);
        return userSetIdReq.serialize(PacketEnum.TCS_USER_SET_ID_REQ);
    }

    public void Handler_TCS_USER_SET_ID_ACK(InPacket inPacket) {
        TCS_USER_SET_ID_ACK tcsUserSetIdAck = new TCS_USER_SET_ID_ACK();
        tcsUserSetIdAck.deserialize(inPacket);
        int iOK = tcsUserSetIdAck.getiOK();
        if(TCS_USER_SET_ID_ACK.Status.getStatusByID(iOK) == TCS_USER_SET_ID_ACK.Status.SET_ID_SUCCESS){
            LOGGER.info("SetID Success!");
        }
    }

    public OutPacket Handler_TCS_HANDSHAKE_NOT() {
        TCS_HANDSHAKE_REQ tcsHandshakeReq = new TCS_HANDSHAKE_REQ();
        return tcsHandshakeReq.serialize(PacketEnum.TCS_HANDSHAKE_REQ);
    }

    public void Handler_TCS_HANDSHAKE_ACK(InPacket inPacket) {
        TCS_HANDSHAKE_ACK tcsHandshakeAck = new TCS_HANDSHAKE_ACK();
        tcsHandshakeAck.deserialize(inPacket);
        int iOK = tcsHandshakeAck.getiOK();
        if(TCS_HANDSHAKE_ACK.Status.getStatusByID(iOK) == TCS_HANDSHAKE_ACK.Status.HANDSHAKE_SUCCESS){
            LOGGER.info("Handshake Success!");
            socketChannel.writeAndFlush(Handler_TCS_USER_SET_ID_REQ(iAccountID));
        }
    }

    public OutPacket Handler_TCS_HEARTBEAT_NOT() {
        return new OutPacket(PacketEnum.TCS_HEARTBEAT_REQ);
    }

    public void Handler_TCS_HEARTBEAT_ACK() {
    }

    public OutPacket Handler_TCS_SPAM_WARNING_NOT(boolean isSend) {
        if(isSend)
            return new OutPacket(PacketEnum.TCS_SPAM_WARNING_NOT);
        else{
            LOGGER.warn("Spam Warning");
        }
        return null;
    }


}
