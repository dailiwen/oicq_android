package com.example.dai.oicq_android.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Properties;

import android.app.Application;

/**
 * @author dailiwen
 * @date 2018/06/24
 */
public class ApplicationUtil extends Application {

    private static Properties PROPERTIES = PropertiesUtil.getProperties();
    private Socket socket;
    private BufferedReader bufferedReader = null;
    private PrintWriter printWriter = null;


    public void init() throws Exception{
        this.socket = new Socket(PROPERTIES.getProperty("serverUrl"), Integer.valueOf(PROPERTIES.getProperty("serverPort")));
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"utf-8"));
        this.printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),true);
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public BufferedReader getBufferedReader() {
        return bufferedReader;
    }

    public void setBufferedReader(BufferedReader bufferedReader) {
        this.bufferedReader = bufferedReader;
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    public void setPrintWriter(PrintWriter printWriter) {
        this.printWriter = printWriter;
    }
}