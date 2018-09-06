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

/**
 *
 * @author Julian A. Santos
 */
public class MessageProcessManager implements Runnable {

    private boolean stopped = false;
    private ConnectionManager connectionManager;
    private String messageProcess;
    private Message message;
    private Gson gson;

    public MessageProcessManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
        this.gson = new GsonBuilder().create();
    }

    @Override
    public void run() {

        while (!connectionManager.isStopThreads()) {

            while (connectionManager.existMessageInputList()) {

                message = connectionManager.nextMessageInputList();

                messageProcess = gson.toJson(message);

                connectionManager.addMessageOutputList(message);
                connectionManager.removeMessageInputList();

                synchronized (connectionManager.getKeyOutputList()) {
                    connectionManager.getKeyOutputList().notifyAll();
                }

            }

            synchronized (connectionManager.getKeyInputList()) {
                try {
                    connectionManager.getKeyInputList().wait();
                } catch (InterruptedException ex) {
                    System.out.println("THREAD MMW INTERROMPIDA: " + ex.getMessage());
                }
            }
        }

    }
}
