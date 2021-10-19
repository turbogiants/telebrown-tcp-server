package org.turbogiants.server.connection.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.common.MessageInfo;

import java.sql.*;
import java.util.ArrayList;

public class SQLCore extends SQLDriver{

    private static SQLCore instance = null;
    private static final Logger LOGGER = LogManager.getRootLogger();

    public static SQLCore getInstance(){
        if(instance == null) {
            instance = new SQLCore();
        }
        return instance;
    }

    public boolean addMessage(MessageInfo messageInfo){
        String statement = String.format("INSERT INTO messagehistory(senderID, recipientID, message, TIMESTAMP) VALUES(%d, %d, \"%s\", %d)",
                messageInfo.getOwnerID(),
                messageInfo.getDestID(),
                messageInfo.getMessage(),
                messageInfo.getUnixTime()
        );
        Integer affectedRows = 0;
        return query(statement, affectedRows);
    }

    public boolean updateMessageStatus(MessageInfo messageInfo){
        String statement = String.format("UPDATE messagehistory SET STATUS = 0 WHERE senderID = %d AND recipientID = %d AND message = \"%s\" AND TIMESTAMP = %d",
                messageInfo.getOwnerID(),
                messageInfo.getDestID(),
                messageInfo.getMessage(),
                messageInfo.getUnixTime()
        );
        Integer affectedRows = 0;
        return query(statement, affectedRows);
    }

    public ArrayList<MessageInfo> getMessage(int iReceiverID){
        String statement = String.format("SELECT * FROM messagehistory WHERE recipientID = %d and STATUS = -1", iReceiverID);
        ResultSet resultSet = null;
        try(Connection con = DriverManager.getConnection(CONNECTION_URL, USER, PASS);
            PreparedStatement query = con.prepareStatement(statement);
        ){
            resultSet = query.executeQuery();
            ArrayList<MessageInfo> messageInfos = new ArrayList<>();
            while (resultSet.next()) {
                MessageInfo messageInfo = new MessageInfo();
                messageInfo.setOwnerID(resultSet.getInt("senderID"));
                messageInfo.setDestID(resultSet.getInt("recipientID"));
                messageInfo.setMessage(resultSet.getString("message"));
                messageInfo.setUnixTime(resultSet.getLong("TIMESTAMP"));
                messageInfos.add(messageInfo);
            }
            return messageInfos;
        }catch(SQLException ex){
            LOGGER.error(ex.getLocalizedMessage());
        }
         return null;
    }
}
