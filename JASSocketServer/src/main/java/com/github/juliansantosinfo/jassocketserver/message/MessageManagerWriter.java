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
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Julian A. Santos
 */
public class MessageManagerWriter implements Runnable {

    private boolean stopped = false;
    private ConnectionManager connectionManager;
    private DataOutputStream dataOutputStream;
    private String messageOutput;
    private Message message;
    private Gson gson;

    public MessageManagerWriter() {
    }

    public MessageManagerWriter(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.dataOutputStream = connectionManager.getDataOutputStream();
        this.gson = new GsonBuilder().create();
    }

    public boolean isStopped() {
        return stopped;
    }

    public void setStopped(boolean isStopped) {
        this.stopped = isStopped;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public void setConnectionManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }

    public void setDataOutputStream(DataOutputStream dataOutputStream) {
        this.dataOutputStream = dataOutputStream;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public void run() {

        while (!connectionManager.isStopThreads()) {

            while (connectionManager.existMessageOutputList()) {

                message = connectionManager.nextMessageOutputList();

                try {

                    messageOutput = gson.toJson(message);

                    dataOutputStream.writeUTF(messageOutput);
                    dataOutputStream.flush();

                    connectionManager.removeMessageOutputList();

                } catch (IOException ex) {
                    System.out.println("CONEXAO COM SERVIDOR INTERROMPIDA: " + ex.getMessage());
                    connectionManager.setStopThreads(true);
                }
            }

            synchronized (connectionManager.getKeyOutputList()) {
                try {
                    connectionManager.getKeyOutputList().wait();
                } catch (InterruptedException ex) {
                    System.out.println("THREAD MMW INTERROMPIDA: " + ex.getMessage());
                }
            }

        }
    }

}
