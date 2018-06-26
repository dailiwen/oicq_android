package com.example.dai.oicq_android.client;

import com.example.dai.oicq_android.util.PropertiesUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * @author dailiwen
 * @date 2018/06/24
 */
public class Client extends Socket{

    private static Properties PROPERTIES = PropertiesUtil.getProperties();
    private static final String host = PROPERTIES.getProperty("serverUrl");
    private static final int port = Integer.valueOf(PROPERTIES.getProperty("serverPort"));
    private static BufferedReader br = null;
    private static PrintWriter pw = null;
    /* 持有私有静态实例，防止被引用，此处赋值为null，目的是实现延迟加载 */
    private static Client socket = null;

    private Client(String host, int port) throws UnknownHostException, IOException {
        super(host, port);
    }

    public static Client getsocket() throws IOException {
        if(socket == null) {
            socket = new Client(host,port);
        }
        return socket;
    }

    public static BufferedReader getbr() throws IOException {
        if(br == null) {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
        }
        return br;
    }

    public static PrintWriter getpw() throws IOException {
        if(pw == null) {
            pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
        }
        return pw;
    }
}
