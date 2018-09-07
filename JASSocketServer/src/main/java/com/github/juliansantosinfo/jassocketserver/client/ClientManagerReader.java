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
import javax.swing.JOptionPane;

/**
 *
 * @author Julian A. Santos
 */
public class ClientManagerReader extends Thread {

    private Client client;
    private ConnectionManager connectionManager;
    private String messageInput;
    private Message message;
    private Gson gson = new GsonBuilder().create();

    public ClientManagerReader(Client client) {
        this.client = client;
        client.setStopThreads(false);
    }

    // -------------------------------------------------------------------------
    // METHODS GET AND SET
    // -------------------------------------------------------------------------
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    // -------------------------------------------------------------------------
    // METHODS GET AND SET
    // -------------------------------------------------------------------------
    /**
     * Start thread for instance of the class.
     */
    @Override
    public void run() {

        while (!client.isStopThreads()) {

            messageInput = "";

            try {

                while (client.getDataInputStream().available() > 0) {
                    messageInput = client.getDataInputStream().readUTF();
                }

                if (!messageInput.isEmpty()) {

                    message = gson.fromJson(messageInput, Message.class);
                    messageInput = "["
                            + message.getDate()
                            + " - "
                            + message.getHour()
                            + "] "
                            + "\n"
                            + message.getMessage()
                            + "\n";
                    client.getClientUI().getjTextArea().append(messageInput);

                }

                Thread.sleep(100);

            } catch (IOException ex) {
                client.setStopThreads(true);
            } catch (InterruptedException ex) {
                Logger.getLogger(ClientManagerReader.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
}
