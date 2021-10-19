package org.turbogiants.client.cli;

import org.turbogiants.client.connection.packet.PacketHandler;

import java.util.Scanner;

import static org.turbogiants.client.connection.network.ClientInit.socketChannel;

public class CommandListener implements Runnable {

    /**
     * Date: --.--.--
     * Desc: Get the singleton instance of PacketHandler
     * @since 1.4
     */
    private static final PacketHandler PACKET_HANDLER = PacketHandler.getInstance();

    private static final String[] strCommandList = {
            "setID",
            "exit",
            "send",
            "receive",
            "spam",
            "olchk"
    };

    private static String strCommand = " ";

    @Override
    public void run() {
        while (!strCommand.equals("exit")) {
            Scanner myObj = new Scanner(System.in);
            System.out.print(">> ");
            String commandLine = myObj.nextLine();

            String[] commands = commandLine.split(" ");
            if (commands.length >= 2) {
                for (String s : strCommandList) {
                    if (commands[0].equals(s)) {
                        strCommand = s;
                    }
                }

                if ("setID".equals(strCommand)) {
                    setID(Integer.parseInt(commands[1]));
                } else if ("send".equals(strCommand)) {
                    if(commands.length >= 3)
                        send(Integer.parseInt(commands[1]), commands[2]);
                } else if ("spam".equals(strCommand)) {
                    spam();
                } else if ("receive".equals(strCommand)) {
                    receive();
                } else if ("olchk".equals(strCommand)) {
                    olCheck(Integer.parseInt(commands[1]));
                }
            }

        }

    }

    private void setID(int iID) {
        socketChannel.writeAndFlush(PACKET_HANDLER.Handler_TCS_USER_SET_ID_REQ(iID));
    }

    private void olCheck(int id) {
        socketChannel.writeAndFlush(PACKET_HANDLER.Handler_TCS_USER_IS_ONLINE_REQ(id));
    }

    private void send(int id, String message) {
        socketChannel.writeAndFlush(PACKET_HANDLER.Handler_TCS_COMM_MESSAGE_REQ(id, message));
    }

    private void spam() {
        socketChannel.writeAndFlush(PACKET_HANDLER.Handler_TCS_SPAM_WARNING_NOT(true));
    }

    private void receive(){
        socketChannel.writeAndFlush(PACKET_HANDLER.Handler_TCS_COMM_2_MESSAGE_REQ());
    }

}
