package net.browny.server;

import net.browny.server.connection.network.SocketAccepter;
import net.browny.server.utility.Config;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Server {
    final Logger logger = LogManager.getRootLogger();
    private static final Server server = new Server();

    public static Server getInstance() {
        return server;
    }

    private void init(String[] args){
        logger.info("Browny Server [Starting]");
        long startNow = System.currentTimeMillis();
        logger.info("Config.json [Loading]");
        Config.init();
        logger.info("Config.json [Loaded] in " + (System.currentTimeMillis() - startNow) + "ms");
        logger.info("Binding to Socket [Loading]");
        Runnable sockAccepter = new SocketAccepter();
        Thread sockThread = new Thread(sockAccepter);
        sockThread.start();
        logger.info("Binded to " + Config.getSocketIp() + ":" + Config.getSocketPort() + " [Loaded] in " + (System.currentTimeMillis() - startNow) + "ms");
    }

    public static void main(String[] args) {
        Server.getInstance().init(args);
    }
}
