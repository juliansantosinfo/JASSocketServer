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
package com.github.juliansantosinfo.jassocketserver.message;

import com.github.juliansantosinfo.jassocketserver.connection.ConnectionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Julian A. Santos
 */
public class MessageManagerReader implements Runnable {

    private boolean stopped = false;
    private ConnectionManager connectionManager;
    private DataInputStream dataInputStream;
    private String messageInput;
    private Message message;
    private Gson gson;

    public MessageManagerReader() {
    }

    public MessageManagerReader(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.dataInputStream = connectionManager.getDataInputStream();
        this.gson = new GsonBuilder().create();
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    @Override
    public void run() {

        messageInput = "";

        while (!connectionManager.isStopThreads()) {

            try {
                while (dataInputStream.available() > 0) {
                    messageInput = dataInputStream.readUTF();
                }

                if (!messageInput.isEmpty()) {
                    
                    message = gson.fromJson(messageInput, Message.class);
                    connectionManager.addMessageInputList(message);
                    messageInput = "";
                    synchronized (connectionManager.getKeyInputList()) {
                        connectionManager.getKeyInputList().notifyAll();
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(MessageManagerReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
