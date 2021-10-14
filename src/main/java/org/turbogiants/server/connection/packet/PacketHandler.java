package org.turbogiants.server.connection.packet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.common.MessageInfo;
import org.turbogiants.common.packet.InPacket;
import org.turbogiants.common.packet.OutPacket;
import org.turbogiants.common.packet.PacketEnum;
import org.turbogiants.common.packet.definitions.*;
import org.turbogiants.server.connection.database.SQLCore;
import org.turbogiants.server.user.NettyUser;
import org.turbogiants.server.user.UserDef;

import java.util.ArrayList;

/**
 * Packet Handler
 * Desc:
 * @author https://github.com/Raitou
 * @version 1.2
 * @since 1.0
 */
public class PacketHandler {
    private static final Logger LOGGER = LogManager.getRootLogger();
    private static final SQLCore SQL_CORE = SQLCore.getInstance();


    private static PacketHandler instance = null;

    public static PacketHandler getInstance(){
        if(instance == null) {
            instance = new PacketHandler();
        }
        return instance;
    }

    public OutPacket Handler_TCS_COMM_2_MESSAGE_ACK(int iReceiverID){
        TCS_COMM_2_MESSAGE_ACK tcsComm2MessageAck = new TCS_COMM_2_MESSAGE_ACK();
        if(SQLCore.getInstance().updateMessageStatus(iReceiverID))
            tcsComm2MessageAck.setiOK(TCS_COMM_2_MESSAGE_ACK.Status.MESSAGE_RECEIVED_SUCCESS);
        else
            tcsComm2MessageAck.setiOK(TCS_COMM_2_MESSAGE_ACK.Status.MESSAGE_RECEIVED_FAILED);
        return tcsComm2MessageAck.serialize(PacketEnum.TCS_COMM_2_MESSAGE_ACK);
    }

    public OutPacket Handler_TCS_COMM_MESSAGE_2_REQ(NettyUser nettyUser, InPacket inPacket){
        TCS_COMM_2_MESSAGE_REQ tcsComm2MessageReq = new TCS_COMM_2_MESSAGE_REQ();
        tcsComm2MessageReq.deserialize(inPacket);
        ArrayList<MessageInfo> arrayList = null;
        if((arrayList = SQL_CORE.getMessage(tcsComm2MessageReq.getReceiverID())) != null){
            for(MessageInfo messageInfo : arrayList){
                nettyUser.write(Handler_TCS_COMM_MESSAGE_NOT(messageInfo));
            }
        }
        return Handler_TCS_COMM_2_MESSAGE_ACK(tcsComm2MessageReq.getReceiverID());
    }

    public OutPacket Handler_TCS_USER_IS_ONLINE_REQ(NettyUser nettyUser, InPacket inPacket){
        TCS_USER_IS_ONLINE_REQ tcsUserIsOnlineReq = new TCS_USER_IS_ONLINE_REQ();
        tcsUserIsOnlineReq.deserialize(inPacket);
        int uid = tcsUserIsOnlineReq.getUserID();
        if(uid <= 0){
            LOGGER.error("Client(" + nettyUser.getIP() + ") finding id " + uid);
            return null;
        }
        boolean isOnline = NettyUser.isUserIDExists(uid);
        return Handler_TCS_USER_IS_ONLINE_ACK(isOnline);
    }

    public OutPacket Handler_TCS_USER_IS_ONLINE_ACK(boolean isOnline){
        TCS_USER_IS_ONLINE_ACK tcsUserIsOnlineAck = new TCS_USER_IS_ONLINE_ACK();
        if(isOnline){
            tcsUserIsOnlineAck.setiOK(TCS_USER_IS_ONLINE_ACK.Status.USER_ONLINE);
        } else {
            tcsUserIsOnlineAck.setiOK(TCS_USER_IS_ONLINE_ACK.Status.USER_OFFLINE);
        }
        return tcsUserIsOnlineAck.serialize(PacketEnum.TCS_USER_IS_ONLINE_ACK);
    }

    public OutPacket Handler_TCS_COMM_MESSAGE_REQ(InPacket inPacket) {
        TCS_COMM_MESSAGE_REQ tcsCommMessageReq = new TCS_COMM_MESSAGE_REQ();
        tcsCommMessageReq.deserialize(inPacket);
        MessageInfo messageInfo = tcsCommMessageReq.getMessageInfo();
        return Handler_TCS_COMM_MESSAGE_ACK(messageInfo);
    }

    public OutPacket Handler_TCS_COMM_MESSAGE_ACK(MessageInfo messageInfo){
        TCS_COMM_MESSAGE_ACK tcsCommMessageAck = new TCS_COMM_MESSAGE_ACK();
        if(SQL_CORE.addMessage(messageInfo)){
            tcsCommMessageAck.setiOK(TCS_COMM_MESSAGE_ACK.Status.MESSAGE_SENT_SUCCESS);
        } else {
            tcsCommMessageAck.setiOK(TCS_COMM_MESSAGE_ACK.Status.MESSAGE_SENT_FAILED);
        }
        return tcsCommMessageAck.serialize(PacketEnum.TCS_COMM_MESSAGE_ACK);
    }

    public OutPacket Handler_TCS_COMM_MESSAGE_NOT(MessageInfo messageInfo){
        TCS_COMM_MESSAGE_NOT tcsCommMessageNot = new TCS_COMM_MESSAGE_NOT();
        tcsCommMessageNot.setMessageInfo(messageInfo);
        return tcsCommMessageNot.serialize(PacketEnum.TCS_COMM_MESSAGE_NOT);
    }

    public OutPacket Handler_TCS_USER_SET_ID_REQ(NettyUser nettyUser, InPacket inPacket) {
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

            //close both client if userIDExists
            NettyUser user = NettyUser.getUserByID(uid);
            if (user != null)
                user.close();
            return null;
        }
        return Handler_USER_SET_ID_ACK(userSetIdReq, nettyUser);
    }

    public OutPacket Handler_USER_SET_ID_ACK(TCS_USER_SET_ID_REQ tcsUserSetIdReq, NettyUser nettyUser){
        TCS_USER_SET_ID_ACK tcsUserSetIdAck = new TCS_USER_SET_ID_ACK();
        int uid = tcsUserSetIdReq.getUserID();
        tcsUserSetIdAck.setiOK(TCS_USER_SET_ID_ACK.Status.SET_ID_SUCCESS);

        UserDef userDef = new UserDef();
        userDef.setUID(uid);
        nettyUser.setUserDef(userDef);

        LOGGER.info("Client(" + nettyUser.getIP() + ") SetID to " + uid);
        return tcsUserSetIdAck.serialize(PacketEnum.TCS_USER_SET_ID_ACK);
    }

    public OutPacket Handler_TCS_HANDSHAKE_NOT() {
        return new OutPacket(PacketEnum.TCS_HANDSHAKE_NOT);
    }

    public OutPacket Handler_TCS_HANDSHAKE_REQ(NettyUser user, InPacket inPacket) {
        TCS_HANDSHAKE_REQ tcsHandshakeReq = new TCS_HANDSHAKE_REQ();
        tcsHandshakeReq.deserialize(inPacket);
        if(!tcsHandshakeReq.isKeyMatch()){
            LOGGER.error("Client(" + user.getIP() + "): Handshake Failed");
            return null;
        }
        return Handler_TCS_HANDSHAKE_ACK();
    }

    public OutPacket Handler_TCS_HANDSHAKE_ACK(){
        TCS_HANDSHAKE_ACK tcsHandshakeAck = new TCS_HANDSHAKE_ACK();
        tcsHandshakeAck.setiOK(TCS_HANDSHAKE_ACK.Status.HANDSHAKE_SUCCESS);
        return tcsHandshakeAck.serialize(PacketEnum.TCS_HANDSHAKE_ACK);
    }

    public OutPacket Handler_TCS_HEARTBEAT_NOT() {
        return new OutPacket(PacketEnum.TCS_HEARTBEAT_NOT);
    }

    public OutPacket Handler_TCS_HEARTBEAT_REQ() {
        return new OutPacket(PacketEnum.TCS_HEARTBEAT_ACK);
    }

    public OutPacket Handler_TCS_SPAM_WARNING_NOT() {
        return new OutPacket(PacketEnum.TCS_SPAM_WARNING_NOT);
    }

}
