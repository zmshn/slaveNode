package com.slavenode;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Connector {
    public static final int READ_PORT = 10003;
    public static final int WRITE_PORT = 10002;
    public static final int TEST_PORT = 10005;
    public static final int MANAGER_NODE_TEST_PORT = 10004;
    private static ServerSocket serverSocket = null;
    private static ServerSocket testSocket = null;
    private static String managerIP = null;

    public static void write(String msg, int port) {
        try {
            Socket sck = new Socket(managerIP,port);
            PrintWriter writer = new PrintWriter(sck.getOutputStream());
            writer.write(msg);
            writer.close();
            sck.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String read() {
        Socket socket = null;
        String msg = null;
        try {
            socket = serverSocket.accept();
            Scanner scan = new Scanner(socket.getInputStream());
            msg = scan.nextLine();
            managerIP = socket.getInetAddress().getHostAddress();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(socket != null){
                try{
                    socket.close();
                }catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return msg;
    }

    public static void TestNode() {
        for (;;) {
            Socket socket = null;
            try {
                socket = testSocket.accept();
                Scanner scan = new Scanner(socket.getInputStream());
                String msg = scan.nextLine();
                System.out.println(msg);
                managerIP = socket.getInetAddress().getHostAddress();
                write(msg, MANAGER_NODE_TEST_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                if(socket != null){
                    try{
                        socket.close();
                    }catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    public static void main(String args[]) throws IOException {
        serverSocket = new ServerSocket(READ_PORT);
        testSocket = new ServerSocket(TEST_PORT);
        new Thread(new Runnable() {
            @Override
            public void run() {
                TestNode();
            }
        }).start();

        for (;;) {
            String url = read();
            Spider spider = Spider.getSpider();
            String JSONMsg = spider.getFollowersInfoJSON(url);
            write(JSONMsg,WRITE_PORT);
        }
    }
}
