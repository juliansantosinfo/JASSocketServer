/*
 * Copyright (C) 2018 Julian
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
package com.github.juliansantosinfo.jassocketserver.client;

import com.github.juliansantosinfo.jassocketserver.message.Message;
import com.github.juliansantosinfo.jassocketserver.ui.ClientUI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Julian
 */
public class Client implements Runnable {

    private Socket connection;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Message message;
    private String messageOutput;
    private ClientUI clientUI;
    private ClientManagerReader cmr;
    private boolean connectionStatus = false;
    private boolean stopThreads = false;

    public final Object keyToRead = new Object();
    public final Object keyToWrite = new Object();

    /**
     * Contructor for instance class Client.
     *
     * @param clientUI
     */
    public Client(ClientUI clientUI) {
        this.clientUI = clientUI;
    }

    public Socket getConnection() {
        return connection;
    }

    public void setConnection(Socket connection) {
        this.connection = connection;
    }

    public DataInputStream getDis() {
        return dis;
    }

    public void setDis(DataInputStream dis) {
        this.dis = dis;
    }

    public DataOutputStream getDos() {
        return dos;
    }

    public void setDos(DataOutputStream dos) {
        this.dos = dos;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getMessageOutput() {
        return messageOutput;
    }

    public void setMessageOutput(String messageOutput) {
        this.messageOutput = messageOutput;
    }

    public ClientUI getClientUI() {
        return clientUI;
    }

    public void setClientUI(ClientUI clientUI) {
        this.clientUI = clientUI;
    }

    public boolean isStopThreads() {
        return stopThreads;
    }

    public void setStopThreads(boolean stopThreads) {
        this.stopThreads = stopThreads;
    }

    public ClientManagerReader getCmr() {
        return cmr;
    }

    public void setCmr(ClientManagerReader cmr) {
        this.cmr = cmr;
    }

    /**
     * Start thread for instance of the class.
     */
    @Override
    public void run() {
    }

    /**
     * Create connection to server.
     *
     * @param host server address
     * @param port server port
     * @return
     */
    public boolean connect(String host, Integer port) {

        try {

            // Start connection in server.
            connection = new Socket(host, port);

            if (connection.isConnected()) {
                // Wait return of the server.
                connectionStatus = new DataInputStream(connection.getInputStream()).readBoolean();
            }

            if (connectionStatus) {

                // Set inputStream and outputStream.
                dos = new DataOutputStream(connection.getOutputStream());
                dis = new DataInputStream(connection.getInputStream());

                // Initialize thread for ClientManagerReader.
                cmr = new ClientManagerReader(this);
                Thread tCMR = new Thread(cmr);
                tCMR.start();

                // Defines window behavior when connected to the server.
                clientUI.formConnected();

            } else {

                // Set inputStream and outputStream.
                dos = null;
                dis = null;

                // Defines window behavior when disconnected to the server.
                clientUI.formConnected();
            }

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        return connectionStatus;

    }

    /**
     * Close connection to server.
     *
     */
    public void disconnect() {

        try {
            connection.shutdownInput();
            connection.shutdownOutput();
            dos.close();
            dis.close();
            connection.close();
            connectionStatus = false;
            stopThreads = true;
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        clientUI.formDisconnected();
    }

    /**
     * Send message to server.
     *
     * @param messageText
     */
    public void sendToServer(String messageText) {

        try {

            // Make object message.
            message = new Message(0, messageText);
            message.read();

            // Transform message em jsan.
            Gson gson = new GsonBuilder().create();
            messageText = gson.toJson(message);

            // Sendo to server.
            dos.writeUTF(messageText);
            dos.flush();

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
