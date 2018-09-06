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
package com.github.juliansantosinfo.jassocketserver.client;

import com.github.juliansantosinfo.jassocketserver.message.*;
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
public class ClientManagerReader implements Runnable {

    private Client client;
    private ConnectionManager connectionManager;
    private DataInputStream dataInputStream;
    private String messageInput;
    private Message message;
    private Gson gson;

    public ClientManagerReader(Client client) {
        this.client = client;
        dataInputStream = client.getDis();
        gson = new GsonBuilder().create();
    }
    
    /**
     * Start thread for instance of the class.
     */
    @Override
    public void run() {

        while (!client.isStopThreads()) {

            messageInput = "";

            try {
                while (dataInputStream.available() > 0) {
                    messageInput = dataInputStream.readUTF();
                }
                System.out.println("ENTRE");
                if (!messageInput.isEmpty()) {

                    message = gson.fromJson(messageInput, Message.class);
                    client.getClientUI().getjTextArea().append(message.getMessage());

                }

            } catch (IOException ex) {
                Logger.getLogger(ClientManagerReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
