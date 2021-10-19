package org.turbogiants.common.utility;

// logger

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
// end logger

// javax.json
import javax.json.JsonObjectBuilder;
import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonObject;
import javax.json.JsonWriter;
// end javax.json

// java.io
import java.io.*;
import java.util.Arrays;
// end java.io

public class Config {

    public static final String CONFIG_SERVER_JSON = "Config_Server.json";
    public static final String CONFIG_CLIENT_JSON = "Config_Client.json";
    private static final Logger LOGGER = LogManager.getRootLogger();

    // CONFIG DEFAULT VALUES
    private static String databaseIp = "127.0.0.1";
    private static String databaseName = "telebrown";
    private static Integer databasePort = 3306;
    private static String databaseUsername = "root";
    private static String databasePassword = "root";
    private static String socketIp = "127.0.0.1";
    private static Integer socketPort = 6100;
    // END CONFIG DEFAULT VALUES

    private static void CreateServerConfigFile() {
        JsonObjectBuilder configBuilder = Json.createObjectBuilder();

        configBuilder
                .add("databaseIp", databaseIp)
                .add("databasePort", databasePort)
                .add("databaseName", databaseName)
                .add("databaseUsername", databaseUsername)
                .add("databasePassword", databasePassword)
                .add("socketIp", socketIp)
                .add("socketPort", socketPort);

        try {
            FileOutputStream fileOut = new FileOutputStream(CONFIG_SERVER_JSON);
            JsonWriter jsonWriter = Json.createWriter(fileOut);
            JsonObject configJsonObj = configBuilder.build();
            jsonWriter.writeObject(configJsonObj);
            jsonWriter.close();
        } catch (FileNotFoundException ex) {
            LOGGER.error(CONFIG_SERVER_JSON + " already exists Exception(): " + ex.getMessage());
        } catch (SecurityException ex) {
            LOGGER.error("Cannot write " + CONFIG_SERVER_JSON + " Exception(): " + ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error("Unknown Error StackTrace(): " + Arrays.toString(ex.getStackTrace()));
            System.exit(-1);
        }
    }

    private static void CreateClientConfigFile() {
        JsonObjectBuilder configBuilder = Json.createObjectBuilder();

        configBuilder
                .add("socketIp", socketIp)
                .add("socketPort", socketPort);

        try {
            FileOutputStream fileOut = new FileOutputStream(CONFIG_CLIENT_JSON);
            JsonWriter jsonWriter = Json.createWriter(fileOut);
            JsonObject configJsonObj = configBuilder.build();
            jsonWriter.writeObject(configJsonObj);
            jsonWriter.close();
        } catch (FileNotFoundException ex) {
            LOGGER.error(CONFIG_CLIENT_JSON + " already exists Exception(): " + ex.getMessage());
        } catch (SecurityException ex) {
            LOGGER.error("Cannot write " + CONFIG_CLIENT_JSON + " Exception(): " + ex.getMessage());
        } catch (Exception ex) {
            LOGGER.error("Unknown Error StackTrace(): " + Arrays.toString(ex.getStackTrace()));
            System.exit(-1);
        }
    }

    public static String getDatabaseIp() {
        return databaseIp;
    }

    public static Integer getDatabasePort() {
        return databasePort;
    }

    public static String getDatabaseName(){
        return databaseName;
    }

    public static String getDatabaseUsername() {
        return databaseUsername;
    }

    public static String getDatabasePassword() {
        return databasePassword;
    }

    public static Integer getSocketPort() {
        return socketPort;
    }

    public static String getSocketIp() {
        return socketIp;
    }

    public static void init(boolean isServer) {
        if (isServer) {
            try {
                FileInputStream fileIn = new FileInputStream(CONFIG_SERVER_JSON);

                JsonReader jsonReader = Json.createReader(fileIn);
                JsonObject jsonObject = jsonReader.readObject();

                databaseIp = jsonObject.getString("databaseIp");
                databasePort = jsonObject.getInt("databasePort");
                databaseName = jsonObject.getString("databaseName");
                databaseUsername = jsonObject.getString("databaseUsername");
                databasePassword = jsonObject.getString("databasePassword");
                socketIp = jsonObject.getString("socketIp");
                socketPort = jsonObject.getInt("socketPort");

                jsonReader.close();
                fileIn.close();
            } catch (FileNotFoundException ex) {
                LOGGER.warn(CONFIG_SERVER_JSON + " doesn't exists! Creating...");
                CreateServerConfigFile();
            } catch (IOException ex) {
                LOGGER.error("Application is denied to read the \"" + CONFIG_SERVER_JSON + "\" file Exception(): " + ex.getMessage());
                System.exit(-1);
            } catch (Exception ex) {
                LOGGER.error("Unknown Error StackTrace(): " + Arrays.toString(ex.getStackTrace()));
                System.exit(-1);
            }
        } else {
            try {
                FileInputStream fileIn = new FileInputStream(CONFIG_CLIENT_JSON);

                JsonReader jsonReader = Json.createReader(fileIn);
                JsonObject jsonObject = jsonReader.readObject();

                socketIp = jsonObject.getString("socketIp");
                socketPort = jsonObject.getInt("socketPort");

                jsonReader.close();
                fileIn.close();
            } catch (FileNotFoundException ex) {
                System.out.println(CONFIG_CLIENT_JSON + " doesn't exists! Creating...");
                CreateServerConfigFile();
            } catch (IOException ex) {
                System.out.println("Application is denied to read the \"" + CONFIG_SERVER_JSON + "\" file Exception(): " + ex.getMessage());
                System.exit(-1);
            } catch (Exception ex) {
                System.out.println("Unknown Error StackTrace(): " + Arrays.toString(ex.getStackTrace()));
                System.exit(-1);
            }
        }

    }
}
