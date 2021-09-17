package org.turbogiants.client.cli;

import org.turbogiants.client.connection.packet.PacketHandler;

import java.util.Scanner;

import static org.turbogiants.client.connection.network.ClientInit.socketChannel;

public class CommandListener implements Runnable {

    private static final String[] strCommandList = {
            "setID",
            "exit",
            "send",
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
                } else if ("olchk".equals(strCommand)) {
                    olCheck(Integer.parseInt(commands[1]));
                }
            }

        }

    }

    private void setID(int iID) {
        socketChannel.writeAndFlush(PacketHandler.Handler_TCS_USER_SET_ID_REQ(iID));
    }

    private void olCheck(int id) {
        socketChannel.writeAndFlush(PacketHandler.Handler_TCS_USER_IS_ONLINE_REQ(id));
    }

    private void send(int id, String message) {
        socketChannel.writeAndFlush(PacketHandler.Handler_TCS_COMM_MESSAGE_REQ(id, message));
    }

    private void spam() {
        socketChannel.writeAndFlush(PacketHandler.Handler_TCS_SPAM_WARNING_NOT(true));
    }

}
