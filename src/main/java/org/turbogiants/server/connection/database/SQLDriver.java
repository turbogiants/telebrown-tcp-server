package org.turbogiants.server.connection.database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.turbogiants.common.utility.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;


/**
 * This class is SQL Driver for MySQL
 * @author https://github.com/Raitou
 * @version 1.3
 * @since 1.3
 */
public class SQLDriver {

    private static final Logger LOGGER = LogManager.getRootLogger();

    protected final static String CONNECTION_URL = String.format("jdbc:mysql://%s:%d/%s?useSSL=false",
            Config.getDatabaseIp(),
            Config.getDatabasePort(),
            Config.getDatabaseName()
    );
    protected final static String USER = Config.getDatabaseUsername();
    protected final static String PASS = Config.getDatabasePassword();

    /**
     * Checks whether the connection to the MySQL database
     * @return boolean: If the connection is successful or not
     */
    public static boolean testSQLConnection(){
        boolean bOK = true;
        try(Connection con = DriverManager.getConnection(CONNECTION_URL, USER, PASS);){
        }catch(SQLException ex){
            bOK = false;
            LOGGER.error(ex.getLocalizedMessage());
        }
        return bOK;
    }

    /**
     * Uses executeUpdate: Returns an integer representing the number of rows affected by the SQL statement.
     * Use this method if you are using INSERT, DELETE, or UPDATE SQL statements.
     * @param statement: SQL statement that needs to be executed.
     * @param affected: Pass-by-Value Integer object - passes the number of rows affected.
     * @return boolean: If the execution is successful or not.
     */
    public static boolean query(String statement, Integer affected){
        boolean bOK = true;
        try(Connection con = DriverManager.getConnection(CONNECTION_URL, USER, PASS);
            PreparedStatement query = con.prepareStatement(statement);
        ){
            affected = query.executeUpdate();
        }catch(SQLException ex){
            LOGGER.error(ex.getLocalizedMessage());
            affected = 0;
            bOK = false;
        }
        return bOK;
    }

    /**
     * Uses executeQuery: Returns a ResultSet Object
     * @param statement: SQL statement that needs to be executed.
     * @param result: Pass-by-Value result object
     * @return boolean: If the execution is successful or not.
     */
    public static boolean query_fetch(String statement, ResultSet result){
        boolean bOK = true;
        try(Connection con = DriverManager.getConnection(CONNECTION_URL, USER, PASS);
            PreparedStatement query = con.prepareStatement(statement);
        ){
            result = query.executeQuery();
        }catch(SQLException ex){
            LOGGER.error(ex.getLocalizedMessage());
            bOK = false;
        }
        return bOK;
    }
}