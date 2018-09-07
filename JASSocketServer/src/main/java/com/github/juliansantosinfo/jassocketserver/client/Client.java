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
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
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
     */
    public Client() {
    }

    /**
     * * Contructor for instance class Client.
     *
     * @param clientUI
     */
    public Client(ClientUI clientUI) {
        this.clientUI = clientUI;
    }

    /**
     * Get connection socket from the client.
     *
     * @return connection socket.
     */
    public Socket getConnection() {
        return connection;
    }

    /**
     * Set connection socket for client.
     *
     * @param connection
     */
    public void setConnection(Socket connection) {
        this.connection = connection;
    }

    /**
     * Get DataInputStream instance from the socket client.
     *
     * @return datainputstream.
     */
    public DataInputStream getDataInputStream() {
        return dataInputStream;
    }

    /**
     * Set DataInputStream for socket client.
     *
     * @param dataInputStream
     */
    public void setDataInputStream(DataInputStream dataInputStream) {
        this.dataInputStream = dataInputStream;
    }

    /**
     * Get DataOutputStream instance from the socket client.
     *
     * @return DataOutputStream.
     */
    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    /**
     * Set DataInputStream for socket client.
     *
     * @param dataOutputStream
     */
    public void setDataOutputStream(DataOutputStream dataOutputStream) {
        this.dataOutputStream = dataOutputStream;
    }

    /**
     * Get Message instance from the client.
     *
     * @return message object.
     */
    public Message getMessage() {
        return message;
    }

    /**
     * Set Message objeto for client.
     *
     * @param message
     */
    public void setMessage(Message message) {
        this.message = message;
    }

    /**
     * Get string with client output messagen.
     *
     * @return output message.
     */
    public String getMessageOutput() {
        return messageOutput;
    }

    /**
     * Set string with client output messagen.
     *
     * @param messageOutput
     */
    public void setMessageOutput(String messageOutput) {
        this.messageOutput = messageOutput;
    }

    /**
     * Get JFrame with client user interface.
     *
     * @return JFrame.
     */
    public ClientUI getClientUI() {
        return clientUI;
    }

    /**
     * Set JFrame for client user interface.
     *
     * @param clientUI
     */
    public void setClientUI(ClientUI clientUI) {
        this.clientUI = clientUI;
    }

    /**
     * Gets status of the variable that controls the stop of the threads.
     *
     * @return stopThreads.
     */
    public boolean isStopThreads() {
        return stopThreads;
    }

    /**
     * Set status of the variable that controls the stop of the threads.
     *
     * @param stopThreads
     */
    public void setStopThreads(boolean stopThreads) {
        this.stopThreads = stopThreads;
    }

    /**
     * Get ClientManagerReader instance responsible for reading the messages
     * received by the socket.
     *
     * @return ClientManagerReader.
     */
    public ClientManagerReader getCmr() {
        return cmr;
    }

    /**
     * Set ClientManagerReader instance responsible for reading the messages
     * received by the socket.
     *
     * @param cmr
     */
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
                dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataInputStream = new DataInputStream(connection.getInputStream());

                // Initialize thread for ClientManagerReader.
                cmr = new ClientManagerReader(this);
                Thread tCMR = new Thread(cmr);
                tCMR.start();

                // Defines window behavior when connected to the server.
                if (clientUI != null) {
                    clientUI.formConnected();
                }

            } else {

                // Set inputStream and outputStream.
                dataOutputStream = null;
                dataInputStream = null;

                // Defines window behavior when disconnected to the server.
                if (clientUI != null) {
                    clientUI.formDisconnected();
                }
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
            dataOutputStream.close();
            dataInputStream.close();
            connection.close();
            connectionStatus = false;
            stopThreads = true;
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Set inputStream and outputStream.
        dataOutputStream = null;
        dataInputStream = null;

        // Defines window behavior when disconnected to the server.
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
            dataOutputStream.writeUTF(messageText);
            dataOutputStream.flush();

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
