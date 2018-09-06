/* 
 * Copyright (C) 2018 Julian A. Santos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.juliansantosinfo.jassocketserver.server;

import com.github.juliansantosinfo.jassocketserver.connection.ConnectionManager;
import com.github.juliansantosinfo.jassocketserver.ui.Console;
import com.github.juliansantosinfo.jassocketserver.ui.SystemTray;
import com.github.juliansantosinfo.jasdatehour.JASDateHour;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Julian A. Santos
 */
public class Server extends Thread {

    // Variaveis globais.
    private Socket connection;
    private ServerSocket serverSocket;
    private ServerConfig serverConfig;
    private ServerManagerConnection serverManagerConnection;

    private boolean started;

    private List<ConnectionManager> connectionList;
    private List<Console> consoleList;

    private int port;
    private int connectionLimit;
    private File logFile;
    private String logPath;
    private PrintWriter pwLogFile;

    private Object keyToReadLog = new Object();

    // Contrutores.
    public Server() {
        
        // Carrega arquivo de configuração.
        serverConfig = new ServerConfig();
        
        if (serverConfig.loadIniFile()) {
            port = serverConfig.getPort();
            connectionLimit = serverConfig.getConnectionLimit();
            logPath = serverConfig.getLogPath();
        } else {
            return;
        }

        // Abre arquivo de Log.
        try {

            // Define arquivo de Log e PrintWriter para gravar o mesmo.
            logFile = new File(logPath + createLogFileName());
            pwLogFile = new PrintWriter(new FileWriter(logFile), true);

            // Registra Log.
            addToLog("LOG FILE STARTED AT: " + logFile.getAbsolutePath());

        } catch (IOException ex) {
            javax.swing.JOptionPane.showMessageDialog(null, ex.getMessage());
        }

        // Inicia variaveis.
        this.started = false;
        this.connectionList = new ArrayList<>();

        // Inicia TrayIcon.
        SystemTray.initSystemTraySwing(this);

        // Start Server.
        startServer();
    }

    // GETTERS e SETTERS.
    public Socket getConnection() {
        return connection;
    }

    public void setConnection(Socket connection) {
        this.connection = connection;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public ServerManagerConnection getServerManagerConnection() {
        return serverManagerConnection;
    }

    public void setServerManagerConnection(ServerManagerConnection serverManagerConnection) {
        this.serverManagerConnection = serverManagerConnection;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public List<ConnectionManager> getConnectionList() {
        return connectionList;
    }

    public void setConnectionList(List<ConnectionManager> connectionList) {
        this.connectionList = connectionList;
    }

    public List<Console> getConsoleList() {
        return consoleList;
    }

    public void setConsoleList(List<Console> consoleList) {
        this.consoleList = consoleList;
    }

    public File getLogFile() {
        return logFile;
    }

    public void setLogFile(File logFile) {
        this.logFile = logFile;
    }

    public PrintWriter getPwLogFile() {
        return pwLogFile;
    }

    public void setPwLogFile(PrintWriter pwLogFile) {
        this.pwLogFile = pwLogFile;
    }

    public Object getKeyToReadLog() {
        return keyToReadLog;
    }

    public void setKeyToReadLog(Object keyToReadLog) {
        this.keyToReadLog = keyToReadLog;
    }

    // Inicia servidor.
    public final void startServer() {

        try {

            if (!isStarted()) {

                serverSocket = new ServerSocket(port);
                serverManagerConnection = new ServerManagerConnection(this);
                new Thread(serverManagerConnection).start();
                addToLog("SOCKETSERVER STARTED IN DOOR: " + String.valueOf(port));

                setStarted(true);
                addToLog("SERVER STARTED WITH SUCCESS!");
            }

        } catch (IOException ex) {
            addToLog(ex.getMessage().toUpperCase());
        }

    }

    // Para servidor.
    public void stopServer() {

        if (isStarted()) {

            addToLog("FINISHING SERVER IN DOOR: " + String.valueOf(port));

            try {

                while (connectionList.size() > 0) {

                    // Fecha conexao.
                    connectionList.get(0).getConnection().close();
                    connectionList.get(0).setStopThreads(true);
                    connectionList.remove(0);

                    // Registra Log.
                    addToLog("CLOSING CONNECTION: " + connectionList.get(0).getConnection().toString());
                    addToLog(connectionList.get(0).getConnection().getInetAddress().getHostName() + "CLOSED.");
                }

                if (!serverSocket.isClosed()) {

                    // Fecha SocketServer.
                    serverSocket.close();

                    // Registra Log.
                    addToLog("SOCKETSERVER IN DOOR " + String.valueOf(port) + " CLOSED.");
                }

                setStarted(false);

                addToLog("STOPPED WITH SUCCESS.");

            } catch (IOException ex) {
                addToLog(ex.getMessage().toUpperCase());
            }
        }
    }

    public void addToLog(String log) {

        log = "[" + JASDateHour.currentDate() + "-" + JASDateHour.currentHour() + "] " + log;

        pwLogFile.println(log);

        System.out.println(log);

        synchronized (keyToReadLog) {
            keyToReadLog.notifyAll();
        }

    }

    private String createLogFileName() {

        String name = "SocketServer"
                + "_"
                + JASDateHour.getDateInFormat("yyyymmdd")
                + "_"
                + JASDateHour.getHourInFormat("HHmmss")
                + ".log";

        return name;
    }
}
