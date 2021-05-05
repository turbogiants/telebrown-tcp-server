package net.browny.server;

import net.browny.server.connection.network.EchoProtocolAccepter;
import net.browny.server.utility.Config;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Server {
    final Logger logger = LogManager.getRootLogger();
    private static final Server server = new Server();

    public static Server getInstance() {
        return server;
    }

    private void init(String[] args){
        logger.info("Browny Server [Start]");
        long startNow = System.currentTimeMillis();
        Config.init();
        logger.info("Config.json loaded in " + (System.currentTimeMillis() - startNow) + "ms");
        new Thread(new EchoProtocolAccepter()).start(); // test protocol
        logger.info("Binded to " + Config.getSocketIp() + ":" + Config.getSocketPort() + " in " + (System.currentTimeMillis() - startNow) + "ms");
    }

    public static void main(String[] args) {
        try{
            Properties props = new Properties();
            props.load(new FileInputStream("log4j.properties"));
            PropertyConfigurator.configure(props);
        }catch (FileNotFoundException ex){
            System.out.println(ex.getMessage());
            System.exit(-1);
        }catch (IOException ex){
            System.out.println(ex.getMessage());
            System.exit(-1);
        }

        Server.getInstance().init(args);
    }
}
