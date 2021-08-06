package net.browny.server;

import net.browny.server.connection.network.EchoProtocolAccepter;
import net.browny.server.utility.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

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

        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        File file = new File("log4j2.xml");
        context.setConfigLocation(file.toURI());

//            Properties props = new Properties();
//            props.load(new FileInputStream("log4j.properties"));
//            PropertyConfigurator.configure(props);

        Server.getInstance().init(args);
    }
}
