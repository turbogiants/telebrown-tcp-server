package org.turbogiants.server.connection.packet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.common.MessageInfo;
import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;
import org.turbogiants.common.packet.definitions.*;
import org.turbogiants.server.user.NettyUser;
import org.turbogiants.server.user.UserDef;

public class PacketHandler {
    private static final Logger LOGGER = LogManager.getRootLogger();


    public static OutPacket Handler_TCS_COMM_MESSAGE_REQ(NettyUser nettyUser, InPacket inPacket) {
        TCS_COMM_MESSAGE_REQ tcsCommMessageReq = new TCS_COMM_MESSAGE_REQ();
        tcsCommMessageReq.deserialize(inPacket);
        MessageInfo messageInfo = tcsCommMessageReq.getMessageInfo();

        TCS_COMM_MESSAGE_ACK tcsCommMessageAck = new TCS_COMM_MESSAGE_ACK();
        int destID = messageInfo.getiDestID();
        NettyUser destUser = NettyUser.getUserByID(destID);
        if(destUser == null){
            tcsCommMessageAck.setiOK(TCS_COMM_MESSAGE_ACK.Status.MESSAGE_SENT_FAILED);
            //todo: store to db
        } else {
            tcsCommMessageAck.setiOK(TCS_COMM_MESSAGE_ACK.Status.MESSAGE_SENT_SUCCESS);
            TCS_COMM_MESSAGE_NOT tcsCommMessageNot = new TCS_COMM_MESSAGE_NOT();
            tcsCommMessageNot.setMessageInfo(messageInfo);
            destUser.write(tcsCommMessageNot.serialize(PacketEnum.TCS_COMM_MESSAGE_NOT));
        }
        return tcsCommMessageAck.serialize(PacketEnum.TCS_COMM_MESSAGE_ACK);
    }

    public static OutPacket Handler_TCS_USER_SET_ID_REQ(NettyUser nettyUser, InPacket inPacket) {
        TCS_USER_SET_ID_REQ userSetIdReq = new TCS_USER_SET_ID_REQ();
        userSetIdReq.deserialize(inPacket);
        int uid = userSetIdReq.getUserID();
        if(uid <= 0){
            LOGGER.error("Client(" + nettyUser.getIP() + ") SetID to " + uid);
            return null;
        }
        boolean isExists = NettyUser.isUserIDExists(uid);
        if(isExists){
            LOGGER.error("Client(" + nettyUser.getIP() + ") SetID to " + uid + " but already exists");
            return null;
        }
        TCS_USER_SET_ID_ACK tcsUserSetIdAck = new TCS_USER_SET_ID_ACK();
        tcsUserSetIdAck.setiOK(TCS_USER_SET_ID_ACK.Status.SET_ID_SUCCESS);

        UserDef userDef = new UserDef();
        userDef.setUID(uid);
        nettyUser.setUserDef(userDef);

        LOGGER.info("Client(" + nettyUser.getIP() + ") SetID to " + uid);
        return tcsUserSetIdAck.serialize(PacketEnum.TCS_USER_SET_ID_ACK);
    }

    public static OutPacket Handler_TCS_USER_IS_ONLINE_REQ(NettyUser user, InPacket inPacket){
        int uid = inPacket.decodeInt();
        if(uid <= 0){
            LOGGER.error("Client(" + user.getIP() + ") finding id " + uid);
            return null;
        }
        OutPacket outPacket = new OutPacket(PacketEnum.TCS_USER_IS_ONLINE_ACK);
        boolean isOnline = NettyUser.isUserIDExists(uid);
        outPacket.encodeByte(isOnline);
        return outPacket;
    }

    public static OutPacket Handler_TCS_HANDSHAKE_NOT() {
        return new OutPacket(PacketEnum.TCS_HANDSHAKE_NOT);
    }

    public static OutPacket Handler_TCS_HANDSHAKE_REQ(NettyUser user, InPacket inPacket) {
        TCS_HANDSHAKE_REQ tcsHandshakeReq = new TCS_HANDSHAKE_REQ();
        tcsHandshakeReq.deserialize(inPacket);
        if(!tcsHandshakeReq.isKeyMatch()){
            LOGGER.error("Client(" + user.getIP() + "): Handshake Failed");
            return null;
        }
        return Handler_TCS_HANDSHAKE_ACK();
    }

    public static OutPacket Handler_TCS_HANDSHAKE_ACK(){
        TCS_HANDSHAKE_ACK tcsHandshakeAck = new TCS_HANDSHAKE_ACK();
        tcsHandshakeAck.setiOK(TCS_HANDSHAKE_ACK.Status.HANDSHAKE_SUCCESS);
        return tcsHandshakeAck.serialize(PacketEnum.TCS_HANDSHAKE_ACK);
    }

    public static OutPacket Handler_TCS_HEARTBEAT_NOT() {
        return new OutPacket(PacketEnum.TCS_HEARTBEAT_NOT);
    }

    public static OutPacket Handler_TCS_HEARTBEAT_REQ() {
        return new OutPacket(PacketEnum.TCS_HEARTBEAT_ACK);
    }

    public static OutPacket Handler_TCS_SPAM_WARNING_NOT() {
        return new OutPacket(PacketEnum.TCS_SPAM_WARNING_NOT);
    }

}
