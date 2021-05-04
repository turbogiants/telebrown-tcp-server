package net.browny.server.Utility;

// logger
import org.apache.log4j.Logger;
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

    public static final String CONFIG_JSON = "Config.json";
    private static final Logger LOGGER = Logger.getLogger(Config.class);

    // CONFIG DEFAULT VALUES
    private static String databaseIp = "127.0.0.1";
    private static Integer databasePort = 3306;
    private static String databaseUsername = "root";
    private static String databasePassword = "root";
    private static String socketIp = "127.0.0.1";
    private static Integer socketPort = 6100;
    // END CONFIG DEFAULT VALUES

    private static void CreateConfigFile() {
        JsonObjectBuilder configBuilder = Json.createObjectBuilder();

        configBuilder
                .add("databaseIp", databaseIp)
                .add("databasePort", databasePort)
                .add("databaseUsername", databaseUsername)
                .add("databasePassword", databasePassword)
                .add("socketIp", socketIp)
                .add("socketPort", socketPort);

        try{
            FileOutputStream fileOut = new FileOutputStream(CONFIG_JSON);
            JsonWriter jsonWriter = Json.createWriter(fileOut);
            JsonObject configJsonObj = configBuilder.build();
            jsonWriter.writeObject(configJsonObj);
            jsonWriter.close();
        }catch(FileNotFoundException ex){
            LOGGER.error("Config.json already exists Exception(): " + ex.getMessage());
        }catch(SecurityException ex){
            LOGGER.error("Cannot write Config.json Exception(): " + ex.getMessage());
        } catch (Exception ex){
            LOGGER.error("Unknown Error StackTrace(): " + Arrays.toString(ex.getStackTrace()));
            System.exit(-1);
        }
    }

    public static Integer getSocketPort(){
        return socketPort;
    }

    public static String getSocketIp(){
        return socketIp;
    }

    public static void init() {
        try{
            FileInputStream fileIn = new FileInputStream(CONFIG_JSON);

            JsonReader jsonReader = Json.createReader(fileIn);
            JsonObject jsonObject = jsonReader.readObject();

            databaseIp = jsonObject.getString("databaseIp");
            databasePort = jsonObject.getInt("databasePort");
            databaseUsername = jsonObject.getString("databaseUsername");
            databasePassword = jsonObject.getString("databasePassword");
            socketIp = jsonObject.getString("socketIp");
            socketPort = jsonObject.getInt("socketPort");

            jsonReader.close();
            fileIn.close();
        } catch (FileNotFoundException ex){
            LOGGER.warn("Config.json doesn't exists! Creating...");
            CreateConfigFile();
        }  catch (IOException ex){
            LOGGER.error("Server is denied to read the \"Config.json\" file Exception(): " + ex.getMessage());
            System.exit(-1);
        } catch (Exception ex){
            LOGGER.error("Unknown Error StackTrace(): " + Arrays.toString(ex.getStackTrace()));
            System.exit(-1);
        }
    }
}
