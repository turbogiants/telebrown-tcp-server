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
        String statement = String.format("INSERT INTO messagehistory(senderID, recipientID, message, TIMESTAMP) VALUES(%d, %d, \"%s\", \"%s\")",
                messageInfo.getiOwnerID(),
                messageInfo.getiDestID(),
                messageInfo.getStrMessage(),
                messageInfo.getlUnixTime()
        );
        Integer affectedRows = 0;
        return query(statement, affectedRows);
    }

    public boolean updateMessageStatus(int iReceiverID){
        String statement = String.format("UPDATE messagehistory SET STATUS = 0 WHERE recipientID = %d",
                iReceiverID
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
                messageInfo.setiOwnerID(resultSet.getInt("senderID"));
                messageInfo.setiDestID(resultSet.getInt("recipientID"));
                messageInfo.setStrMessage(resultSet.getString("message"));
                messageInfo.setlUnixTime(resultSet.getLong("TIMESTAMP"));
                messageInfos.add(messageInfo);
            }
            return messageInfos;
        }catch(SQLException ex){
            LOGGER.error(ex.getLocalizedMessage());
        }
         return null;
    }
}
