package org.turbogiants.server;

import org.turbogiants.server.connection.network.ServerInit;
import org.turbogiants.common.utility.Config;

import java.io.File;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

public class Server {
    public final Logger logger = LogManager.getRootLogger();
    private static final Server server = new Server();

    public static Server getInstance() {
        return server;
    }

    private void init(String[] args){
        logger.info("Browny Server [Start]");
        long startNow = System.currentTimeMillis();
        Config.init(true);
        logger.info("Configuration loaded in " + (System.currentTimeMillis() - startNow) + "ms");
        new Thread(new ServerInit()).start();
        logger.info("Binded to " + Config.getSocketIp() + ":" + Config.getSocketPort() + " in " + (System.currentTimeMillis() - startNow) + "ms");

    }

    public static void main(String[] args) {
        try{
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            File file = new File("log4j2_server.xml");
            context.setConfigLocation(file.toURI());
            Server.getInstance().init(args);
        }catch(Exception e){
             Server.getInstance().logger.error(e.getStackTrace());
        }

    }
}
